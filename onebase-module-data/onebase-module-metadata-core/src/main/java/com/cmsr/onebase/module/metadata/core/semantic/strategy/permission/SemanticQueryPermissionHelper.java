package com.cmsr.onebase.module.metadata.core.semantic.strategy.permission;

import cn.hutool.core.convert.Convert;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionFilter;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionLevel;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionTag;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticPermissionContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.mybatisflex.core.query.CPI;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于 MyBatis Flex 的查询权限助手
 *
 * 职责：
 * - 将运行态的权限上下文（数据权限/字段权限）转换为 `QueryWrapper` 条件
 * - 在分页查询前应用数据范围过滤（本人/本部门/下级部门/全部拒绝等）
 * - 在查询后对结果进行字段级过滤，仅保留可读与系统字段
 *
 * 适配：运行态语义查询链路（`SemanticPageExecutor` -> `SemanticDataCrudService`）。
 */
@Slf4j
@Component
public class SemanticQueryPermissionHelper {

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    /**
     * 应用数据权限与可查询字段选择到 `QueryWrapper`
     *
     * - 数据权限：按标签聚合 owner_id/owner_dept 条件并 OR 合并，整体 AND 到主查询
     * - 字段权限：根据权限裁剪选择列，减少不必要的列查询
     *
     * @param permissionContext 权限上下文
     * @param fields 语义字段列表（用于系统字段识别与列选择）
     */
    public QueryWrapper applyQueryPermissionFilter(
                                            QueryWrapper queryWrapper,
                                            SemanticPermissionContext permissionContext,
                                            List<SemanticFieldSchemaDTO> fields) {
        if (queryWrapper == null) { queryWrapper = new QueryWrapper(); }
        FieldPermission fieldPermissionEarly = permissionContext == null ? null : permissionContext.getFieldPermission();
        List<String> selectableEarly = getQueryableFieldNames(fieldPermissionEarly, fields);
        if (selectableEarly != null && !selectableEarly.isEmpty()) {
            for (String f : selectableEarly) { queryWrapper.select(new QueryColumn(f)); }
        }
        if (permissionContext == null) { return queryWrapper; }
        DataPermission dataPermission = permissionContext.getDataPermission();
        if (dataPermission == null) { return queryWrapper; }

        if (dataPermission.isAllDenied()) {
            queryWrapper.where(new QueryColumn("id").eq(-1));
            return queryWrapper;
        }
        if (dataPermission.isAllAllowed()) { return queryWrapper; }

        Set<String> ownerIds = new HashSet<>();
        Set<String> deptIds = new HashSet<>();
        boolean allowAllDataTag = false;

        List<DataPermissionGroup> groups = dataPermission.getGroups();
        if (groups == null || groups.isEmpty()) { return queryWrapper; }

        // 解析当前登录用户及其主部门ID（为空时安全回退为 null）
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        Long userDeptId = null;
        if (loginUserId != null) {
            AdminUserRespDTO user = adminUserApi.getUser(loginUserId).getCheckedData();
            userDeptId = user != null ? user.getDeptId() : null;
        }

        // 遍历数据权限组并按「标签」提取范围集合：
        // 1) ownerIds：本人提交（OWN_SUBMIT）收集当前用户ID，生成 owner_id IN (...)
        // 2) deptIds：本部门/下级部门（DEPARTMENT_SUBMIT/SUB_DEPARTMENT_SUBMIT）收集部门ID，生成 owner_dept IN (...)
        // 3) ALL_DATA：存在该标签且无其他限定集合时不追加数据权限条件，实现全量放行
        // 4) CUSTOM_CONDITION：自定义条件，处理 scopeLevel 和 filters
        List<QueryCondition> customConditions = new ArrayList<>();
        for (DataPermissionGroup group : groups) {
            List<DataPermissionTag> tags = group.getScopTags();
            if (tags == null || tags.isEmpty()) { continue; }
            for (DataPermissionTag tag : tags) {
                switch (tag) {
                    // 全量数据：记标记位；如无其他限定集合，将不追加数据权限条件
                    case ALL_DATA -> allowAllDataTag = true;
                    // 本人提交：收集当前登录用户ID，后续生成 owner_id IN (...)
                    case OWN_SUBMIT -> { if (loginUserId != null) { ownerIds.add(String.valueOf(loginUserId)); } }
                    // 本部门提交：收集当前用户主部门ID，后续生成 owner_dept IN (...)
                    case DEPARTMENT_SUBMIT -> { if (userDeptId != null) { deptIds.add(String.valueOf(userDeptId)); } }
                    // 下级部门提交：查询主部门的所有子部门并合并到 deptIds，包含主部门自身
                    case SUB_DEPARTMENT_SUBMIT -> {
                        if (userDeptId != null) {
                            List<DeptRespDTO> children = deptApi.getChildDeptList(userDeptId).getCheckedData();
                            if (children != null && !children.isEmpty()) {
                                deptIds.addAll(children.stream().map(DeptRespDTO::getId).map(String::valueOf).collect(Collectors.toSet()));
                                deptIds.add(String.valueOf(userDeptId));
                            }
                        }
                    }
                    // 自定义条件：处理 scopeLevel
                    case CUSTOM_CONDITION -> {
                        QueryCondition levelCondition = buildLevelVisibilityCondition(
                                group.getScopeLevel(), group.getScopeFieldUuid(), group.getScopeValue(),
                                loginUserId, userDeptId, fields);
                        if (levelCondition != null) {
                            customConditions.add(levelCondition);
                        }
                    }
                    default -> {}
                }
            }
        }

        // 若存在 ALL_DATA 且未收集到具体限定（既无 ownerIds 又无 deptIds 又无 customConditions），则不追加数据权限条件
        // 等价于全量放行，避免构造无意义的 WHERE 子句
        if (allowAllDataTag && ownerIds.isEmpty() && deptIds.isEmpty() && customConditions.isEmpty()) { return queryWrapper; }

        QueryWrapper perms = QueryWrapper.create();
        boolean added = false;
        if (!ownerIds.isEmpty()) {
            perms.where(new QueryColumn("owner_id").in(ownerIds));
            added = true;
        }
        if (!deptIds.isEmpty()) {
            if (!added) { perms.where(new QueryColumn("owner_dept").in(deptIds)); }
            else { perms.or(new QueryColumn("owner_dept").in(deptIds)); }
            added = true;
        }
        // 添加自定义条件（CUSTOM_CONDITION 的 scopeLevel 条件）
        for (QueryCondition customCond : customConditions) {
            if (!added) { perms.where(customCond); added = true; }
            else { perms.or(customCond); }
        }
        if (added) { queryWrapper.and(CPI.getWhereQueryCondition(perms)); }

        // 应用自定义过滤条件（filters）
        Map<String, String> fieldUuidToNameMap = buildFieldUuidToNameMap(fields);
        for (DataPermissionGroup group : groups) {
            QueryCondition filterCondition = buildFiltersCondition(group.getFilters(), fieldUuidToNameMap);
            if (filterCondition != null) {
                queryWrapper.and(filterCondition);
            }
        }

        // 字段选择已在方法开头统一应用
        return queryWrapper;
    }

