package com.cmsr.onebase.module.metadata.core.service.permission.filter;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionFilter;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionLevel;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionTag;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            log.warn("数据权限对象为空，不应用数据权限过滤");
            return;
        }

        // 全部拒绝：添加永假条件
        if (dataPermission.isAllDenied()) {
            log.info("数据权限：全部拒绝，添加1=0条件");
            configStore.and("1", 0); // 添加永假条件，查询不到任何数据
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
            log.warn("数据权限组为空，不应用数据权限过滤");
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

        // 多个权限组之间是 OR 关系
        // TODO: 当前简化实现，暂时只取第一个权限组
        // 完整实现需要支持多个权限组的 OR 条件组合
        for (DataPermissionGroup group : groups) {
            applyGroupFilter(configStore, group, loginUserCtx, fieldIdToNameMap);
            break; // 暂时只处理第一个权限组
        }
    }

    /**
     * 应用单个权限组的过滤条件
     *
     * @param configStore Anyline 查询配置
     * @param group 权限组
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     */
    private void applyGroupFilter(ConfigStore configStore,
                                   DataPermissionGroup group,
                                   LoginUserCtx loginUserCtx,
                                   Map<Long, String> fieldIdToNameMap) {
        
        log.debug("应用权限组过滤：scopeLevel={}, scopeFieldId={}", 
                group.getScopeLevel(), 
                group.getScopeFieldId());

        // 1. 根据权限标签应用过滤
        if (group.getScopTags() != null && !group.getScopTags().isEmpty()) {
            applyTagFilters(configStore, group.getScopTags(), loginUserCtx, fieldIdToNameMap);
        }

        // 2. 根据权限级别和范围值应用过滤
        if (group.getScopeLevel() != null && group.getScopeFieldId() != null) {
            applyLevelFilter(configStore, group.getScopeLevel(), 
                    group.getScopeFieldId(), group.getScopeValue(), 
                    loginUserCtx, fieldIdToNameMap);
        }

        // 3. 应用自定义过滤条件
        if (group.getFilters() != null && !group.getFilters().isEmpty()) {
            applyCustomFilters(configStore, group.getFilters(), fieldIdToNameMap);
        }
    }

    /**
     * 根据权限标签应用过滤
     *
     * @param configStore Anyline 查询配置
     * @param tags 权限标签列表
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     */
    private void applyTagFilters(ConfigStore configStore,
                                  List<DataPermissionTag> tags,
                                  LoginUserCtx loginUserCtx,
                                  Map<Long, String> fieldIdToNameMap) {
        
        for (DataPermissionTag tag : tags) {
            switch (tag) {
                case ALL_DATA:
                    // 全部数据：不添加任何过滤条件
                    log.debug("权限标签：全部数据，不添加过滤条件");
                    break;

                case OWN_SUBMIT:
                    // 本人提交：添加 creator_id = 当前用户ID 的条件
                    if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                        configStore.and(Compare.EQUAL, "creator", loginUserCtx.getUserId());
                        log.debug("权限标签：本人提交，添加 creator={}", loginUserCtx.getUserId());
                    }
                    break;

                case DEPARTMENT_SUBMIT:
                    // 本部门提交：添加 department_id = 当前用户部门ID 的条件
                    // TODO: 需要获取用户的部门信息
                    log.debug("权限标签：本部门提交（待实现）");
                    break;

                case SUB_DEPARTMENT_SUBMIT:
                    // 下级部门提交：需要查询下级部门列表，添加 IN 条件
                    // TODO: 需要获取用户的下级部门列表
                    log.debug("权限标签：下级部门提交（待实现）");
                    break;

                case CUSTOM_CONDITION:
                    // 自定义条件：由 filters 字段处理
                    log.debug("权限标签：自定义条件，由filters处理");
                    break;

                default:
                    log.warn("未知的权限标签：{}", tag);
            }
        }
    }

    /**
     * 根据权限级别应用过滤
     *
     * @param configStore Anyline 查询配置
     * @param level 权限级别
     * @param scopeFieldId 范围字段ID
     * @param scopeValue 范围值（JSON字符串）
     * @param loginUserCtx 当前登录用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     */
    private void applyLevelFilter(ConfigStore configStore,
                                   DataPermissionLevel level,
                                   Long scopeFieldId,
                                   String scopeValue,
                                   LoginUserCtx loginUserCtx,
                                   Map<Long, String> fieldIdToNameMap) {
        
        String fieldName = fieldIdToNameMap.get(scopeFieldId);
        if (fieldName == null) {
            log.warn("未找到字段名：fieldId={}", scopeFieldId);
            return;
        }

        log.debug("应用权限级别过滤：level={}, fieldName={}", level.getLabel(), fieldName);

        switch (level) {
            case SELF:
                // 本人：字段值 = 当前用户ID
                if (loginUserCtx != null && loginUserCtx.getUserId() != null) {
                    configStore.and(Compare.EQUAL, fieldName, loginUserCtx.getUserId());
                    log.debug("权限级别：本人，添加 {}={}", fieldName, loginUserCtx.getUserId());
                }
                break;

            case SELF_AND_SUBORDINATES:
                // 本人及下属：需要查询下属列表，添加 IN 条件
                // TODO: 需要获取用户的下属员工列表
                log.debug("权限级别：本人及下属（待实现）");
                break;

            case MAIN_DEPARTMENT:
                // 当前员工所在主部门
                // TODO: 需要获取用户的主部门
                log.debug("权限级别：主部门（待实现）");
                break;

            case MAIN_DEPARTMENT_AND_SUBS:
                // 当前员工所在主部门及下级部门
                // TODO: 需要获取用户的主部门及下级部门列表
                log.debug("权限级别：主部门及下级（待实现）");
                break;

            case SPECIFIED_DEPARTMENT:
                // 指定部门：从 scopeValue 解析部门列表
                if (scopeValue != null && !scopeValue.isEmpty()) {
                    // TODO: 解析 scopeValue JSON，提取部门ID列表，添加 IN 条件
                    log.debug("权限级别：指定部门，scopeValue={}", scopeValue);
                }
                break;

            case SPECIFIED_PERSON:
                // 指定人员：从 scopeValue 解析人员列表
                if (scopeValue != null && !scopeValue.isEmpty()) {
                    // TODO: 解析 scopeValue JSON，提取人员ID列表，添加 IN 条件
                    log.debug("权限级别：指定人员，scopeValue={}", scopeValue);
                }
                break;

            default:
                log.warn("未知的权限级别：{}", level);
        }
    }

    /**
     * 应用自定义过滤条件
     *
     * @param configStore Anyline 查询配置
     * @param filters 自定义过滤条件（二维列表，外层OR，内层AND）
     * @param fieldIdToNameMap 字段ID到字段名的映射
     */
    private void applyCustomFilters(ConfigStore configStore,
                                     List<List<DataPermissionFilter>> filters,
                                     Map<Long, String> fieldIdToNameMap) {
        
        log.debug("应用自定义过滤条件：filterGroupCount={}", filters.size());

        // 外层是 OR 关系，内层是 AND 关系
        // TODO: 当前简化实现，暂时只处理第一个过滤组的 AND 条件
        if (filters.isEmpty()) {
            return;
        }

        List<DataPermissionFilter> firstGroup = filters.get(0);
        for (DataPermissionFilter filter : firstGroup) {
            String fieldName = fieldIdToNameMap.get(filter.getFieldId());
            if (fieldName == null) {
                log.warn("未找到字段名：fieldId={}", filter.getFieldId());
                continue;
            }

            applyFilterCondition(configStore, fieldName, filter);
        }
    }

    /**
     * 应用单个过滤条件
     *
     * @param configStore Anyline 查询配置
     * @param fieldName 字段名
     * @param filter 过滤条件
     */
    private void applyFilterCondition(ConfigStore configStore,
                                       String fieldName,
                                       DataPermissionFilter filter) {
        
        String operator = filter.getFieldOperator();
        String value = filter.getFieldValue();

        log.debug("应用过滤条件：field={}, operator={}, value={}", 
                fieldName, operator, value);

        if (operator == null || value == null) {
            return;
        }

        switch (operator.toUpperCase()) {
            case "EQUALS":
            case "EQUAL":
                configStore.and(Compare.EQUAL, fieldName, value);
                break;
            case "NOT_EQUALS":
            case "NOT_EQUAL":
                configStore.and(Compare.NOT_EQUAL, fieldName, value);
                break;
            case "GREATER_THAN":
            case "GT":
                configStore.and(Compare.GREAT, fieldName, value);
                break;
            case "GREATER_EQUALS":
            case "GTE":
                configStore.and(Compare.GREAT_EQUAL, fieldName, value);
                break;
            case "LESS_THAN":
            case "LT":
                configStore.and(Compare.LESS, fieldName, value);
                break;
            case "LESS_EQUALS":
            case "LTE":
                configStore.and(Compare.LESS_EQUAL, fieldName, value);
                break;
            case "CONTAINS":
            case "LIKE":
                configStore.and(Compare.LIKE, fieldName, value);
                break;
            case "NOT_CONTAINS":
            case "NOT_LIKE":
                configStore.and(Compare.NOT_LIKE, fieldName, value);
                break;
            case "IN":
                configStore.and(Compare.IN, fieldName, value);
                break;
            case "NOT_IN":
                configStore.and(Compare.NOT_IN, fieldName, value);
                break;
            default:
                log.warn("未知的操作符：{}", operator);
                configStore.and(Compare.EQUAL, fieldName, value);
        }
    }
}

