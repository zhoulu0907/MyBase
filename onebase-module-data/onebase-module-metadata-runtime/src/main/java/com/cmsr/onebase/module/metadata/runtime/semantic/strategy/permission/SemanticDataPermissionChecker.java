package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.permission;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionFilter;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionLevel;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionTag;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;

import org.springframework.stereotype.Component;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.annotation.Resource;

import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据权限校验器
 *
 * 基于数据范围标签、范围级别与自定义过滤条件，对当前数据行进行可见性与操作权限的校验：
 * - 范围标签：全部数据/本人提交/本部门提交/下级部门提交/自定义条件
 * - 范围级别：本人/本人及下属/主部门/主部门含下级/指定部门/指定人员
 * - 自定义过滤：支持 AND(OR(...)) 的组合条件
 */
@Component
public class SemanticDataPermissionChecker implements SemanticRuntimePermissionChecker {
    private static final String TYPE = "数据权限";
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;

    @Override
    public String getPermissionType() { return TYPE; }

    @Override
    public int getOrder() { return 20; }

    @Override
    /**
     * 数据权限在 UPDATE/DELETE/GET 操作时生效
     */
    public boolean supports(SemanticRecordDTO recordDTO) {
        MetadataDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        return op == MetadataDataMethodOpEnum.UPDATE || op == MetadataDataMethodOpEnum.DELETE || op == MetadataDataMethodOpEnum.GET;
    }

    @Override
    /**
     * 数据权限校验主流程：
     * 1. allDenied/allAllowed 的快速通路
     * 2. 构建数据行与字段映射
     * 3. 逐组判断操作权限 + 范围标签 + 自定义过滤
     */
    public void check(SemanticRecordDTO recordDTO) {
        MetadataPermissionContext permissionContext = recordDTO.getRecordContext().getPermissionContext();
        DataPermission dataPermission = permissionContext.getDataPermission();
        if (dataPermission.isAllDenied()) { throw new PermissionDeniedException(TYPE, "ALL_DENIED", "无权访问数据"); }
        if (dataPermission.isAllAllowed()) { return; }
        List<DataPermissionGroup> groups = dataPermission.getGroups();
        MetadataDataMethodOpEnum operationType = recordDTO.getRecordContext().getOperationType();
        LoginUserCtx loginUserContext = recordDTO.getRecordContext().getLoginUserCtx();
        Map<String,Object> dataRow = buildDataRow(recordDTO);
        Map<Long,String> fieldIdToNameMap = buildFieldIdToNameMap(recordDTO);
        AdminUserRespDTO currentUser = adminUserApi.getUser(loginUserContext.getUserId()).getCheckedData();
        boolean hasPermission = false;
        for (DataPermissionGroup group : groups) {
            boolean canOperate = switch (operationType) {
                case UPDATE -> group.isCanEdit();
                case DELETE -> group.isCanDelete();
                case GET -> true;
                default -> false;
            };
            if (!canOperate) { continue; }
            boolean matchesScope = checkScopeTags(group, dataRow, currentUser, fieldIdToNameMap)
                    && checkFilters(group, dataRow, fieldIdToNameMap);
            if (matchesScope) { hasPermission = true; break; }
        }
        if (!hasPermission) {
            String operation = switch (operationType) { case UPDATE -> "编辑"; case DELETE -> "删除"; case GET -> "查看"; default -> "操作"; };
            throw new PermissionDeniedException(TYPE, operationType.name(), String.format("无权%s该数据", operation));
        }
    }
    
    /**
     * 从值模型构建数据行：字段名 -> 原始值
     */
    private Map<String,Object> buildDataRow(SemanticRecordDTO recordDTO) {
        Map<String,Object> row = new HashMap<>();
        Map<String, SemanticFieldValueDTO> data = recordDTO.getEntityValue().getFieldValueMap();
        if (data == null) { return row; }
        for (Map.Entry<String, SemanticFieldValueDTO> e: data.entrySet()) {
            row.put(e.getKey(), e.getValue() == null ? null : e.getValue().getEntityValue());
        }
        return row;
    }

