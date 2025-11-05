package com.cmsr.onebase.module.formula.service.extendsion;

import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.permission.RoleApi;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.cmsr.onebase.module.formula.enums.FormulaConstants.*;

@Slf4j
@Service
public class FormulaExtendsServiceImpl implements FormulaExtendsService {

    /**
     * 注入用户API
     */
    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 注入角色API
     */
    @Resource
    private RoleApi roleApi;

    /**
     * 注入部门API
     */
    @Resource
    private DeptApi deptApi;

    @Override
    public void buildParametersWithSystemInfo(String formula, Map<String, Object> parameters) {
        // 获取当前登录用户信息
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return;
        }
        AdminUserRespDTO user = adminUserApi.getUser(loginUserId).getCheckedData();
        DeptRespDTO dept = deptApi.getDept(user.getDeptId()).getCheckedData();
        // 检查公式是否包含用户相关函数
        if (formula.contains(GETUSER)) {
            if (user != null) {
                parameters.put("id", user.getId());
                parameters.put("name", user.getNickname());
            }
            return;
        }

        // 检查公式是否包含部门相关函数
        if (formula.contains(GETDEPT)) {
            if (dept != null) {
                parameters.put("deptno", dept.getId());
                parameters.put("name", dept.getName());
            }
            return;
        }

        // 检查公式是否包含上级部门相关函数
        if (formula.contains(GETUPDEPT)) {
            if (dept != null && dept.getParentId() != null) {
                DeptRespDTO parentDept = deptApi.getDept(dept.getParentId()).getCheckedData();
                if (parentDept != null) {
                    parameters.put("deptno", parentDept.getId());
                    parameters.put("name", parentDept.getName());
                }
            }
            return;
        }

        // 检查公式是否包含角色相关函数
        if (formula.contains(GETROLE)) {
            // 角色信息已在PermissionService中处理，此处可扩展具体实现
            // 获取用户拥有的角色ID列表
            // Set<Long> roleIds = permissionService.getRoleIdsListByUserId(loginUserId);
            // if (roleIds != null && !roleIds.isEmpty()) {
            //     // 根据角色ID获取角色详细信息
            //     List<RoleDO> roles = roleService.getRoleList(roleIds);
            //     parameters.put("roles", roles);
            // } else {
            //     parameters.put("roles", java.util.Collections.emptyList());
            // }
        }

        // 检查公式是否包含直属上级相关函数
        if (formula.contains(GETSUPERVISOR)) {
            Object idObj = parameters.get("id");
            Long userId = null;
            if (idObj instanceof Long) {
                userId = (Long) idObj;
            } else if (idObj instanceof Integer) {
                userId = ((Integer) idObj).longValue();
            }
            if (userId != null) {
                user = adminUserApi.getUser(userId).getCheckedData();
                dept = deptApi.getDept(user.getDeptId()).getCheckedData();
            }
            if (dept != null && dept.getLeaderUserId() != null) {
                AdminUserRespDTO supervisor = adminUserApi.getUser(dept.getLeaderUserId()).getCheckedData();
                if (supervisor != null) {
                    parameters.put("id", supervisor.getId());
                    parameters.put("name", supervisor.getNickname());
                }
            }
            return;
        }

        // 检查公式是否包含角色相关函数
        if (formula.contains(ISINROLE)) {

        }
        // 检查公式是否包含角色相关函数
        if (formula.contains(ISINDEPT)) {
            // 角色信息已在PermissionService中处理，此处可扩展具体实现
            // 获取用户拥有的角色ID列表
            // Set<Long> roleIds = permissionService.getRoleIdsListByUserId(loginUserId);
            // if (roleIds != null && !roleIds.isEmpty()) {
            //     // 根据角色ID获取角色详细信息
            //     List<RoleDO> roles = roleService.getRoleList(roleIds);
            //     parameters.put("roles", roles);
            // } else {
            //     parameters.put("roles", java.util.Collections.emptyList());
            // }
        }
    }
}