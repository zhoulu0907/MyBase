package com.cmsr.onebase.module.app.build.service.menu;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.service.resource.PageSetService;
import com.cmsr.onebase.module.app.build.vo.menu.*;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dto.appresource.CopyPageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetDTO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.menu.BpmMenuEnum;
import com.cmsr.onebase.module.app.core.enums.menu.MenuTypeEnum;
import com.cmsr.onebase.module.app.core.utils.MenuUtils;
import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
@Setter
@Service
@Validated
public class BuildAppMenuServiceImpl implements BuildAppMenuService {

    @Autowired
    private AppCommonService appCommonService;

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private AppPageSetRepository appPageSetRepository;

    @Autowired
    private PageSetService pageSetService;

    @Autowired
    private AppAuthPermissionRepository authPermissionRepository;

    @Autowired
    private AppAuthFieldRepository authFieldRepository;

    @Autowired
    private AppAuthDataGroupRepository authDataGroupRepository;

    @Override
    public List<MenuListRespVO> listBpmApplicationMenu(Long applicationId) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(applicationId);
        List<AppMenuDO> menuDOS = appMenuRepository.findByApplicationIdAndType(applicationDO.getId(),
                Set.of(MenuTypeEnum.BPM.getValue())
        );
        // 返回菜单
        return menuDOS.stream()
                .map(v -> BeanUtils.toBean(v, MenuListRespVO.class))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void createDefaultBpmMenu(Long applicationId) {
        List<AppMenuDO> menuDOList = new ArrayList<>();
        int menuSort = 0;

        for (BpmMenuEnum bpmMenuEnum : BpmMenuEnum.values()) {
            AppMenuDO menuDO = new AppMenuDO();
            menuDO.setApplicationId(applicationId);
            menuDO.setMenuUuid(UuidUtils.getUuid());
            menuDO.setParentUuid(MenuUtils.ROOT_MENU_UUID);
            menuDO.setMenuCode(bpmMenuEnum.getCode());
            menuDO.setMenuSort(menuSort++);
            menuDO.setMenuType(MenuTypeEnum.BPM.getValue());
            menuDO.setMenuName(bpmMenuEnum.getText());
            menuDO.setMenuIcon("icon-folder");
            menuDO.setIsVisible(0);

            menuDOList.add(menuDO);
        }

        appMenuRepository.saveBatch(menuDOList);
    }

    @Override
    public List<MenuListRespVO> listApplicationMenu(Long applicationId, String name) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(applicationId);
        List<AppMenuDO> menuDOS = appMenuRepository.findByApplicationIdAndType(applicationDO.getId(),
                Set.of(MenuTypeEnum.PAGE.getValue(), MenuTypeEnum.GROUP.getValue())
        );
        if (CollectionUtils.isEmpty(menuDOS)) {
            return Collections.emptyList();
        }
        List<MenuListRespVO> menuListRespList = BeanUtils.toBean(menuDOS, MenuListRespVO.class);
        enrichPagesetType(menuListRespList);

