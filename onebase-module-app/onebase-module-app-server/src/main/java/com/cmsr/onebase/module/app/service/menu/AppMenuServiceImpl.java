package com.cmsr.onebase.module.app.service.menu;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CopyPageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.controller.admin.menu.vo.*;
import com.cmsr.onebase.module.app.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.menu.MenuTypeEnum;
import com.cmsr.onebase.module.app.enums.menu.MenuVisibleEnum;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.service.app.AppApplicationService;
import com.cmsr.onebase.module.app.service.appresource.PageSetService;
import com.cmsr.onebase.module.app.util.MenuUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
@Setter
@Service
@Validated
public class AppMenuServiceImpl implements AppMenuService {

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Resource
    private PageSetService pageSetService;

    @Override
    public List<MenuListRespVO> listApplicationMenu(Long applicationId) {
        List<MenuDO> menuDOS = appMenuRepository.findByApplicationId(applicationId);
        List<MenuListRespVO> menuListRespList = new ArrayList<>();
        // 把第一层的菜单添加到列表中
        List<MenuListRespVO> levelOneMenus = menuDOS.stream()
                .filter(v -> Objects.equals(v.getParentId(), MenuUtils.ROOT_MENU_ID))
                .map(v -> BeanUtils.toBean(v, MenuListRespVO.class))
                .toList();
        menuListRespList.addAll(levelOneMenus);
        //递归实现每个菜单的子菜单
        for (MenuListRespVO respVO : menuListRespList) {
            List<MenuListRespVO> children = recursiveGetChildren(respVO, menuDOS);
            respVO.setChildren(children);
        }
        return menuListRespList;
    }

    private List<MenuListRespVO> recursiveGetChildren(MenuListRespVO parent, List<MenuDO> menuDOS) {
        List<MenuListRespVO> children = new ArrayList<>();
        for (MenuDO menuDO : menuDOS) {
            if (Objects.equals(menuDO.getParentId(), parent.getId())) {
                // 只有父菜单的uuid等于当前菜单的父菜单的uuid时，才添加子菜单，继续递归
                MenuListRespVO child = BeanUtils.toBean(menuDO, MenuListRespVO.class);
                child.setChildren(recursiveGetChildren(child, menuDOS));
                children.add(child);
            }
        }
        return children.isEmpty() ? null : children;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuCreateRespVO createApplicationMenu(MenuCreateReqVO createReqVO) {
        // 菜单类型校验
        MenuTypeEnum.validate(createReqVO.getMenuType());
        appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        // 创建菜单
        MenuDO menuDO = new MenuDO();
        menuDO.setApplicationId(createReqVO.getApplicationId());
        menuDO.setParentId(parseParentMenuId(createReqVO));
        menuDO.setMenuCode(MenuUtils.generateMenuCode());
        menuDO.setMenuType(createReqVO.getMenuType());
        menuDO.setMenuName(createReqVO.getMenuName());
        menuDO.setMenuIcon(createReqVO.getMenuIcon());
        menuDO.setMenuSort(generateMenuSort(createReqVO.getApplicationId()));
        menuDO.setVisible(MenuVisibleEnum.YES.getValue());
        appMenuRepository.insert(menuDO);
        // 创建页面集
        CreatePageSetDTO createPageSetDTO = new CreatePageSetDTO();
        createPageSetDTO.setMenuId(menuDO.getId());
        createPageSetDTO.setPageSetName(menuDO.getMenuName());
        createPageSetDTO.setDisplayName(menuDO.getMenuName());
        pageSetService.createPageSet(createPageSetDTO);
        // 返回结果
        MenuCreateRespVO menuCreateRespVO = BeanUtils.toBean(menuDO, MenuCreateRespVO.class);
        return menuCreateRespVO;
    }

    private Integer generateMenuSort(Long applicationId) {
        return appMenuRepository.countByApplicationId(applicationId) + 1;
    }

    private Long parseParentMenuId(MenuCreateReqVO createReqVO) {
        if (createReqVO.getParentId() == null || createReqVO.getParentId() == MenuUtils.ROOT_MENU_ID) {
            return MenuUtils.ROOT_MENU_ID;
        }
        MenuDO parentMenu = appCommonService.validateMenuExist(createReqVO.getParentId());
        if (parentMenu == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_NOT_EXIST);
        }
        if (!MenuTypeEnum.isGroup(parentMenu.getMenuType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_TYPE_ERROR);
        }
        return createReqVO.getParentId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationMenuName(Long id, String menuName) {
        MenuDO menuDO = appCommonService.validateMenuExist(id);
        menuDO.setMenuName(menuName);
        appMenuRepository.update(menuDO);
    }

    @Override
    public void updateApplicationMenuOrder(MenuOrderUpdateReqVO updateReqVO) {
        MenuDO menuDO = appCommonService.validateMenuExist(updateReqVO.getId());
        menuDO.setParentId(updateReqVO.getId());
        appMenuRepository.update(menuDO);
        Map<Long, Integer> menuSortMap = toMenuSortMap(updateReqVO.getMenuTree());
        List<MenuDO> menuDOS = appMenuRepository.findByApplicationId(menuDO.getApplicationId());
        for (MenuDO menu : menuDOS) {
            Integer order = MapUtils.getInteger(menuSortMap, menu.getId(), MenuUtils.MENU_SORT_MAX_VALUE);
            menu.setMenuSort(order);
            appMenuRepository.update(menu);
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
    private void recursiveBuildMenuSortMap(List<MenuOrderUpdateReqVO.MenuOrderNode> menus, Map<Long, Integer> menuSortMap, AtomicInteger sortOrder) {
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
    public void updateApplicationMenuVisible(Long id, Boolean visible) {
        MenuDO menuDO = appCommonService.validateMenuExist(id);
        menuDO.setVisible(visible);
        appMenuRepository.update(menuDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyApplicationMenu(MenuCopyReqVO copyReqVO) {
        MenuDO menuDO = appCommonService.validateMenuExist(copyReqVO.getId());
        if (menuDO.getMenuType() == MenuTypeEnum.GROUP.getValue()) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_NOT_ALLOW_COPY);
        }
        // 复制菜单
        menuDO.setId(null);
        menuDO.setMenuName(copyReqVO.getMenuName());
        menuDO.setParentId(copyReqVO.getParentId());
        menuDO.setMenuCode(MenuUtils.generateMenuCode());
        appMenuRepository.insert(menuDO);
        // 复制页面
        CopyPageSetDTO copyPageSetDTO = new CopyPageSetDTO();
        copyPageSetDTO.setMenuId(menuDO.getId());
        pageSetService.copyPageSet(copyPageSetDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplicationMenu(Long id) {
        MenuDO menuDO = appCommonService.validateMenuExist(id);
        if (menuDO.getMenuType() == MenuTypeEnum.GROUP.getValue()
                && validateMenuGroupHasChildren(menuDO.getId())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_HAS_CHILDREN);
        }
        // 删除菜单
        appMenuRepository.deleteById(id);
        // 删除页面
        pageSetService.deletePageSet(menuDO.getId());
    }

    private boolean validateMenuGroupHasChildren(Long id) {
        return appMenuRepository.countByParentId(id) > 0;
    }

}
