package com.cmsr.onebase.module.metadata.core.service.permission.filter;

import cn.hutool.core.convert.Convert;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionFilter;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionLevel;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionTag;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 数据权限过滤条件构建器
 *
 * 根据数据权限配置，构建 MyBatis-Flex QueryWrapper 查询条件
 * 用于在查询时过滤用户无权访问的数据
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class DataPermissionFilterBuilder {

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    /**
     * 应用数据权限过滤
     *
     * @param queryWrapper MyBatis-Flex 查询包装器
     * @param dataPermission 数据权限配置
     * @param loginUserCtx 当前登录用户
     * @param fields 实体字段列表
     */
    public void applyDataPermissionFilter(QueryWrapper queryWrapper,
                                          DataPermission dataPermission,
                                          LoginUserCtx loginUserCtx,
                                          List<MetadataEntityFieldDO> fields) {
        if (dataPermission == null) {
            log.info("数据权限对象为空，不应用数据权限过滤");
            return;
        }

        // 全部拒绝：添加永假条件
        if (dataPermission.isAllDenied()) {
            log.info("数据权限：全部拒绝，添加1=0条件");
            queryWrapper.and("1 = 0");
            return;
        }

        // 全部允许：不添加任何过滤条件
        if (dataPermission.isAllAllowed()) {
            log.info("数据权限：全部允许，不添加过滤条件");
            return;
        }

        // 处理数据权限组
        List<DataPermissionGroup> groups = dataPermission.getGroups();
        if (groups == null || groups.isEmpty()) {
            log.info("数据权限组为空，不应用数据权限过滤");
            return;
        }

        log.info("开始构建数据权限过滤条件：groupCount={}, userId={}",
                groups.size(),
                loginUserCtx != null ? loginUserCtx.getUserId() : "unknown");

        // 构建字段名映射（fieldId -> fieldName）
        Map<Long, String> fieldIdToNameMap = fields.stream()
                .collect(Collectors.toMap(
                        MetadataEntityFieldDO::getId,
                        MetadataEntityFieldDO::getFieldName
                ));

        // 多个权限组之间 OR 组合
        List<QueryCondition> groupConditions = new ArrayList<>();
        for (DataPermissionGroup group : groups) {
            QueryCondition groupCondition = buildSingleGroupCondition(group, loginUserCtx, fieldIdToNameMap);
            if (groupCondition != null) {
                groupConditions.add(groupCondition);
            }
        }

        // 将所有组条件以OR组合后AND到主查询
        if (!groupConditions.isEmpty()) {
            QueryCondition combinedCondition = groupConditions.get(0);
            for (int i = 1; i < groupConditions.size(); i++) {
                combinedCondition = combinedCondition.or(groupConditions.get(i));
            }
            queryWrapper.and(combinedCondition);
        }
    }

    /**
     * 构建单个权限组对应的 QueryCondition
     *
     * @param group 权限组
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 查询条件
     */
    private QueryCondition buildSingleGroupCondition(DataPermissionGroup group,
                                                     LoginUserCtx loginUserCtx,
                                                     Map<Long, String> fieldIdToNameMap) {
        log.debug("构建权限组过滤：scopeLevel={}, scopeFieldId={}",
                group.getScopeLevel(), group.getScopeFieldId());

        List<QueryCondition> visibilityConditions = new ArrayList<>();

        // tags 可见性
        List<DataPermissionTag> tags = group.getScopTags();
        if (tags != null && !tags.isEmpty()) {
            List<QueryCondition> tagConditions = buildTagVisibilityConditions(group, tags, loginUserCtx, fieldIdToNameMap);
            visibilityConditions.addAll(tagConditions);
        }

        // 把所有备选 OR 成一个组条件
        QueryCondition groupOr = null;
        for (QueryCondition condition : visibilityConditions) {
            if (groupOr == null) {
                groupOr = condition;
            } else {
                groupOr = groupOr.or(condition);
            }
        }

        // 应用自定义过滤条件
        QueryCondition filterCondition = buildFiltersCondition(group.getFilters(), fieldIdToNameMap);

        // 组合groupOr和filterCondition
        if (groupOr != null && filterCondition != null) {
            return groupOr.and(filterCondition);
        } else if (groupOr != null) {
            return groupOr;
        } else if (filterCondition != null) {
            return filterCondition;
        }
        return null;
    }

    /**
     * 根据权限标签构建可见性条件
     *
     * @param group 权限组
     * @param tags 权限标签列表
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 条件列表
     */
    private List<QueryCondition> buildTagVisibilityConditions(DataPermissionGroup group,
                                                              List<DataPermissionTag> tags,
                                                              LoginUserCtx loginUserCtx,
                                                              Map<Long, String> fieldIdToNameMap) {
        List<QueryCondition> conditions = new ArrayList<>();

        for (DataPermissionTag tag : tags) {
            switch (tag) {
                case ALL_DATA:
                    log.debug("权限标签：全部数据");
                    conditions.add(QueryCondition.createEmpty().and("1 = 1"));
                    break;

                case OWN_SUBMIT:
                    if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                        QueryColumn creatorColumn = new QueryColumn("creator");
                        conditions.add(creatorColumn.eq(loginUserCtx.getUserId()));
                        log.debug("权限标签：本人提交 creator={}", loginUserCtx.getUserId());
                    }
                    break;

                case DEPARTMENT_SUBMIT:
                    if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                        AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                        if (user != null && user.getDeptId() != null) {
                            QueryColumn ownerDeptColumn = new QueryColumn("owner_dept");
                            conditions.add(ownerDeptColumn.eq(user.getDeptId()));
                            log.debug("权限标签：本部门提交 owner_dept={}", user.getDeptId());
                        }
                    }
                    break;

                case SUB_DEPARTMENT_SUBMIT:
                    if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                        AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                        if (user != null && user.getDeptId() != null) {
                            List<DeptRespDTO> children = deptApi.getChildDeptList(user.getDeptId()).getCheckedData();
                            if (children != null && !children.isEmpty()) {
                                Set<Long> deptIds = children.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
                                QueryColumn ownerDeptColumn = new QueryColumn("owner_dept");
                                conditions.add(ownerDeptColumn.in(deptIds));
                                log.debug("权限标签：下级部门提交 IN {}", deptIds.size());
                            }
                        }
                    }
                    break;

                case CUSTOM_CONDITION:
                    QueryCondition levelCondition = buildLevelVisibilityCondition(group.getScopeLevel(),
                            group.getScopeFieldId(), group.getScopeValue(), loginUserCtx, fieldIdToNameMap);
                    if (levelCondition != null) {
                        conditions.add(levelCondition);
                    }
                    break;

                default:
                    log.warn("未知的权限标签：{}", tag);
            }
        }
        return conditions;
    }

    /**
     * 根据自定义权限构建条件
     *
     * @param level 权限级别
     * @param scopeFieldId 范围字段ID
     * @param scopeValue 范围值
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 查询条件
     */
    private QueryCondition buildLevelVisibilityCondition(DataPermissionLevel level,
                                                         Long scopeFieldId,
                                                         String scopeValue,
                                                         LoginUserCtx loginUserCtx,
                                                         Map<Long, String> fieldIdToNameMap) {
        if (level == null || scopeFieldId == null) {
            return null;
        }

        String fieldName = fieldIdToNameMap.get(scopeFieldId);
        if (fieldName == null) {
            log.warn("未找到字段名：fieldId={}", scopeFieldId);
            return null;
        }

        log.debug("应用权限级别过滤：level={}, fieldName={}", level.getLabel(), fieldName);
        QueryColumn column = new QueryColumn(fieldName);

        switch (level) {
            case SELF:
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    return column.eq(loginUserCtx.getUserId());
                }
                return null;

            case SELF_AND_SUBORDINATES:
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    List<AdminUserRespDTO> subs = adminUserApi.getUserListBySubordinate(loginUserCtx.getUserId()).getCheckedData();
                    Set<Long> ids = subs == null ? new HashSet<>() : subs.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                    ids.add(loginUserCtx.getUserId());
                    return column.in(ids);
                }
                return null;

            case MAIN_DEPARTMENT:
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                    if (user != null && user.getDeptId() != null) {
                        List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(Set.of(user.getDeptId())).getCheckedData();
                        if (users != null && !users.isEmpty()) {
                            Set<Long> userIds = users.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                            return column.in(userIds);
                        }
                    }
                }
                return null;

            case MAIN_DEPARTMENT_AND_SUBS:
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                    if (user != null && user.getDeptId() != null) {
                        List<DeptRespDTO> children = deptApi.getChildDeptList(user.getDeptId()).getCheckedData();
                        Set<Long> deptIds = children == null ? new HashSet<>() :
                                children.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
                        deptIds.add(user.getDeptId());

                        List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(deptIds).getCheckedData();
                        if (users != null && !users.isEmpty()) {
                            Set<Long> userIds = users.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                            return column.in(userIds);
                        }
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
     * 构建自定义过滤条件（外层OR、内层AND）
     *
     * @param filters 过滤条件列表
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 查询条件
     */
    private QueryCondition buildFiltersCondition(List<List<DataPermissionFilter>> filters,
                                                 Map<Long, String> fieldIdToNameMap) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }

        QueryCondition orCondition = null;
        for (List<DataPermissionFilter> andGroup : filters) {
            QueryCondition andCondition = null;
            if (andGroup != null) {
                for (DataPermissionFilter filter : andGroup) {
                    String fieldName = fieldIdToNameMap.get(filter.getFieldId());
                    if (fieldName == null) {
                        log.warn("未找到字段名：fieldId={}", filter.getFieldId());
                        continue;
                    }
                    QueryCondition filterCond = buildSingleFilterCondition(fieldName, filter, fieldIdToNameMap);
                    if (filterCond != null) {
                        andCondition = andCondition == null ? filterCond : andCondition.and(filterCond);
                    }
                }
            }
            if (andCondition != null) {
                orCondition = orCondition == null ? andCondition : orCondition.or(andCondition);
            }
        }
        return orCondition;
    }

    /**
     * 构建单个过滤条件
     *
     * @param fieldName 字段名
     * @param filter 过滤条件
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 查询条件
     */
    private QueryCondition buildSingleFilterCondition(String fieldName,
                                                      DataPermissionFilter filter,
                                                      Map<Long, String> fieldIdToNameMap) {
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
                    try {
                        Long refFieldId = Convert.toLong(parts[1]);
                        compareToFieldName = fieldIdToNameMap.get(refFieldId);
                        if (compareToFieldName == null) {
                            log.error("变量解析失败，未找到字段名：fieldId={}", refFieldId);
                            return null;
                        }
                    } catch (Exception ex) {
                        log.error("变量解析失败：{}", variableExpr);
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
                if (compareToFieldName != null) {
                    return column.eq(new QueryColumn(compareToFieldName));
                } else {
                    return column.eq(compareToValue);
                }

            case "NOT_EQUALS":
            case "NOT_EQUAL":
                if (compareToFieldName != null) {
                    return column.ne(new QueryColumn(compareToFieldName));
                } else {
                    return column.ne(compareToValue);
                }

            case "IS_EMPTY":
                // 字段为空：fieldName IS NULL OR fieldName = ''
                return column.isNull().or(column.eq(""));

            case "IS_NOT_EMPTY":
                // 字段不为空：fieldName IS NOT NULL AND fieldName != ''
                return column.isNotNull().and(column.ne(""));

            default:
                log.warn("未知的操作符：{}", operator);
                return null;
        }
    }
}

