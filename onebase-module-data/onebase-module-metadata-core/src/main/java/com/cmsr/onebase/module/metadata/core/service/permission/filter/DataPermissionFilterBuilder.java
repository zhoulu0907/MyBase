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
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 数据权限过滤条件构建器
 *
 * 根据数据权限配置，构建 Anyline ConfigStore 查询条件
 * 用于在查询时过滤用户无权访问的数据
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class DataPermissionFilterBuilder {

    /**
     * 用户服务
     */
    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 部门服务
     */
    @Resource
    private DeptApi deptApi;

    /**
     * 应用数据权限过滤
     *
     * 根据数据权限配置，向 ConfigStore 添加相应的查询条件
     *
     * @param configStore Anyline 查询配置
     * @param dataPermission 数据权限配置
     * @param loginUserCtx 当前登录用户
     * @param fields 实体字段列表
     */
    public void applyDataPermissionFilter(ConfigStore configStore,
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
            configStore.and("1 = 0"); // 添加永假条件，查询不到任何数据
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

        // 多个权限组之间 OR 组合：为每个组构建一个子 store，然后 OR 到主 store
        ConfigStore groupStores = new DefaultConfigStore();
        for (DataPermissionGroup group : groups) {
            ConfigStore groupStore = buildSingleGroupStore(group, loginUserCtx, fieldIdToNameMap);
            if (groupStore != null) {
                groupStores.or(groupStore);
            }
        }
        // configs.and(Compare.EQUAL, "deleted", 0);
        configStore.and(groupStores);
    }

    /**
     * 构建单个权限组对应的 ConfigStore（用于 OR 组合）
     *
     * @param group 权限组
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 子条件存储（用于 OR 拼接）
     */
    private ConfigStore buildSingleGroupStore(DataPermissionGroup group,
                                              LoginUserCtx loginUserCtx,
                                              Map<Long, String> fieldIdToNameMap) {
        log.debug("构建权限组过滤：scopeLevel={}, scopeFieldId={}",
                group.getScopeLevel(),
                group.getScopeFieldId());

        // 1. 根据权限标签应用过滤
        // 目标：实现 (可见性[tags或level] OR 可见性...) AND filters
        // 做法：为每个可见性备选创建子 store，并把 filters AND 到该子 store，最后将这些子 store 以 OR 组合
        List<ConfigStore> visibilityAlternatives = new ArrayList<>();

        // tags 可见性（每个tag生成一个子store）
        List<DataPermissionTag> tags = group.getScopTags();
        if (tags != null && !tags.isEmpty()) {
            List<ConfigStore> tagStores = buildTagVisibilityStores(group, tags, loginUserCtx, fieldIdToNameMap);
            visibilityAlternatives.addAll(tagStores);
        }

          // 2. 根据权限级别和范围值应用过滤
//        if (group.getScopeLevel() != null && group.getScopeFieldId() != null) {
//            ConfigStore levelStore = buildLevelVisibilityStore(group.getScopeLevel(),
//                    group.getScopeFieldId(), group.getScopeValue(), loginUserCtx, fieldIdToNameMap);
//            if (levelStore != null) {
//                visibilityAlternatives.add(levelStore);
//            }
//        }

        // 把所有备选 OR 成一个组store
        DefaultConfigStore groupOr = new DefaultConfigStore();
        for (ConfigStore alt : visibilityAlternatives) {
            groupOr.or(alt);

        }
        // 3. 应用自定义过滤条件
        DefaultConfigStore groupStore = new DefaultConfigStore();
        groupStore.and(groupOr);

        applyFiltersAnd(groupStore, group.getFilters(), fieldIdToNameMap);
        return groupStore;
    }

    /**
     * 根据权限标签构建可见性子条件（每个标签一组）
     *
     * @param tags 权限标签列表
     * @param loginUserCtx 当前登录用户
     * @return 子条件列表
     */
    private List<ConfigStore> buildTagVisibilityStores(DataPermissionGroup group,
                                                       List<DataPermissionTag> tags,
                                                       LoginUserCtx loginUserCtx,
                                                       Map<Long, String> fieldIdToNameMap) {
        List<ConfigStore> stores = new ArrayList<>();

        for (DataPermissionTag tag : tags) {
            switch (tag) {
                case ALL_DATA:
                    // 全量数据：用一个空条件的子 store 表示“始终可见”
                    log.debug("权限标签：全部数据");
                    DefaultConfigStore configStore = new DefaultConfigStore();
                    configStore.and("1 = 1");
                    stores.add(configStore);
                    break;

                case OWN_SUBMIT:
                    // 本人提交：添加 creator_id = 当前用户ID 的条件
                    if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                        DefaultConfigStore s = new DefaultConfigStore();
                        s.and(Compare.EQUAL, "creator", loginUserCtx.getUserId());
                        stores.add(s);
                        log.debug("权限标签：本人提交 creator={}", loginUserCtx.getUserId());
                    }
                    break;

                case DEPARTMENT_SUBMIT:
                    // 本部门提交：owner_dept = 当前用户部门ID
                    if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                        AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                        if (user != null && user.getDeptId() != null) {
                            DefaultConfigStore s = new DefaultConfigStore();
                            s.and(Compare.EQUAL, "owner_dept", user.getDeptId());
                            stores.add(s);
                            log.debug("权限标签：本部门提交 owner_dept={}", user.getDeptId());
                        }
                    }
                    break;

                case SUB_DEPARTMENT_SUBMIT:
                    // 下级部门提交：owner_dept IN 子部门集合
                    if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                        AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                        if (user != null && user.getDeptId() != null) {
                            List<DeptRespDTO> children = deptApi.getChildDeptList(user.getDeptId()).getCheckedData();
                            if (children != null && !children.isEmpty()) {
                                Set<Long> deptIds = children.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
                                DefaultConfigStore s = new DefaultConfigStore();
                                s.and(Compare.IN, "owner_dept", deptIds);
                                stores.add(s);
                                log.debug("权限标签：下级部门提交 IN {}", deptIds.size());
                            }
                        }
                    }
                    break;

                case CUSTOM_CONDITION:
                    // 自定义条件：不在此处处理，由 filters 统一追加
                    ConfigStore levelStore = buildLevelVisibilityStore(group.getScopeLevel(),
                            group.getScopeFieldId(), group.getScopeValue(), loginUserCtx, fieldIdToNameMap);
                    stores.add(levelStore);
                    break;

                default:
                    log.warn("未知的权限标签：{}", tag);
            }
        }
        return stores;
    }

    /**
     * 根据自定义权限拼接sql
     *
     * @param level 权限级别
     * @param scopeFieldId 范围字段ID
     * @param scopeValue 范围值（JSON字符串）
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 子条件（可能为null）
     */
    private ConfigStore buildLevelVisibilityStore(DataPermissionLevel level,
                                                  Long scopeFieldId,
                                                  String scopeValue,
                                                  LoginUserCtx loginUserCtx,
                                                  Map<Long, String> fieldIdToNameMap) {

        String fieldName = fieldIdToNameMap.get(scopeFieldId);
        if (fieldName == null) {
            log.warn("未找到字段名：fieldId={}", scopeFieldId);
            return null;
        }

        log.debug("应用权限级别过滤：level={}, fieldName={}", level.getLabel(), fieldName);

        DefaultConfigStore s = new DefaultConfigStore();

        switch (level) {
            case SELF:
                // 本人：字段值 = 当前用户ID
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    s.and(Compare.EQUAL, fieldName, loginUserCtx.getUserId());
                    log.debug("权限级别：本人，添加 {}={}", fieldName, loginUserCtx.getUserId());
                }
                return s;

            case SELF_AND_SUBORDINATES:
                // 本人及下属：查询下属列表，字段 IN (本人 + 下属)
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    List<AdminUserRespDTO> subs = adminUserApi.getUserListBySubordinate(loginUserCtx.getUserId()).getCheckedData();
                    Set<Long> ids = subs == null ? Set.of() : subs.stream().map(AdminUserRespDTO::getId).collect(Collectors.toSet());
                    // 包含本人
                    ids.add(loginUserCtx.getUserId());
                    s.and(Compare.IN, fieldName, ids);
                    log.debug("权限级别：本人及下属，{} IN {}", fieldName, ids.size());
                    return s;
                }
                return null;

            case MAIN_DEPARTMENT:
                // 当前员工所在主部门：通过字段值（用户ID）查询该用户的部门，判断是否与当前用户部门一致
                // 需要先获取当前用户的部门，然后查询该部门下的所有用户，最后字段 IN 这些用户ID
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                    if (user != null && user.getDeptId() != null) {
                        // 查询当前用户部门下的所有用户
                        List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(Set.of(user.getDeptId())).getCheckedData();
                        if (users != null && !users.isEmpty()) {
                            Set<Long> userIds = users.stream()
                                    .map(AdminUserRespDTO::getId)
                                    .collect(Collectors.toSet());
                            s.and(Compare.IN, fieldName, userIds);
                            log.debug("权限级别：主部门，查询到{}个用户，{} IN {}",
                                    userIds.size(), fieldName, userIds.size());
                            return s;
                        } else {
                            log.debug("权限级别：主部门，未查询到符合条件的用户");
                            return null;
                        }
                    }
                }
                return null;

            case MAIN_DEPARTMENT_AND_SUBS:
                // 当前员工所在主部门及下级部门：通过字段值（用户ID）查询该用户的部门，判断是否在当前用户的主部门及下级部门列表中
                // 需要先获取允许的部门列表，然后查询这些部门下的所有用户，最后字段 IN 这些用户ID
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    AdminUserRespDTO user = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
                    if (user != null && user.getDeptId() != null) {
                        // 获取当前用户的主部门及下级部门列表
                        List<DeptRespDTO> children = deptApi.getChildDeptList(user.getDeptId()).getCheckedData();
                        Set<Long> deptIds = children == null ? new java.util.HashSet<>() :
                                children.stream().map(DeptRespDTO::getId).collect(Collectors.toSet());
                        deptIds.add(user.getDeptId());

                        // 根据部门ID列表查询所有用户
                        List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(deptIds).getCheckedData();
                        if (users != null && !users.isEmpty()) {
                            Set<Long> userIds = users.stream()
                                    .map(AdminUserRespDTO::getId)
                                    .collect(Collectors.toSet());
                            s.and(Compare.IN, fieldName, userIds);
                            log.debug("权限级别：主部门及下级，查询到{}个部门下的{}个用户，{} IN {}",
                                    deptIds.size(), userIds.size(), fieldName, userIds.size());
                            return s;
                        } else {
                            log.debug("权限级别：主部门及下级，未查询到符合条件的用户");
                            return null;
                        }
                    }
                }
                return null;

            case SPECIFIED_DEPARTMENT:
                // 指定部门：从 scopeValue 解析部门列表，然后查询这些部门下的所有用户，最后字段 IN 这些用户ID
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
                                // 根据部门ID列表查询所有用户
                                List<AdminUserRespDTO> users = adminUserApi.getUserListByDeptIds(deptIds).getCheckedData();
                                if (users != null && !users.isEmpty()) {
                                    Set<Long> userIds = users.stream()
                                            .map(AdminUserRespDTO::getId)
                                            .collect(Collectors.toSet());
                                    s.and(Compare.IN, fieldName, userIds);
                                    log.debug("权限级别：指定部门，查询到{}个部门下的{}个用户，{} IN {}",
                                            deptIds.size(), userIds.size(), fieldName, userIds.size());
                                    return s;
                                } else {
                                    log.debug("权限级别：指定部门，未查询到符合条件的用户");
                                    return null;
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("解析指定部门scopeValue失败: {}", e.getMessage());
                    }
                }
                return null;

            case SPECIFIED_PERSON:
                // 指定人员：从 scopeValue 解析人员列表
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
                                s.and(Compare.IN, fieldName, userIds);
                                return s;
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
     * 在指定的 store 上追加 AND 型自定义过滤条件
     *
     * @param filters 自定义过滤条件（二维列表，外层OR，内层AND）
     * @param fieldIdToNameMap 字段ID到字段名的映射
     */
    private void applyFiltersAnd(ConfigStore store,
                                 List<List<DataPermissionFilter>> filters,
                                 Map<Long, String> fieldIdToNameMap) {
        if (filters == null || filters.isEmpty()) {
            return;
        }
        // 外层 OR、内层 AND：将 OR 的每组条件做成子 store，并 OR 到目标 store
        DefaultConfigStore father = new DefaultConfigStore();
        for (List<DataPermissionFilter> andGroup : filters) {
            DefaultConfigStore sub = new DefaultConfigStore();
            if (andGroup != null) {
                for (DataPermissionFilter filter : andGroup) {
                    String fieldName = fieldIdToNameMap.get(filter.getFieldId());
                    if (fieldName == null) {
                        log.warn("未找到字段名：fieldId={}", filter.getFieldId());
                        continue;
                    }
                    applyFilterCondition(sub, fieldName, filter, fieldIdToNameMap);
                }
            }
            father.or(sub);
        }
        store.and(father);
    }

    /**
     * 应用单个过滤条件
     * @param configStore Anyline 查询配置
     * @param fieldName 字段名
     * @param filter 过滤条件
     * @param fieldIdToNameMap 字段ID到字段名的映射（用于解析 variables 类型）
     */
    private void applyFilterCondition(ConfigStore configStore,
                                      String fieldName,
                                      DataPermissionFilter filter,
                                      Map<Long, String> fieldIdToNameMap) {

        String operator = filter.getFieldOperator();
        String filterValue = filter.getFieldValue();
        String filterValueType = filter.getFieldValueType();

        log.debug("应用过滤条件：field={}, operator={}, valueType={}, value={}",
                fieldName, operator, filterValueType, filterValue);

        if (operator == null) {
            log.warn("操作符为空，跳过过滤条件");
            return;
        }

        // 根据 fieldValueType 判断类型，获取比较值
        String compareToValue = null;
        String compareToFieldName = null; // 用于字段间比较

        if ("value".equals(filterValueType)) {
            // 直接使用 fieldValue 作为比较值
            compareToValue = filterValue;
        } else if ("variables".equals(filterValueType)) {
            // 变量类型：期望形如 entity-123945626659094528.123950299583512578
            // 解析出引用的字段ID，然后获取字段名，用于字段间比较
            String variableExpr = filterValue != null ? filterValue.trim() : null;
            if (variableExpr != null && variableExpr.contains(".")) {
                String[] parts = variableExpr.split("\\.");
                if (parts.length == 2) {
                    String fieldIdPart = parts[1];
                    try {
                        Long refFieldId = Convert.toLong(fieldIdPart);
                        compareToFieldName = fieldIdToNameMap.get(refFieldId).toString();
                        if (compareToFieldName == null) {
                            log.error("变量解析失败，未找到字段名：fieldId={}, variableExpr={}", refFieldId, variableExpr);
                            return;
                        }
                        log.debug("变量解析成功：variableExpr={}, refFieldName={}", variableExpr, compareToFieldName);
                    } catch (Exception ex) {
                        log.error("变量解析失败，期望 entity-<tableId>.<fieldId>，实际={}", variableExpr);
                        return;
                    }
                } else {
                    log.error("变量表达式格式不正确，期望 entity-<tableId>.<fieldId>，实际={}", variableExpr);
                    return;
                }
            } else {
                log.error("变量表达式未识别，期望以 entity- 开头且包含 '.' 分隔，实际={}", variableExpr);
                return;
            }
        } else if ("formula".equals(filterValueType)) {
            log.error("暂不支持公式类型(fieldValueType=formula)的数据权限过滤");
            return;
        }

        // 根据操作类型进行匹配（只支持 EQUALS、NOT_EQUALS、IS_EMPTY、IS_NOT_EMPTY）
        switch (operator.toUpperCase()) {
            case "EQUALS":
            case "EQUAL":
                if (compareToFieldName != null) {
                    // 字段间比较：fieldName = compareToFieldName
                    configStore.and(Compare.EQUAL, fieldName, compareToFieldName);
                } else {
                    // 值与字段比较：fieldName = compareToValue
                    configStore.and(Compare.EQUAL, fieldName, compareToValue);
                }
                break;

            case "NOT_EQUALS":
            case "NOT_EQUAL":
                if (compareToFieldName != null) {
                    // 字段间比较：fieldName != compareToFieldName
                    configStore.and(Compare.NOT_EQUAL, fieldName, compareToFieldName);
                } else {
                    // 值与字段比较：fieldName != compareToValue
                    configStore.and(Compare.NOT_EQUAL, fieldName, compareToValue);
                }
                break;

            case "IS_EMPTY":
                // 字段为空：fieldName IS NULL OR fieldName = ''
                // 使用 OR 组合两个条件
                DefaultConfigStore emptyStore = new DefaultConfigStore();
                emptyStore.and(Compare.NULL, fieldName, null);
                DefaultConfigStore emptyStringStore = new DefaultConfigStore();
                emptyStringStore.and(Compare.EQUAL, fieldName, "");
                emptyStore.or(emptyStringStore);
                configStore.and(emptyStore);
                break;

            case "IS_NOT_EMPTY":
                // 字段不为空：fieldName IS NOT NULL AND fieldName != ''
                // 使用 AND 组合两个条件
                configStore.and(Compare.NOT_NULL, fieldName, null);
                configStore.and(Compare.NOT_EQUAL, fieldName, "");
                break;

            default:
                log.warn("未知的操作符：{}，仅支持 EQUALS、NOT_EQUALS、IS_EMPTY、IS_NOT_EMPTY", operator);
                return;
        }
    }
}