    /**
     * 构建字段ID到字段名的映射，供范围/过滤条件解析
     */
    private Map<Long,String> buildFieldIdToNameMap(SemanticRecordDTO recordDTO) {
        Map<Long,String> map = new HashMap<>();
        List<SemanticFieldSchemaDTO> fields = recordDTO.getEntitySchema().getFields();
        if (fields == null) { return map; }
        for (SemanticFieldSchemaDTO f: fields) {
            if (f.getId() != null && f.getFieldName() != null) { map.put(f.getId(), f.getFieldName()); }
        }
        return map;
    }

    /**
     * 检查范围标签，只要命中任意标签即认为范围匹配
     */
    private boolean checkScopeTags(DataPermissionGroup group, Map<String,Object> dataRow,
                                   AdminUserRespDTO currentUser, Map<Long,String> fieldIdToNameMap) {
        List<DataPermissionTag> scopeTags = group.getScopTags();
        if (scopeTags != null && scopeTags.contains(DataPermissionTag.ALL_DATA)) { return true; }
        if (scopeTags == null || scopeTags.isEmpty()) { return true; }
        for (DataPermissionTag tag: scopeTags) {
            switch (tag) {
                case ALL_DATA -> { return true; }
                case OWN_SUBMIT -> { Object creator = dataRow.get("creator"); if (creator != null && String.valueOf(creator).equals(String.valueOf(currentUser.getId()))) { return true; } }
                case DEPARTMENT_SUBMIT -> { if (checkDepartmentMatch(dataRow, currentUser.getDeptId())) { return true; } }
                case SUB_DEPARTMENT_SUBMIT -> { if (checkSubDepartmentMatch(dataRow, currentUser.getDeptId())) { return true; } }
                case CUSTOM_CONDITION -> { return checkScopeLevel(group, dataRow, currentUser, fieldIdToNameMap); }
                default -> {}
            }
        }
        return false;
    }

    /**
     * 检查是否本部门提交
     */
    private boolean checkDepartmentMatch(Map<String,Object> dataRow, Long userDeptId) {
        String[] deptFieldNames = {"owner_dept"};
        for (String fieldName : deptFieldNames) {
            Object deptId = dataRow.get(fieldName);
            if (deptId != null && String.valueOf(deptId).equals(String.valueOf(userDeptId))) { return true; }
        }
        return false;
    }

    /**
     * 检查是否下级部门提交
     */
    private boolean checkSubDepartmentMatch(Map<String,Object> dataRow, Long userDeptId) {
        List<DeptRespDTO> childDepts = deptApi.getChildDeptList(userDeptId).getCheckedData();
        Set<Long> childDeptIds = childDepts.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
        String[] deptFieldNames = {"owner_dept"};
        for (String fieldName : deptFieldNames) {
            Object deptId = dataRow.get(fieldName);
            if (deptId != null && childDeptIds.contains(toLong(deptId))) { return true; }
        }
        return false;
    }

    /**
     * 检查范围级别，需结合 scopeFieldId 的字段值进行判断
     */
    private boolean checkScopeLevel(DataPermissionGroup group, Map<String,Object> dataRow,
                                    AdminUserRespDTO currentUser, Map<Long,String> fieldIdToNameMap) {
        DataPermissionLevel scopeLevel = group.getScopeLevel();
        Long scopeFieldId = group.getScopeFieldId();
        String scopeValue = group.getScopeValue();
        String fieldName = fieldIdToNameMap.get(scopeFieldId);
        Object fieldValue = dataRow.get(fieldName);
        return switch (scopeLevel) {
            case SELF -> fieldValue != null && String.valueOf(fieldValue).equals(String.valueOf(currentUser.getId()));
            case SELF_AND_SUBORDINATES -> {
                List<AdminUserRespDTO> subs = adminUserApi.getUserListBySubordinate(currentUser.getId()).getCheckedData();
                Set<Long> ids = subs.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                ids.add(currentUser.getId());
                yield fieldValue != null && ids.contains(toLong(fieldValue));
            }
            case MAIN_DEPARTMENT -> {
                AdminUserRespDTO target = getAdminUser(currentUser, fieldValue);
                yield target != null && target.getDeptId() != null && target.getDeptId().equals(currentUser.getDeptId());
            }
            case MAIN_DEPARTMENT_AND_SUBS -> {
                AdminUserRespDTO user = getAdminUser(currentUser, fieldValue);
                List<DeptRespDTO> childDepts = deptApi.getChildDeptList(currentUser.getDeptId()).getCheckedData();
                Set<Long> deptIds = childDepts.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
                deptIds.add(currentUser.getDeptId());
                yield user != null && user.getDeptId() != null && deptIds.contains(user.getDeptId());
            }
            case SPECIFIED_DEPARTMENT -> {
                AdminUserRespDTO user2 = getAdminUser(currentUser, fieldValue);
                yield user2 != null && user2.getDeptId() != null && checkSpecifiedScopeValue(scopeValue, user2.getDeptId());
            }
            case SPECIFIED_PERSON -> checkSpecifiedScopeValue(scopeValue, fieldValue);
            default -> false;
        };
    }