    /**
     * 构建字段UUID到字段名的映射
     */
    private Map<String, String> buildFieldUuidToNameMap(List<SemanticFieldSchemaDTO> fields) {
        Map<String, String> map = new HashMap<>();
        if (fields == null) { return map; }
        for (SemanticFieldSchemaDTO f : fields) {
            if (f.getFieldUuid() != null && f.getFieldName() != null) {
                map.put(f.getFieldUuid(), f.getFieldName());
            }
        }
        return map;
    }

    /**
     * 根据自定义权限级别构建条件
     */
    private QueryCondition buildLevelVisibilityCondition(DataPermissionLevel level,
                                                         String scopeFieldUuid,
                                                         String scopeValue,
                                                         Long loginUserId,
                                                         Long userDeptId,
                                                         List<SemanticFieldSchemaDTO> fields) {
        if (level == null || scopeFieldUuid == null) {
            return null;
        }

        String fieldName = null;
        for (SemanticFieldSchemaDTO f : fields) {
            if (scopeFieldUuid.equals(f.getFieldUuid())) {
                fieldName = f.getFieldName();
                break;
            }
        }
        if (fieldName == null) {
            log.warn("CUSTOM_CONDITION: 未找到字段名：fieldUuid={}", scopeFieldUuid);
            return null;
        }

        log.debug("应用权限级别过滤：level={}, fieldName={}", level.getLabel(), fieldName);
        QueryColumn column = new QueryColumn(fieldName);

        switch (level) {
            case SELF:
                if (loginUserId != null) {
                    return column.eq(loginUserId);
                }
                return null;

            case SELF_AND_SUBORDINATES:
                if (loginUserId != null) {
                    List<AdminUserRespDTO> subs = adminUserApi.getUserListBySubordinate(loginUserId).getCheckedData();
                    Set<Long> ids = subs == null ? new HashSet<>() : subs.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                    ids.add(loginUserId);
                    return column.in(ids);
                }
                return null;

            case MAIN_DEPARTMENT:
                if (loginUserId != null && userDeptId != null) {
                    List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(Set.of(userDeptId)).getCheckedData();
                    if (users != null && !users.isEmpty()) {
                        Set<Long> userIds = users.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                        return column.in(userIds);
                    }
                }
                return null;

            case MAIN_DEPARTMENT_AND_SUBS:
                if (loginUserId != null && userDeptId != null) {
                    List<DeptRespDTO> children = deptApi.getChildDeptList(userDeptId).getCheckedData();
                    Set<Long> deptIds = children == null ? new HashSet<>() :
                            children.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
                    deptIds.add(userDeptId);

                    List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(deptIds).getCheckedData();
                    if (users != null && !users.isEmpty()) {
                        Set<Long> userIds = users.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                        return column.in(userIds);
                    }
                }
                return null;

            case SPECIFIED_DEPARTMENT:
                if (scopeValue != null && !scopeValue.isEmpty()) {
                    try {
                        List<Map<String, Object>> scopeList = JsonUtils.parseObject(scopeValue, new TypeReference<List<Map<String, Object>>>() {});
                        if (scopeList != null && !scopeList.isEmpty()) {
                            Set<Long> deptIds = scopeList.stream()
                                    .map(m -> m.get("key"))
                                    .filter(Objects::nonNull)
                                    .map(Convert::toLong)
                                    .collect(Collectors.toSet());
                            if (!deptIds.isEmpty()) {
                                List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(deptIds).getCheckedData();
                                if (users != null && !users.isEmpty()) {
                                    Set<Long> userIds = users.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                                    return column.in(userIds);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("解析指定部门scopeValue失败: {}", e.getMessage());
                    }
                }
                return null;

            case SPECIFIED_PERSON:
                if (scopeValue != null && !scopeValue.isEmpty()) {
                    try {
                        List<Map<String, Object>> scopeList = JsonUtils.parseObject(scopeValue, new TypeReference<List<Map<String, Object>>>() {});
                        if (scopeList != null && !scopeList.isEmpty()) {
                            Set<Long> userIds = scopeList.stream()
                                    .map(m -> m.get("key"))
                                    .filter(Objects::nonNull)
                                    .map(Convert::toLong)
                                    .collect(Collectors.toSet());
                            if (!userIds.isEmpty()) {
                                return column.in(userIds);
                            }
                        }
                    } catch (Exception e) {
                        log.error("解析指定人员scopeValue失败: {}", e.getMessage());
                    }
                }
                return null;

            default:
                log.warn("未知的权限级别：{}", level);
                return null;
        }
    }

    /**
     * 构建自定义过滤条件（外层AND、内层OR）
     */
    private QueryCondition buildFiltersCondition(List<List<DataPermissionFilter>> filters,
                                                 Map<String, String> fieldUuidToNameMap) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }

        QueryCondition andCondition = null;
        for (List<DataPermissionFilter> orGroup : filters) {
            QueryCondition orCondition = null;
            if (orGroup != null) {
                for (DataPermissionFilter filter : orGroup) {
                    String fieldName = fieldUuidToNameMap.get(filter.getFieldUuid());
                    if (fieldName == null) {
                        log.warn("过滤条件未找到字段名：fieldUuid={}", filter.getFieldUuid());
                        continue;
                    }
                    QueryCondition filterCond = buildSingleFilterCondition(fieldName, filter, fieldUuidToNameMap);
                    if (filterCond != null) {
                        orCondition = orCondition == null ? filterCond : orCondition.or(filterCond);
                    }
                }
            }
            if (orCondition != null) {
                andCondition = andCondition == null ? orCondition : andCondition.and(orCondition);
            }
        }
        return andCondition;
    }

    /**
     * 构建单个过滤条件
     */
    private QueryCondition buildSingleFilterCondition(String fieldName,
                                                      DataPermissionFilter filter,
                                                      Map<String, String> fieldUuidToNameMap) {
        String operator = filter.getFieldOperator();
        String filterValue = filter.getFieldValue();
        String filterValueType = filter.getFieldValueType();

        log.debug("应用过滤条件：field={}, operator={}, valueType={}, value={}",
                fieldName, operator, filterValueType, filterValue);

        if (operator == null) {
            log.warn("操作符为空，跳过过滤条件");
            return null;
        }

        QueryColumn column = new QueryColumn(fieldName);
        String compareToValue = null;
        String compareToFieldName = null;

        if ("value".equals(filterValueType)) {
            compareToValue = filterValue;
        } else if ("variables".equals(filterValueType)) {
            String variableExpr = filterValue != null ? filterValue.trim() : null;
            if (variableExpr != null && variableExpr.contains(".")) {
                String[] parts = variableExpr.split("\\.");
                if (parts.length == 2) {
                    String refFieldUuid = parts[1];
                    compareToFieldName = fieldUuidToNameMap.get(refFieldUuid);
                    if (compareToFieldName == null) {
                        log.error("变量解析失败，未找到字段名：fieldUuid={}", refFieldUuid);
                        return null;
                    }
                } else {
                    log.error("变量表达式格式不正确：{}", variableExpr);
                    return null;
                }
            } else {
                log.error("变量表达式未识别：{}", variableExpr);
                return null;
            }
        } else if ("formula".equals(filterValueType)) {
            log.error("暂不支持公式类型的数据权限过滤");
            return null;
        }

        switch (operator.toUpperCase()) {
            case "EQUALS":
            case "EQUAL":
            case "EQ":
            case "=":
                if (compareToFieldName != null) {
                    return column.eq(new QueryColumn(compareToFieldName));
                } else {
                    return column.eq(compareToValue);
                }

            case "NOT_EQUALS":
            case "NOT_EQUAL":
            case "NE":
            case "!=":
                if (compareToFieldName != null) {
                    return column.ne(new QueryColumn(compareToFieldName));
                } else {
                    return column.ne(compareToValue);
                }

            case "IN":
                if (compareToValue != null) {
                    List<String> values = Arrays.asList(compareToValue.split(","));
                    return column.in(values.stream().map(String::trim).collect(Collectors.toList()));
                }
                return null;

            case "NIN":
            case "NOT_IN":
                if (compareToValue != null) {
                    List<String> values = Arrays.asList(compareToValue.split(","));
                    return column.notIn(values.stream().map(String::trim).collect(Collectors.toList()));
                }
                return null;

            case "IS_EMPTY":
                return column.isNull().or(column.eq(""));

            case "IS_NOT_EMPTY":
                return column.isNotNull().and(column.ne(""));

            default:
                log.warn("未知的操作符：{}", operator);
                return null;
        }
    }

    /**
     * 对查询结果列表进行字段权限过滤
     *
     * - 全部拒绝：仅保留系统字段
     * - 全部允许：不做过滤
     * - 其他：保留系统字段与可读字段
     *
     * @param dataList 查询结果列表
     * @param permissionContext 权限上下文
     * @param fields 语义字段列表
     * @return 过滤后的结果列表
     */
    public List<Map<String, Object>> filterQueryResultList(List<Map<String, Object>> dataList,
                                                           SemanticPermissionContext permissionContext,
                                                           List<SemanticFieldSchemaDTO> fields) {
        if (permissionContext == null) { return dataList; }
        FieldPermission fp = permissionContext.getFieldPermission();
        if (fp == null) { return dataList; }
        Set<Long> readableIds = fp.getFields() == null ? Collections.emptySet() :
                fp.getFields().stream().filter(FieldPermissionItem::isCanRead).map(FieldPermissionItem::getFieldId).collect(Collectors.toSet());
        if (fp.isAllDenied()) {
            List<String> systemFields = fields.stream()
                    .filter(SemanticQueryPermissionHelper::isSystemField)
                    .map(SemanticFieldSchemaDTO::getFieldName)
                    .toList();
            return dataList.stream().map(map -> retainKeys(map, new HashSet<>(systemFields))).collect(Collectors.toList());
        }
        if (fp.isAllAllowed()) { return dataList; }
        Set<String> allowedNames = fields.stream()
                .filter(f -> isSystemField(f) || readableIds.contains(f.getId()))
                .map(SemanticFieldSchemaDTO::getFieldName).collect(Collectors.toSet());
        return dataList.stream().map(map -> retainKeys(map, allowedNames)).collect(Collectors.toList());
    }

    /**
     * 计算可查询字段名列表，用于 QueryWrapper 列裁剪
     *
     * - 全部允许：返回所有字段
     * - 全部拒绝：返回系统字段
     * - 其他：返回系统字段与可读字段
     *
     * @param fieldPermission 字段权限
     * @param fields 语义字段列表
     * @return 可查询字段名列表
     */
    public List<String> getQueryableFieldNames(FieldPermission fieldPermission,
                                               List<SemanticFieldSchemaDTO> fields) {
        if (fieldPermission == null || fieldPermission.isAllAllowed()) {
            return fields.stream().map(SemanticFieldSchemaDTO::getFieldName).toList();
        }
        if (fieldPermission.isAllDenied()) {
            return fields.stream().filter(SemanticQueryPermissionHelper::isSystemField)
                    .map(SemanticFieldSchemaDTO::getFieldName).toList();
        }
        Set<Long> readableIds = fieldPermission.getFields() == null ? Collections.emptySet() :
                fieldPermission.getFields().stream().filter(FieldPermissionItem::isCanRead).map(FieldPermissionItem::getFieldId).collect(Collectors.toSet());
        return fields.stream().filter(f -> isSystemField(f) || readableIds.contains(f.getId()))
                .map(SemanticFieldSchemaDTO::getFieldName).toList();
    }

    /**
     * 判断字段是否为系统字段
     */
    private static boolean isSystemField(SemanticFieldSchemaDTO f) {
        return f.getIsSystemField();
    }

    /**
     * 仅保留允许的键
     */
    private static Map<String, Object> retainKeys(Map<String, Object> src, Set<String> allowed) {
        Map<String, Object> ret = new HashMap<>();
        for (Map.Entry<String, Object> e : src.entrySet()) {
            String k = e.getKey();
            if (k != null && allowed.contains(k)) { ret.put(k, e.getValue()); }
        }
        return ret;
    }
}
