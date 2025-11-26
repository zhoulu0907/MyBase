package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.module.app.core.dal.database.AppAuthRoleDeptRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDeptDO;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/10/25 14:58
 */
@Slf4j
@Setter
@Service
public class AppAuthRoleProvider {

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private AppSqlQueryRepository appSqlQueryRepository;

    @Autowired
    private AppAuthRoleDeptRepository appAuthRoleDeptRepository;

    public UserRoleDTO findUserRoleByApplication(Long userId, Long applicationId) {
        List<AppAuthRoleDO> authRoleDOS = new ArrayList<>();

        List<AppAuthRoleDO> userAuthRoleDOS = appAuthRoleRepository.findByUserIdAndApplicationId(userId, applicationId);
        authRoleDOS.addAll(userAuthRoleDOS);

        List<AppAuthRoleDO> deptAuthRoleDOS = findRolesByDept(userId, applicationId);
        authRoleDOS.addAll(deptAuthRoleDOS);

        UserRoleDTO userRoleDTO = new UserRoleDTO();
        userRoleDTO.setAdminRole(false);
        userRoleDTO.setRoleIds(Set.of());
        for (AppAuthRoleDO authRoleDO : authRoleDOS) {
            if (AuthRoleTypeEnum.isSystemAdminRole(authRoleDO.getRoleType())) {
                userRoleDTO.setAdminRole(true);
            }
        }
        Set<Long> ids = authRoleDOS.stream().map(AppAuthRoleDO::getId).collect(Collectors.toSet());
        userRoleDTO.setRoleIds(ids);
        return userRoleDTO;
    }

    private List<AppAuthRoleDO> findRolesByDept(Long userId, Long applicationId) {
        // 获取用户的部门层次（包含用户所在部门及其所有上级部门）
        List<Long> deptTree = appSqlQueryRepository.findDeptHierarchyByUserId(userId);
        if (deptTree == null || deptTree.isEmpty()) {
            return Collections.emptyList();
        }
        // 获取该应用下的部门角色关系
        List<AppAuthRoleDeptDO> authRoleDeptDOS = appAuthRoleDeptRepository.findByApplicationId(applicationId);
        if (authRoleDeptDOS == null || authRoleDeptDOS.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> roleIds = findRolesByDept(authRoleDeptDOS, deptTree);
        // 根据角色ID查询完整的角色信息
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return appAuthRoleRepository.listByIds(roleIds);
    }

    private Set<Long> findRolesByDept(List<AppAuthRoleDeptDO> authRoleDeptDOS, List<Long> deptTree) {
        Set<Long> roleIds = new HashSet<>();
        for (AppAuthRoleDeptDO authRoleDeptDO : authRoleDeptDOS) {
            Long roleDeptId = authRoleDeptDO.getDeptId();
            Integer isIncludeChild = authRoleDeptDO.getIsIncludeChild();
            if (isIncludeChild != null && isIncludeChild == 1) {
                if (deptTree.contains(roleDeptId)) {
                    roleIds.add(authRoleDeptDO.getRoleId());
                }
            } else {
                if (roleDeptId.equals(deptTree.get(0))) {
                    roleIds.add(authRoleDeptDO.getRoleId());
                }
            }
        }
        return roleIds;
    }


}
