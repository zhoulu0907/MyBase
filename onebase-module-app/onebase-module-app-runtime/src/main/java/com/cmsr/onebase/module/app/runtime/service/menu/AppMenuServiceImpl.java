package com.cmsr.onebase.module.app.runtime.service.menu;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthViewRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.impl.auth.AppAuthSecurityApiImpl;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthPermissionProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthRoleProvider;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import com.cmsr.onebase.module.app.core.utils.MenuUtils;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
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
public class AppMenuServiceImpl implements AppMenuService {

    @Resource
    private AppMenuRepository appMenuRepository;

    @Autowired
    private AppAuthRoleProvider appAuthRoleProvider;

    @Resource
    private AppAuthPermissionProvider appAuthPermissionProvider;

    @Resource
    private AppAuthSecurityApiImpl appAuthSecurityApi;

    @Autowired
    private AppAuthViewRepository appAuthViewRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<MenuListRespVO> listApplicationMenu() {
        Long userId = RTSecurityContext.getUserId();
        Long applicationId = RTSecurityContext.getApplicationId();
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        List<AppMenuDO> menuDOS;
        if (userRoleDTO.isAdminRole()) {
            menuDOS = appMenuRepository.findVisibleByAppId(applicationId);
        } else {
            Set<Long> menuIds = findVisibleMenuIds(applicationId, userRoleDTO.getRoleIds());
            menuDOS = appMenuRepository.findVisibleByAppIdAndMenuIds(applicationId, menuIds);
        }
        List<MenuListRespVO> menuListRespList = new ArrayList<>();
        // 把第一层的菜单添加到列表中
        LinkedList<MenuListRespVO> levelOneMenus = menuDOS.stream()
                .filter(v -> MenuUtils.ROOT_MENU_ID.equals(v.getParentId()))
                .map(v -> BeanUtils.toBean(v, MenuListRespVO.class))
                .collect(Collectors.toCollection(LinkedList::new));
        menuListRespList.addAll(levelOneMenus);
        // 递归实现每个菜单的子菜单
        for (MenuListRespVO respVO : menuListRespList) {
            LinkedList<MenuListRespVO> children = recursiveGetChildren(respVO.getId(), menuDOS);
            respVO.setChildren(children);
        }
        return menuListRespList;
    }

    private LinkedList<MenuListRespVO> recursiveGetChildren(Long parentId, List<AppMenuDO> menuDOS) {
        LinkedList<MenuListRespVO> children = new LinkedList<>();
        for (AppMenuDO menuDO : menuDOS) {
            if (Objects.equals(menuDO.getParentId(), parentId)) {
                // 只有父菜单的uuid等于当前菜单的父菜单的uuid时，才添加子菜单，继续递归
                MenuListRespVO child = BeanUtils.toBean(menuDO, MenuListRespVO.class);
                child.setChildren(recursiveGetChildren(child.getId(), menuDOS));
                children.add(child);
            }
        }
        return children.isEmpty() ? null : children;
    }

    private Set<Long> findVisibleMenuIds(Long applicationId, Set<Long> roleIds) {
        List<AppAuthPermissionDO> permissions = appAuthPermissionProvider.findPermissions(applicationId, roleIds);
        Set<Long> result = new HashSet<>();
        for (AppAuthPermissionDO permission : permissions) {
            if (NumberUtils.INTEGER_ONE.equals(permission.getIsPageAllowed()))
                result.add(permission.getMenuId());
        }
        return result;
    }

    @Override
    public MenuPermissionVO getMenuPermission(Long menuId) {
        Long userId = RTSecurityContext.getUserId();
        Long applicationId = RTSecurityContext.getApplicationId();
        MenuPermissionVO menuPermissionVO = new MenuPermissionVO();
        menuPermissionVO.setOperationPermission(appAuthSecurityApi.getMenuOperationPermission(userId, applicationId, menuId));
        menuPermissionVO.setFieldPermission(appAuthSecurityApi.getMenuFieldPermission(userId, applicationId, menuId));
        menuPermissionVO.setViewIds(findMenuViews(userId, applicationId, menuId));
        return menuPermissionVO;
    }

    /**
     * 要缓存
     */
    public Set<Long> findMenuViews(Long userId, Long applicationId, Long menuId) {
        String redisKey = CacheUtils.keyForPagePermission(userId, applicationId, menuId);
        RBucket<Set<Long>> bucket = redissonClient.getBucket(redisKey, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }
        //
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        if (userRoleDTO.isAdminRole()) {
            return findMenuAllViews(applicationId, menuId);
        }
        OperationPermission menuOperationPermission = appAuthSecurityApi.getMenuOperationPermission(userId, applicationId, menuId);
        if (menuOperationPermission.isAllFieldsAllowed()) {
            return findMenuAllViews(applicationId, menuId);
        }
        Set<Long> roleIds = userRoleDTO.getRoleIds();
        Set<Long> result = appAuthViewRepository.findByAppIdAndRoleIdsAndMenuId(applicationId, roleIds, menuId)
                .stream().map(viewDO -> viewDO.getViewId()).collect(Collectors.toSet());
        //
        bucket.set(result, CacheUtils.CACHE_EXPIRE_TIME);
        return result;
    }

    private Set<Long> findMenuAllViews(Long applicationId, Long menuId) {
        return appMenuRepository.findPageIdsByAppIdAndMenuId(applicationId, menuId);
    }

}
