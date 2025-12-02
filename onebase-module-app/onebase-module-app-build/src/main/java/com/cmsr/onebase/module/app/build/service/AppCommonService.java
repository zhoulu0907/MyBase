package com.cmsr.onebase.module.app.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
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
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/7/24 12:42
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
        Map<Long, AdminUserRespDTO> dtoMap = dtos.getData().stream().collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));
        return new UserHelper(dtoMap);
    }


    public UserHelper getUserHelper(Set<Long> ids) {
        CommonResult<List<AdminUserRespDTO>> dtos = adminUserApi.getUserList(ids);
        Map<Long, AdminUserRespDTO> dtoMap = dtos.getData().stream().collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));
        return new UserHelper(dtoMap);
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
