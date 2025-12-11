package com.cmsr.onebase.module.app.runtime.service.menu;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthViewRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.*;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.enums.menu.MenuTypeEnum;
import com.cmsr.onebase.module.app.core.impl.auth.AppAuthSecurityApiImpl;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthPermissionProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthRoleProvider;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import com.cmsr.onebase.module.app.core.utils.MenuUtils;
import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private AppPageSetRepository appPageSetRepository;

    @Autowired
    private AppAuthRoleProvider appAuthRoleProvider;

    @Resource
    private AppAuthPermissionProvider appAuthPermissionProvider;

    @Resource
    private AppAuthSecurityApiImpl appAuthSecurityApi;

    @Autowired
    private AppAuthViewRepository appAuthViewRepository;

    @Autowired
    private AppPageRepository appPageRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<MenuListRespVO> listBpmApplicationMenu() {
        Long applicationId = ApplicationManager.getApplicationId();

        // 获取应用下所有可见的BPM类型菜单
        List<AppMenuDO> menuDOS = appMenuRepository.findByApplicationIdAndType(applicationId,
                Set.of(MenuTypeEnum.BPM.getValue()));

        // 额外过滤只保留可见的菜单
        menuDOS = menuDOS.stream()
                .filter(menu -> BooleanUtils.toBoolean(menu.getIsVisible()))
                .toList();

        // 返回菜单
        return menuDOS.stream()
                .map(v -> BeanUtils.toBean(v, MenuListRespVO.class))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<MenuListRespVO> listApplicationMenu() {
        Long userId = RTSecurityContext.getRequiredUserId();
        Long applicationId = ApplicationManager.getRequiredApplicationId();
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        List<AppMenuDO> menuDOS = appMenuRepository.findVisibleByAppId(applicationId,
                Set.of(MenuTypeEnum.PAGE.getValue(), MenuTypeEnum.GROUP.getValue()));
        if (!userRoleDTO.isAdminRole()) {
            Set<String> blackMenuUuids = findBlackMenuUuids(applicationId, userRoleDTO.getRoleUuids());
            menuDOS = menuDOS.stream().filter(v -> !blackMenuUuids.contains(v.getMenuUuid())).toList();
        }
        if (CollectionUtils.isEmpty(menuDOS)) {
            return Collections.emptyList();
        }
        return convertToMenuListRespVOS(menuDOS);
    }

    private List<MenuListRespVO> convertToMenuListRespVOS(List<AppMenuDO> menuDOS) {
        // 批量查询 pagesetType
        Long applicationId = ApplicationManager.getApplicationId();
        List<String> menuUuids = menuDOS.stream().map(AppMenuDO::getMenuUuid).collect(Collectors.toList());
        Map<String, Integer> pagesetTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(menuUuids)) {
            List<AppResourcePagesetDO> pagesets = appPageSetRepository.findByMenuUuids(applicationId, menuUuids);
            if (CollectionUtils.isNotEmpty(pagesets)) {
                pagesetTypeMap = pagesets.stream()
                        .filter(p -> p.getMenuUuid() != null && p.getPageSetType() != null)
                        .collect(Collectors.toMap(AppResourcePagesetDO::getMenuUuid, AppResourcePagesetDO::getPageSetType, (v1, v2) -> v1));
            }
        }

        List<MenuListRespVO> menuListRespList = new ArrayList<>();
        final Map<String, Integer> finalPagesetTypeMap = pagesetTypeMap;
        // 把第一层的菜单添加到列表中
        LinkedList<MenuListRespVO> levelOneMenus = menuDOS.stream()
                .filter(v -> MenuUtils.ROOT_MENU_UUID.equals(v.getParentUuid()))
                .map(v -> {
                    MenuListRespVO vo = BeanUtils.toBean(v, MenuListRespVO.class);
                    vo.setPagesetType(finalPagesetTypeMap.get(v.getMenuUuid()));
                    return vo;
                })
                .collect(Collectors.toCollection(LinkedList::new));
        menuListRespList.addAll(levelOneMenus);
        // 递归实现每个菜单的子菜单
        for (MenuListRespVO respVO : menuListRespList) {
            LinkedList<MenuListRespVO> children = recursiveGetChildren(respVO.getMenuUuid(), menuDOS, finalPagesetTypeMap);
            respVO.setChildren(children);
        }
        return menuListRespList;
    }

    private LinkedList<MenuListRespVO> recursiveGetChildren(String parentUuid, List<AppMenuDO> menuDOS, Map<String, Integer> pagesetTypeMap) {
        LinkedList<MenuListRespVO> children = new LinkedList<>();
        for (AppMenuDO menuDO : menuDOS) {
            if (Objects.equals(menuDO.getParentUuid(), parentUuid)) {
                // 只有父菜单的uuid等于当前菜单的父菜单的uuid时，才添加子菜单，继续递归
                MenuListRespVO child = BeanUtils.toBean(menuDO, MenuListRespVO.class);
                child.setPagesetType(pagesetTypeMap.get(menuDO.getMenuUuid()));
                child.setChildren(recursiveGetChildren(child.getMenuUuid(), menuDOS, pagesetTypeMap));
                children.add(child);
            }
        }
        return children.isEmpty() ? null : children;
    }

    private Set<String> findBlackMenuUuids(Long applicationId, Set<String> roleUuids) {
        List<AppAuthPermissionDO> permissions = appAuthPermissionProvider.findPermissions(applicationId, roleUuids);
        Set<String> result = new HashSet<>();
        for (AppAuthPermissionDO permission : permissions) {
            if (NumberUtils.INTEGER_ZERO.equals(permission.getIsPageAllowed())
                    && StringUtils.isNotEmpty(permission.getMenuUuid()))
                result.add(permission.getMenuUuid());
        }
        return result;
    }

    @Override
    public MenuPermissionVO getMenuPermission(Long menuId) {
        Long userId = RTSecurityContext.getRequiredUserId();
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
        String redisKey = CacheUtils.keyForPagePermission(userId, applicationId, menuId);
        RBucket<Set<String>> bucket = redissonClient.getBucket(redisKey, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }

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
        bucket.set(result, CacheUtils.CACHE_EXPIRE_TIME);
        return result;
    }

    private Set<String> findMenuAllViews(Long applicationId, Long menuId) {
        List<AppResourcePageDO> pages = appPageRepository.findPagesByMenuId(menuId);
        return pages.stream().map(AppResourcePageDO::getPageUuid).collect(Collectors.toSet());
    }

}
