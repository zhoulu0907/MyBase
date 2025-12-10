package com.cmsr.onebase.module.metadata.core.semantic.strategy.permission;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionFilter;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionLevel;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionTag;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticPermissionContext;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticLoginUserCtx;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.constants.SystemFieldConstants;
import com.cmsr.onebase.module.metadata.core.semantic.dal.DynamicMetadataRepository;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;

import org.springframework.stereotype.Component;

import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.annotation.Resource;

import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import com.mybatisflex.core.row.Row;

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
    @Resource
    private DynamicMetadataRepository dynamicMetadataRepository;

    @Override
    public String getPermissionType() { return TYPE; }

    @Override
    public int getOrder() { return 20; }

    @Override
    /**
     * 数据权限在 UPDATE/DELETE/GET 操作时生效
     */
    public boolean supports(SemanticRecordDTO recordDTO) {
        SemanticDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        return op == SemanticDataMethodOpEnum.UPDATE || op == SemanticDataMethodOpEnum.DELETE || op == SemanticDataMethodOpEnum.GET;
    }

    @Override
    /**
     * 数据权限校验主流程：
     * 1. allDenied/allAllowed 的快速通路
     * 2. 构建数据行与字段映射
     * 3. 逐组判断操作权限 + 范围标签 + 自定义过滤
     */
    public void check(SemanticRecordDTO recordDTO) {
        SemanticPermissionContext permissionContext = recordDTO.getRecordContext().getPermissionContext();
        DataPermission dataPermission = permissionContext.getDataPermission();
        if (dataPermission.isAllDenied()) { throw new PermissionDeniedException(TYPE, "ALL_DENIED", "无权访问数据"); }
        if (dataPermission.isAllAllowed()) { return; }
        List<DataPermissionGroup> groups = dataPermission.getGroups();
        SemanticDataMethodOpEnum operationType = recordDTO.getRecordContext().getOperationType();
        Map<String,Object> dataRow = recordDTO.getEntityValue() == null ? new LinkedHashMap<>() : recordDTO.getEntityValue().getCurrentEntityRawMap();
        Object ownerIdRaw = dataRow == null ? null : dataRow.get(SystemFieldConstants.REQUIRE.OWNER_ID);
        if ((ownerIdRaw == null) && recordDTO.getEntityValue() != null && recordDTO.getEntityValue().getId() != null) {
            String tableName = recordDTO.getEntitySchema().getTableName();
            String pkField = getPrimaryKeyFieldName(recordDTO.getEntitySchema().getFields());
            Object idVal = recordDTO.getEntityValue().getId();
            Row r = dynamicMetadataRepository.selectMainById(tableName, pkField, idVal, true);
            dataRow = (r == null) ? new LinkedHashMap<>() : buildDataRowFromRow(r, recordDTO.getEntitySchema().getFields());
        }
        List<SemanticFieldSchemaDTO> fields = recordDTO.getEntitySchema().getFields();
        boolean hasPermission = false;
        for (DataPermissionGroup group : groups) {
            boolean canOperate = switch (operationType) {
                case UPDATE -> group.isCanEdit();
                case DELETE -> group.isCanDelete();
                case GET -> true;
                default -> false;
            };
            if (!canOperate) { continue; }
            boolean matchesScope = checkScopeTags(group, dataRow, fields)
                    && checkFilters(group, dataRow, fields);
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
    /**
     * 检查范围标签，只要命中任意标签即认为范围匹配
     */
    private boolean checkScopeTags(DataPermissionGroup group, Map<String,Object> dataRow,
                                   List<SemanticFieldSchemaDTO> fields) {
        List<DataPermissionTag> scopeTags = group.getScopTags();
        
        if (scopeTags != null && scopeTags.contains(DataPermissionTag.ALL_DATA)) { return true; }
        if (scopeTags == null || scopeTags.isEmpty()) { return true; }
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();
        for (DataPermissionTag tag: scopeTags) {
            switch (tag) {
                case ALL_DATA -> { return true; }
                case OWN_SUBMIT -> { Object ownerId = dataRow.get(SystemFieldConstants.REQUIRE.OWNER_ID); if (ownerId != null && currentUserId != null && Objects.equals(toLong(ownerId), currentUserId)) { return true; } }
                case DEPARTMENT_SUBMIT -> { if (currentDeptId != null && checkDepartmentMatch(dataRow, currentDeptId)) { return true; } }
                case SUB_DEPARTMENT_SUBMIT -> { if (currentDeptId != null && checkSubDepartmentMatch(dataRow, currentDeptId)) { return true; } }
                case CUSTOM_CONDITION -> { return checkScopeLevel(group, dataRow, fields); }
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
            if (deptId != null && Objects.equals(toLong(deptId), userDeptId)) { return true; }
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
                                    List<SemanticFieldSchemaDTO> fields) {
        DataPermissionLevel scopeLevel = group.getScopeLevel();
        String scopeFieldUuid = group.getScopeFieldUuid();
        String scopeValue = group.getScopeValue();
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();
        String fieldName = null;
        if (fields != null && scopeFieldUuid != null) {
            for (SemanticFieldSchemaDTO f : fields) { if (scopeFieldUuid.equals(f.getFieldUuid())) { fieldName = f.getFieldName(); break; } }
        }
        Object fieldValue = dataRow.get(fieldName);
        return switch (scopeLevel) {
            case SELF -> fieldValue != null && currentUserId != null && Objects.equals(toLong(fieldValue), currentUserId);
            case SELF_AND_SUBORDINATES -> {
                if (currentUserId == null) { yield false; }
                List<AdminUserRespDTO> subs = adminUserApi.getUserListBySubordinate(currentUserId).getCheckedData();
                Set<Long> ids = subs.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                ids.add(currentUserId);
                yield fieldValue != null && ids.contains(toLong(fieldValue));
            }
            case MAIN_DEPARTMENT -> {
                AdminUserRespDTO target = getAdminUser(currentUserId, fieldValue);
                yield target != null && target.getDeptId() != null && currentDeptId != null && target.getDeptId().equals(currentDeptId);
            }
            case MAIN_DEPARTMENT_AND_SUBS -> {
                AdminUserRespDTO user = getAdminUser(currentUserId, fieldValue);
                if (currentDeptId == null) { yield false; }
                List<DeptRespDTO> childDepts = deptApi.getChildDeptList(currentDeptId).getCheckedData();
                Set<Long> deptIds = childDepts.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
                deptIds.add(currentDeptId);
                yield user != null && user.getDeptId() != null && deptIds.contains(user.getDeptId());
            }
            case SPECIFIED_DEPARTMENT -> {
                AdminUserRespDTO user2 = getAdminUser(currentUserId, fieldValue);
                yield user2 != null && user2.getDeptId() != null && checkSpecifiedScopeValue(scopeValue, user2.getDeptId());
            }
            case SPECIFIED_PERSON -> checkSpecifiedScopeValue(scopeValue, fieldValue);
            default -> false;
        };
    }

    private String getPrimaryKeyFieldName(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null || fields.isEmpty()) { return "id"; }
        String idNamed = fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsPrimaryKey()))
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst().orElse(null);
        if (idNamed != null) { return idNamed; }
        String firstPk = fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsPrimaryKey()))
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
        if (firstPk != null) { return firstPk; }
        boolean hasId = fields.stream().map(SemanticFieldSchemaDTO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        return hasId ? "id" : "id";
    }

    private Map<String, Object> buildDataRowFromRow(Row row, List<SemanticFieldSchemaDTO> fields) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (row == null || fields == null) { return map; }
        for (SemanticFieldSchemaDTO f : fields) {
            String name = f.getFieldName();
            if (name != null) { map.put(name, row.get(name)); }
        }
        return map;
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
    private AdminUserRespDTO getAdminUser(Long currentUserId, Object fieldValue) {
        Long userId = fieldValue == null ? null : toLong(fieldValue);
        if (userId == null || (currentUserId != null && Objects.equals(userId, currentUserId))) {
            return currentUserId == null ? null : adminUserApi.getUser(currentUserId).getCheckedData();
        }
        return adminUserApi.getUser(userId).getCheckedData();
    }

    /**
     * AND(OR(...)) 自定义过滤条件检查
     */
    private boolean checkFilters(DataPermissionGroup group, Map<String,Object> dataRow, List<SemanticFieldSchemaDTO> fields) {
        List<List<DataPermissionFilter>> filters = group.getFilters();
        if (filters == null || filters.isEmpty()) { return true; }
        for (List<DataPermissionFilter> orConditionGroup : filters) {
            boolean orConditionMatched = false;
            if (orConditionGroup == null || orConditionGroup.isEmpty()) { continue; }
            for (DataPermissionFilter filter : orConditionGroup) {
                if (evaluateFilter(filter, dataRow, fields)) {
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
    private boolean evaluateFilter(DataPermissionFilter filter, Map<String,Object> dataRow, List<SemanticFieldSchemaDTO> fields) {
        if (filter == null || filter.getFieldUuid() == null) { return false; }
        String fieldName = null;
        if (fields != null) {
            for (SemanticFieldSchemaDTO f : fields) { if (filter.getFieldUuid().equals(f.getFieldUuid())) { fieldName = f.getFieldName(); break; } }
        }
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
