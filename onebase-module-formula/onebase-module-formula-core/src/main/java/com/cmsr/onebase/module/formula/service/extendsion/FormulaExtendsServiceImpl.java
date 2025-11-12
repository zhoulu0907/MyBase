package com.cmsr.onebase.module.formula.service.extendsion;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.app.api.auth.dto.AuthRoleDTO;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.module.formula.enums.FormulaConstants.*;

@Slf4j
@Service
public class FormulaExtendsServiceImpl implements FormulaExtendsService {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ROLE_ID = "roleId";
    public static final String IS_IN_ROLE = "isInRole";
    public static final String IS_IN_DEPT = "isInDept";
    /**
     * 注入用户API
     */
    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 注入角色API
     */
    @Resource
    private AppAuthRoleUser authRoleUser;

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
                parameters.put(ID, user.getId());
                parameters.put(NAME, user.getNickname());
            }
            return;
        }

        // 检查公式是否包含部门相关函数
        if (formula.contains(GETDEPT)) {
            if (dept != null) {
                parameters.put(ID, dept.getId());
                parameters.put(NAME, dept.getName());
            }
            return;
        }

        // 检查公式是否包含上级部门相关函数
        if (formula.contains(GETUPDEPT)) {
            if (dept != null && dept.getParentId() != null) {
                DeptRespDTO parentDept = deptApi.getDept(dept.getParentId()).getCheckedData();
                if (parentDept != null) {
                    parameters.put(ID, parentDept.getId());
                    parameters.put(NAME, parentDept.getName());
                }
            }
            return;
        }

        // 检查公式是否包含角色相关函数
        if (formula.contains(GETROLE)) {
            Long id = getId(parameters,ID);
            if (id == null) {
                id = user.getId();
            }
            List<AuthRoleDTO> roles = authRoleUser.findRolesByUserId(id);
            // 角色信息转为List<Map>，每个Map包含id和name键
            if (roles != null) {
                List<Map<String, Object>> roleList = roles.stream()
                        .map(role -> {
                            Map<String, Object> roleMap = new HashMap<>();
                            roleMap.put(ID, role.getId());
                            roleMap.put(NAME, role.getRoleName());
                            return roleMap;
                        })
                        .collect(Collectors.toList());
                parameters.put(ID, JsonUtils.toJsonString(roleList));
            }
        }

        // 检查公式是否包含直属上级相关函数
        if (formula.contains(GETSUPERVISOR)) {
            Long userId = getId(parameters,ID);
            if (userId != null) {
                user = adminUserApi.getUser(userId).getCheckedData();
                dept = deptApi.getDept(user.getDeptId()).getCheckedData();
            }
            if (dept != null && dept.getLeaderUserId() != null) {
                AdminUserRespDTO supervisor = adminUserApi.getUser(dept.getLeaderUserId()).getCheckedData();
                if (supervisor != null) {
                    parameters.put(ID, supervisor.getId());
                    parameters.put(NAME, supervisor.getNickname());
                }
            }
            return;
        }

        // 检查公式是否包含角色相关函数
        if (formula.contains(ISINROLE)) {
            Long id = getId(parameters,ID);
            if (id == null) {
                id = user.getId();
            }
            Long roleId = getId(parameters, ROLE_ID);
            List<AuthRoleDTO> roles = authRoleUser.findRolesByUserId(id);
            if (CollectionUtils.isNotEmpty(roles)) {
                List<Long> roleList = roles.stream().map(role -> role.getId()).collect(Collectors.toList());
                if (roleId != null && roleList.contains(roleId)) {
                    parameters.put(IS_IN_ROLE, true);
                } else {
                    parameters.put(IS_IN_ROLE, false);
                }
            }
            return;
        }
        // 检查公式是否包含部门相关函数
        if (formula.contains(ISINDEPT)) {
            Long id = getId(parameters,"id");
            if (id == null) {
                id = dept.getId();
            }
            CommonResult<List<DeptRespDTO>> deptList = deptApi.getParentDeptsListByUserId(user.getId());
            if (deptList.isSuccess()) {
                List<DeptRespDTO> deptListData = deptList.getData();
                if (CollectionUtils.isNotEmpty(deptListData)) {
                    List<Long> deptIds = deptListData.stream().map(deptRespDTO -> deptRespDTO.getId())
                            .collect(Collectors.toList());
                    if (id != null && deptIds.contains(id)) {
                        parameters.put(IS_IN_DEPT, true);
                    } else {
                        parameters.put(IS_IN_DEPT, false);
                    }
                }
            }
        }
    }

    @Nullable
    private static Long getId(Map<String, Object> parameters, String key) {
        Object valueObj = parameters.get(key);
        Long id = null;
        if (valueObj instanceof Long) {
            id = (Long) valueObj;
        } else if (valueObj instanceof Integer) {
            id = ((Integer) valueObj).longValue();
        }
        return id;
    }


}