        // 把第一层的菜单添加到列表中
        LinkedList<MenuListRespVO> levelOneMenus = menuListRespList.stream()
                .filter(v -> MenuUtils.ROOT_MENU_UUID.equals(v.getParentUuid()))
                .collect(Collectors.toCollection(LinkedList::new));
        // 递归实现每个菜单的子菜单
        for (MenuListRespVO respVO : levelOneMenus) {
            LinkedList<MenuListRespVO> children = recursiveGetChildren(respVO.getMenuUuid(), menuListRespList);
            respVO.setChildren(children);
        }
        filterMenuByName(levelOneMenus, name);
        return levelOneMenus;
    }


    private LinkedList<MenuListRespVO> recursiveGetChildren(String parentUuid, List<MenuListRespVO> listRespVOS) {
        LinkedList<MenuListRespVO> children = new LinkedList<>();
        for (MenuListRespVO respVO : listRespVOS) {
            if (Objects.equals(respVO.getParentUuid(), parentUuid)) {
                // 只有父菜单的uuid等于当前菜单的父菜单的uuid时，才添加子菜单，继续递归
                children.add(respVO);
                respVO.setChildren(recursiveGetChildren(respVO.getMenuUuid(), listRespVOS));
            }
        }
        return children.isEmpty() ? null : children;
    }

    private void enrichPagesetType(List<MenuListRespVO> menuListRespList) {
        List<String> menuUuids = menuListRespList.stream().map(MenuListRespVO::getMenuUuid).collect(Collectors.toList());
        Long applicationId = ApplicationManager.getApplicationId();
        List<AppResourcePagesetDO> pagesets = appPageSetRepository.findByMenuUuids(applicationId, menuUuids);
        if (CollectionUtils.isEmpty(menuUuids)) {
            return;
        }
        Map<String, Integer> pagesetTypeMap = pagesets.stream()
                .filter(p -> p.getMenuUuid() != null && p.getPageSetType() != null)
                .collect(Collectors.toMap(AppResourcePagesetDO::getMenuUuid, AppResourcePagesetDO::getPageSetType, (v1, v2) -> v1));
        for (MenuListRespVO menuListRespVO : menuListRespList) {
            menuListRespVO.setPagesetType(pagesetTypeMap.get(menuListRespVO.getMenuUuid()));
        }
    }

    /**
     * 根据名称过滤菜单
     * menuType 1 是页面 2是目录
     * 过滤规则为：
     * 如果 菜单名称包含name，则此菜单及其父级目录都要展示
     * 如果 目录名称包含name，则此目录及其子菜单都要展示
     *
     * @param menuListRespList
     * @param name
     * @return
     */
    private void filterMenuByName(LinkedList<MenuListRespVO> menuListRespList, String name) {
        // 如果没有过滤条件，直接返回原列表
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        // 第一步：根据规则设置 filter 标记
        markMenusForFilter(menuListRespList, name);
        // 第二步：移除不符合条件的菜单
        removeUnmarkedMenus(menuListRespList);
    }

    /**
     * 根据过滤规则标记菜单
     *
     * @param menuList 菜单列表
     * @param name     过滤条件
     */
    private void markMenusForFilter(List<MenuListRespVO> menuList, String name) {
        for (MenuListRespVO menu : menuList) {
            if (StringUtils.containsIgnoreCase(menu.getMenuName(), name)) {
                menu.setFilter(true);
            } else if (MenuTypeEnum.isGroup(menu.getMenuType()) && anyChildrenMatches(menu.getChildren(), name)) {
                menu.setFilter(true);
            }
            if (menu.isFilter() && menu.getChildren() != null) {
                markAllChildren(menu.getChildren());
            }
            // 递归处理子菜单
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                markMenusForFilter(menu.getChildren(), name);
            }
        }
    }

    private boolean anyChildrenMatches(List<MenuListRespVO> children, String name) {
        if (children == null) {
            return false;
        }
        for (MenuListRespVO child : children) {
            if (StringUtils.containsIgnoreCase(child.getMenuName(), name)) {
                return true;
            }
            if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                if (anyChildrenMatches(child.getChildren(), name)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 标记所有子菜单
     *
     * @param children 子菜单列表
     */
    private void markAllChildren(List<MenuListRespVO> children) {
        for (MenuListRespVO child : children) {
            child.setFilter(true);
            if (child.getChildren() != null) {
                markAllChildren(child.getChildren());
            }
        }
    }


    /**
     * 移除未标记的菜单
     *
     * @param menuList 菜单列表
     */
    private void removeUnmarkedMenus(LinkedList<MenuListRespVO> menuList) {
        // 使用迭代器安全地删除元素
        Iterator<MenuListRespVO> iterator = menuList.iterator();
        while (iterator.hasNext()) {
            MenuListRespVO menu = iterator.next();
            if (menu.isFilter()) {
                // 递归处理子菜单
                if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                    removeUnmarkedMenus(menu.getChildren());
                    // 如果子菜单全部被删除，设置为null
                    if (menu.getChildren().isEmpty()) {
                        menu.setChildren(null);
                    }
                }
            } else {
                // 删除未标记的菜单
                iterator.remove();
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuCreateRespVO createApplicationMenu(MenuCreateReqVO createReqVO) {
        // 菜单类型校验
        MenuTypeEnum.validate(createReqVO.getMenuType());
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        // 创建菜单
        AppMenuDO menuDO = new AppMenuDO();
        menuDO.setMenuUuid(UuidUtils.getUuid());
        menuDO.setApplicationId(applicationDO.getId());
        menuDO.setParentUuid(validateParentMenuId(createReqVO.getParentId()));
        menuDO.setMenuCode(MenuUtils.generateMenuCode());
        menuDO.setMenuType(createReqVO.getMenuType());
        menuDO.setMenuName(createReqVO.getMenuName());
        menuDO.setMenuIcon(createReqVO.getMenuIcon());
        menuDO.setMenuSort(generateMenuSort(applicationDO.getId()));
        menuDO.setIsVisible(NumberUtils.INTEGER_ONE);
        menuDO.setEntityUuid(createReqVO.getEntityUuid());
        appMenuRepository.save(menuDO);
        // 创建页面集
        CreatePageSetDTO createPageSetDTO = new CreatePageSetDTO();
        createPageSetDTO.setApplicationId(applicationDO.getId());
        createPageSetDTO.setMenuId(menuDO.getId());
        createPageSetDTO.setPageSetType(createReqVO.getPageSetType());
        createPageSetDTO.setPageSetName(menuDO.getMenuName());
        createPageSetDTO.setDisplayName(menuDO.getMenuName());
        createPageSetDTO.setMainMetadata(String.valueOf(createReqVO.getEntityUuid()));
        pageSetService.createPageSet(createPageSetDTO);
        // 返回结果
        MenuCreateRespVO menuCreateRespVO = BeanUtils.toBean(menuDO, MenuCreateRespVO.class);
        return menuCreateRespVO;
    }

    private Integer generateMenuSort(Long applicationId) {
        return appMenuRepository.countByApplicationId(applicationId) + 1;
    }

    private String validateParentMenuId(Long parentId) {
        if (parentId == null) {
            return MenuUtils.ROOT_MENU_UUID;
        }
        AppMenuDO parentMenu = appCommonService.validateMenuExist(parentId);
        if (parentMenu == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_NOT_EXIST);
        }
        if (MenuTypeEnum.isPage(parentMenu.getMenuType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_TYPE_ERROR);
        }
        return parentMenu.getMenuUuid();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationMenu(MenuUpdateReqVO updateReqVO) {
        AppMenuDO menuDO = appCommonService.validateMenuExist(updateReqVO.getId());
        menuDO.setMenuName(updateReqVO.getMenuName());
        menuDO.setMenuIcon(updateReqVO.getMenuIcon());
        appMenuRepository.updateById(menuDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationMenuName(Long id, String menuName) {
        AppMenuDO menuDO = appCommonService.validateMenuExist(id);
        menuDO.setMenuName(menuName);
        appMenuRepository.updateById(menuDO);
    }

    @Override
    public void updateApplicationMenuOrder(MenuOrderUpdateReqVO updateReqVO) {
        AppMenuDO menuDO = appCommonService.validateMenuExist(updateReqVO.getId());
        menuDO.setParentUuid(validateParentMenuId(updateReqVO.getParentId()));
        appMenuRepository.updateById(menuDO);
        Map<Long, Integer> menuSortMap = toMenuSortMap(updateReqVO.getMenuTree());
        List<AppMenuDO> menuDOS = appMenuRepository.findByApplicationId(menuDO.getApplicationId());
        for (AppMenuDO menu : menuDOS) {
            Integer order = MapUtils.getInteger(menuSortMap, menu.getId(), MenuUtils.MENU_SORT_MAX_VALUE);
            menu.setMenuSort(order);
            appMenuRepository.updateById(menu);
        }
    }

    /**
     * 输入的是树结构，需要转换成Map结构。
     * 返回 Map的Key是菜单的ID，Value是菜单顺序，根据菜单的深度路径查找排序。
     *
     * @return
     */
    private Map<Long, Integer> toMenuSortMap(List<MenuOrderUpdateReqVO.MenuOrderNode> menuList) {
        Map<Long, Integer> menuSortMap = new HashMap<>(menuList.size());
        AtomicInteger sortOrder = new AtomicInteger(1);
        recursiveBuildMenuSortMap(menuList, menuSortMap, sortOrder);
        return menuSortMap;
    }

    /**
     * 递归构建菜单排序映射
     *
     * @param menus       当前层级的菜单列表
     * @param menuSortMap 菜单排序映射结果
     * @param sortOrder   当前排序序号
     */
    private void recursiveBuildMenuSortMap(List<MenuOrderUpdateReqVO.MenuOrderNode> menus,
                                           Map<Long, Integer> menuSortMap, AtomicInteger sortOrder) {
        if (menus == null || menus.isEmpty()) {
            return;
        }
        for (MenuOrderUpdateReqVO.MenuOrderNode menu : menus) {
            // 按照深度优先遍历的顺序分配序号
            menuSortMap.put(menu.getId(), sortOrder.getAndIncrement());
            // 递归处理子菜单
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                recursiveBuildMenuSortMap(menu.getChildren(), menuSortMap, sortOrder);
            }
        }
    }

    @Override
    public void updateApplicationMenuVisible(Long id, Integer visible) {
        AppMenuDO menuDO = appCommonService.validateMenuExist(id);
        menuDO.setIsVisible(visible);
        appMenuRepository.updateById(menuDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuCreateRespVO copyApplicationMenu(MenuCopyReqVO copyReqVO) {
        AppMenuDO menuDO = appCommonService.validateMenuExist(copyReqVO.getId());
        if (menuDO.getMenuType() == MenuTypeEnum.GROUP.getValue()) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_NOT_ALLOW_COPY);
        }
        Long sourceMenuId = menuDO.getId();
        // 复制菜单
        menuDO.setId(null);
        menuDO.setMenuUuid(UuidUtils.getUuid());
        menuDO.setMenuName(copyReqVO.getMenuName());
        menuDO.setParentUuid(validateParentMenuId(copyReqVO.getParentId()));
        menuDO.setMenuCode(MenuUtils.generateMenuCode());
        appMenuRepository.save(menuDO);
        // 复制页面
        CopyPageSetDTO copyPageSetDTO = new CopyPageSetDTO();
        copyPageSetDTO.setMenuId(sourceMenuId);
        copyPageSetDTO.setNewMenuId(menuDO.getId());
        pageSetService.copyPageSet(copyPageSetDTO);
        //
        MenuCreateRespVO menuCreateRespVO = BeanUtils.toBean(menuDO, MenuCreateRespVO.class);
        return menuCreateRespVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplicationMenu(Long id) {
        AppMenuDO menuDO = appCommonService.validateMenuExist(id);
        if (menuDO.getMenuType() == MenuTypeEnum.GROUP.getValue()
                && validateMenuGroupHasChildren(menuDO.getApplicationId(), menuDO.getMenuUuid())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_HAS_CHILDREN);
        }
        // 删除页面
        pageSetService.deletePageSetByMenu(menuDO);
        Long applicationId = menuDO.getApplicationId();
        authPermissionRepository.deleteByMenuUuid(applicationId, menuDO.getMenuUuid());
        authFieldRepository.deleteByMenuUuid(applicationId, menuDO.getMenuUuid());
        authDataGroupRepository.deleteByMenuUuid(applicationId, menuDO.getMenuUuid());
        // 删除菜单
        appMenuRepository.removeById(id);
    }

    private boolean validateMenuGroupHasChildren(Long applicationId, String menuUuid) {
        return appMenuRepository.countByParentId(applicationId, menuUuid) > 0;
    }

}
