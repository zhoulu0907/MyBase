package com.cmsr.onebase.module.app.build.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.module.app.build.util.AppUtils;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;

import jakarta.annotation.Resource;
import lombok.Setter;

/**
 * @Author：huangjie
 *                  @Date：2025/7/24 12:42
 */
@Setter
@Service
public class AppCommonService {

    @Resource
    private AppApplicationRepository applicationRepository;

    @Resource
    private AppAuthRoleRepository authRoleRepository;

    @Resource
    private AppMenuRepository menuRepository;

    @Resource
    private AdminUserApi adminUserApi;

    public AppApplicationDO validateApplicationExist(Long id) {
        AppApplicationDO applicationDO = applicationRepository.getById(id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        return applicationDO;
    }

    public AppAuthRoleDO validateRoleExist(Long id) {
        AppAuthRoleDO authRoleDO = authRoleRepository.getById(id);
        if (authRoleDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_EXISTS);
        }
        return authRoleDO;
    }

    public AppMenuDO validateMenuExist(Long id) {
        AppMenuDO menuDO = menuRepository.getById(id);
        if (menuDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_NOT_EXIST);
        }
        return menuDO;
    }

    public UserHelper getUserHelper(BaseEntity baseDO) {
        Set<Long> ids = new HashSet<>();
        ids.add(baseDO.getCreator());
        ids.add(baseDO.getUpdater());
        return getUserHelper(ids);
    }

    public UserHelper getUserHelper(List<? extends BaseEntity> baseDOS) {
        Set<Long> ids1 = baseDOS.stream().map(BaseEntity::getCreator).collect(Collectors.toSet());
        Set<Long> ids2 = baseDOS.stream().map(BaseEntity::getUpdater).collect(Collectors.toSet());
        Set<Long> ids = new HashSet<>();
        ids.addAll(ids1);
        ids.addAll(ids2);
        CommonResult<List<AdminUserRespDTO>> dtos = adminUserApi.getUserList(ids);
        Map<Long, AdminUserRespDTO> dtoMap = dtos.getData().stream()
                .collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));
        return new UserHelper(dtoMap);
    }

    public UserHelper getUserHelper(Set<Long> ids) {
        CommonResult<List<AdminUserRespDTO>> dtos = adminUserApi.getUserList(ids);
        Map<Long, AdminUserRespDTO> dtoMap = dtos.getData().stream()
                .collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));
        return new UserHelper(dtoMap);
    }

    /**
     * 随机生成一个appUid，然后去数据库里面查询是否唯一，如果不唯一，则重新生成一个，尝试25次
     *
     * @return 唯一的appUid
     */
    public String findAndCreateAppUid() {
        for (int i = 0; i < 25; i++) {
            String appUid = AppUtils.createAppUid();
            if (applicationRepository.findOneByUid(appUid) == 0) {
                return appUid;
            }
        }
        throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_UID_GENERATE_FAILED);
    }

    public static class UserHelper {

        private Map<Long, AdminUserRespDTO> userMap;

        public UserHelper(Map<Long, AdminUserRespDTO> userMap) {
            this.userMap = userMap;
        }

        public String getUserNickname(Long userId) {
            AdminUserRespDTO adminUserRespDTO = userMap.get(userId);
            if (adminUserRespDTO == null) {
                return "";
            }
            return adminUserRespDTO.getNickname();
        }

        public AdminUserRespDTO getUser(Long userId) {
            return userMap.get(userId);
        }
    }
}
