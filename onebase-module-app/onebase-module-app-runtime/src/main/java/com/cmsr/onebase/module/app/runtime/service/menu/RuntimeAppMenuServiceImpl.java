package com.cmsr.onebase.module.app.runtime.service.menu;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthViewRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthViewDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.enums.menu.MenuTypeEnum;
import com.cmsr.onebase.module.app.core.impl.auth.AppAuthSecurityApiImpl;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthRoleProvider;
import com.cmsr.onebase.module.app.core.utils.MenuUtils;
import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
@Setter
@Service
@Validated
public class RuntimeAppMenuServiceImpl implements RuntimeAppMenuService {

    @Resource
    private AppMenuRepository appMenuRepository;

    @Resource
    private AppPageSetRepository appPageSetRepository;

    @Autowired
    private AppAuthRoleProvider appAuthRoleProvider;

    @Resource
    private AppAuthSecurityApiImpl appAuthSecurityApi;

    @Autowired
    private AppAuthViewRepository appAuthViewRepository;

    @Autowired
    private AppPageRepository appPageRepository;

    @Override
    public List<MenuListRespVO> listBpmApplicationMenu() {
        Long applicationId = ApplicationManager.getApplicationId();
        // 获取应用下所有可见的BPM类型菜单
        List<AppMenuDO> menuDOS = appMenuRepository.findVisibleByAppIdAndType(applicationId,
                Set.of(MenuTypeEnum.BPM.getValue()));
        // 返回菜单
        return menuDOS.stream()
                .map(v -> BeanUtils.toBean(v, MenuListRespVO.class))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<MenuListRespVO> listApplicationMenu() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long applicationId = ApplicationManager.getRequiredApplicationId();
        //
        appAuthSecurityApi.cleanAuthCache(userId, applicationId);
        appAuthSecurityApi.loadAuthCache(userId, applicationId);
        //
        List<Long> menuIds = appAuthSecurityApi.getVisibleMenuIds(userId, applicationId);
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        List<AppMenuDO> menuDOS = appMenuRepository.listByIds(menuIds);
        if (CollectionUtils.isEmpty(menuDOS)) {
            return Collections.emptyList();
        }
        return convertToMenuListRespVOS(menuDOS);
    }

    private List<MenuListRespVO> convertToMenuListRespVOS(List<AppMenuDO> menuDOS) {
        List<MenuListRespVO> menuListRespList = BeanUtils.toBean(menuDOS, MenuListRespVO.class);
        enrichPagesetType(menuListRespList);
        // 把第一层的菜单添加到列表中
        List<MenuListRespVO> levelOneMenus = menuListRespList.stream()
                .filter(v -> MenuUtils.ROOT_MENU_UUID.equals(v.getParentUuid()))
                .toList();
        // 递归实现每个菜单的子菜单
        for (MenuListRespVO respVO : levelOneMenus) {
            List<MenuListRespVO> children = recursiveGetChildren(respVO.getMenuUuid(), menuListRespList);
            respVO.setChildren(children);
        }
        return levelOneMenus;
    }


    private List<MenuListRespVO> recursiveGetChildren(String parentUuid, List<MenuListRespVO> listRespVOS) {
        List<MenuListRespVO> children = new LinkedList<>();
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

    @Override
    public MenuPermissionVO getMenuPermission(Long menuId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long applicationId = ApplicationManager.getApplicationId();
        MenuPermissionVO menuPermissionVO = new MenuPermissionVO();
        menuPermissionVO.setOperationPermission(appAuthSecurityApi.getMenuOperationPermission(userId, applicationId, menuId));
        menuPermissionVO.setFieldPermission(appAuthSecurityApi.getMenuFieldPermission(userId, applicationId, menuId));
        menuPermissionVO.setViewUuids(findMenuViews(userId, applicationId, menuId));
        return menuPermissionVO;
    }

    /**
     * 要缓存
     */
    public Set<String> findMenuViews(Long userId, Long applicationId, Long menuId) {
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        if (userRoleDTO.isAdminRole()) {
            return findMenuAllViews(applicationId, menuId);
        }
        OperationPermission menuOperationPermission = appAuthSecurityApi.getMenuOperationPermission(userId, applicationId, menuId);
        if (menuOperationPermission.isAllFieldsAllowed()) {
            return findMenuAllViews(applicationId, menuId);
        }
        //
        Set<String> roleUuids = userRoleDTO.getRoleUuids();
        AppMenuDO menuDO = appMenuRepository.getById(menuId);
        String menuUuid = menuDO.getMenuUuid();
        //
        List<AppAuthViewDO> authViewDOS = appAuthViewRepository.findByAppIdAndRoleUuidsAndMenuUuid(applicationId, roleUuids, menuUuid);
        Set<String> result = authViewDOS.stream().map(AppAuthViewDO::getViewUuid).collect(Collectors.toSet());
        return result;
    }

    private Set<String> findMenuAllViews(Long applicationId, Long menuId) {
        List<AppResourcePageDO> pages = appPageRepository.findPagesByMenuId(menuId);
        return pages.stream().map(AppResourcePageDO::getPageUuid).collect(Collectors.toSet());
    }

}