    /**
     * 指定人员/部门范围解析：scopeValue 为 JSON 数组，取其中 key 字段做匹配
     */
    private boolean checkSpecifiedScopeValue(String scopeValue, Object targetValue) {
        List<Object> list = JsonUtils.parseObject(scopeValue, new TypeReference<List<Object>>(){ });
        return list != null && list.contains(targetValue);
    }

    /**
     * 获取字段值对应的用户信息，字段值为空或等于当前用户时复用当前用户
     */
    private AdminUserRespDTO getAdminUser(AdminUserRespDTO currentUser, Object fieldValue) {
        Long userId = fieldValue == null ? null : toLong(fieldValue);
        if (userId == null || Objects.equals(userId, currentUser.getId())) { return currentUser; }
        return adminUserApi.getUser(userId).getCheckedData();
    }

    /**
     * AND(OR(...)) 自定义过滤条件检查
     */
    private boolean checkFilters(DataPermissionGroup group, Map<String,Object> dataRow, Map<Long,String> fieldIdToNameMap) {
        List<List<DataPermissionFilter>> filters = group.getFilters();
        if (filters == null || filters.isEmpty()) { return true; }
        for (List<DataPermissionFilter> orConditionGroup : filters) {
            boolean orConditionMatched = false;
            if (orConditionGroup == null || orConditionGroup.isEmpty()) { continue; }
            for (DataPermissionFilter filter : orConditionGroup) {
                if (evaluateFilter(filter, dataRow, fieldIdToNameMap)) {
                    orConditionMatched = true;
                    break;
                }
            }
            if (!orConditionMatched) { return false; }
        }
        return true;
    }

    /**
     * 单个过滤条件的评估，支持 EQ/NE/IN/NIN 等基础操作符
     */
    private boolean evaluateFilter(DataPermissionFilter filter, Map<String,Object> dataRow, Map<Long,String> fieldIdToNameMap) {
        if (filter == null || filter.getFieldId() == null) { return false; }
        String fieldName = fieldIdToNameMap.get(filter.getFieldId());
        if (fieldName == null) { return false; }
        Object currentFieldValue = dataRow.get(fieldName);
        String operator = filter.getFieldOperator();
        String filterTargetValue = filter.getFieldValue();
        String currentValueAsString = String.valueOf(currentFieldValue);
        String targetValueAsString = String.valueOf(filterTargetValue);
        if (operator == null) { operator = "EQ"; }
        switch (operator.toUpperCase()) {
            case "EQ":
            case "=":
                return Objects.equals(currentValueAsString, targetValueAsString);
            case "NE":
            case "!=":
                return !Objects.equals(currentValueAsString, targetValueAsString);
            case "IN": {
                if (targetValueAsString == null) { return false; }
                for (String token : targetValueAsString.split(",")) {
                    if (Objects.equals(currentValueAsString, token.trim())) { return true; }
                }
                return false;
            }
            case "NIN": {
                if (targetValueAsString == null) { return true; }
                for (String token : targetValueAsString.split(",")) {
                    if (Objects.equals(currentValueAsString, token.trim())) { return false; }
                }
                return true;
            }
            default:
                return false;
        }
    }

    /**
     * 安全 Long 转换，去除对三方工具的依赖
     */
    private Long toLong(Object v) {
        if (v == null) { return null; }
        if (v instanceof Long l) { return l; }
        if (v instanceof Number n) { return n.longValue(); }
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) { return null; }
        try { return Long.parseLong(s); } catch (NumberFormatException ex) { return null; }
    }
}
