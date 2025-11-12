package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleDeptRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDeptDO;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<AuthRoleDO> authRoleDOS1 = appAuthRoleRepository.findByUserIdAndApplicationId(userId, applicationId);
        List<AuthRoleDO> authRoleDOS2 = findRolesByDept(userId, applicationId);

        List<AuthRoleDO> authRoleDOS = new ArrayList<>();
        authRoleDOS.addAll(authRoleDOS1);
        authRoleDOS.addAll(authRoleDOS2);

        UserRoleDTO userRoleDTO = new UserRoleDTO();
        userRoleDTO.setAdminRole(false);
        userRoleDTO.setRoleIds(Set.of());
        for (AuthRoleDO authRoleDO : authRoleDOS) {
            if (AuthRoleTypeEnum.isSystemAdminRole(authRoleDO.getRoleType())) {
                userRoleDTO.setAdminRole(true);
            }
        }
        Set<Long> ids = authRoleDOS.stream().map(AuthRoleDO::getId).collect(Collectors.toSet());
        userRoleDTO.setRoleIds(ids);
        return userRoleDTO;
    }

    private List<AuthRoleDO> findRolesByDept(Long userId, Long applicationId) {
        // 获取用户的部门层次（包含用户所在部门及其所有上级部门）
        List<Long> deptTree = appSqlQueryRepository.findDeptHierarchyByUserId(userId);
        if (deptTree == null || deptTree.isEmpty()) {
            return new ArrayList<>();
        }
        // 获取该应用下的部门角色关系
        List<AuthRoleDeptDO> authRoleDeptDOS = appAuthRoleDeptRepository.findByApplicationId(applicationId);
        if (authRoleDeptDOS == null || authRoleDeptDOS.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> roleIds = findRolesByDept(authRoleDeptDOS, deptTree);
        // 根据角色ID查询完整的角色信息
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return appAuthRoleRepository.findAllByIds(roleIds);
    }

    private Set<Long> findRolesByDept(List<AuthRoleDeptDO> authRoleDeptDOS, List<Long> deptTree) {
        Set<Long> roleIds = new HashSet<>();
        for (AuthRoleDeptDO authRoleDeptDO : authRoleDeptDOS) {
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
