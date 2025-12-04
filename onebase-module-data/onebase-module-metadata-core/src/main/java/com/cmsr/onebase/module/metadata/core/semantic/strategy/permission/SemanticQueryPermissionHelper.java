package com.cmsr.onebase.module.metadata.core.semantic.strategy.permission;

import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionTag;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.mybatisflex.core.query.QueryColumn;
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
     * - 数据权限：按标签聚合 creator/owner_dept 条件并 OR 合并，整体 AND 到主查询
     * - 字段权限：根据权限裁剪选择列，减少不必要的列查询
     *
     * @param permissionContext 权限上下文
     * @param fields 语义字段列表（用于系统字段识别与列选择）
     */
    public QueryWrapper applyQueryPermissionFilter(
                                            QueryWrapper queryWrapper,
                                            MetadataPermissionContext permissionContext,
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

        Set<Long> creatorIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
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
        // 1) creatorIds：本人提交（OWN_SUBMIT）收集当前用户ID，生成 creator IN (...)
        // 2) deptIds：本部门/下级部门（DEPARTMENT_SUBMIT/SUB_DEPARTMENT_SUBMIT）收集部门ID，生成 owner_dept IN (...)
        // 3) ALL_DATA：存在该标签且无其他限定集合时不追加数据权限条件，实现全量放行
        for (DataPermissionGroup group : groups) {
            List<DataPermissionTag> tags = group.getScopTags();
            if (tags == null || tags.isEmpty()) { continue; }
            for (DataPermissionTag tag : tags) {
                switch (tag) {
                    // 全量数据：记标记位；如无其他限定集合，将不追加数据权限条件
                    case ALL_DATA -> allowAllDataTag = true;
                    // 本人提交：收集当前登录用户ID，后续生成 creator IN (...)
                    case OWN_SUBMIT -> { if (loginUserId != null) { creatorIds.add(loginUserId); } }
                    // 本部门提交：收集当前用户主部门ID，后续生成 owner_dept IN (...)
                    case DEPARTMENT_SUBMIT -> { if (userDeptId != null) { deptIds.add(userDeptId); } }
                    // 下级部门提交：查询主部门的所有子部门并合并到 deptIds，包含主部门自身
                    case SUB_DEPARTMENT_SUBMIT -> {
                        if (userDeptId != null) {
                            List<DeptRespDTO> children = deptApi.getChildDeptList(userDeptId).getCheckedData();
                            if (children != null && !children.isEmpty()) {
                                deptIds.addAll(children.stream().map(DeptRespDTO::getId).collect(Collectors.toSet()));
                                deptIds.add(userDeptId);
                            }
                        }
                    }
                    // 自定义条件：此处不处理，保留给后续 filters/level 扩展
                    case CUSTOM_CONDITION -> {}
                    default -> {}
                }
            }
        }

        // 若存在 ALL_DATA 且未收集到具体限定（既无 creatorIds 又无 deptIds），则不追加数据权限条件
        // 等价于全量放行，避免构造无意义的 WHERE 子句
        if (allowAllDataTag && creatorIds.isEmpty() && deptIds.isEmpty()) { return queryWrapper; }

        QueryWrapper perms = QueryWrapper.create();
        boolean added = false;
        if (!creatorIds.isEmpty()) {
            perms.where(new QueryColumn("creator").in(creatorIds));
            added = true;
        }
        if (!deptIds.isEmpty()) {
            if (!added) { perms.where(new QueryColumn("owner_dept").in(deptIds)); }
            else { perms.or(new QueryColumn("owner_dept").in(deptIds)); }
        }
        if (added) { queryWrapper.and(CPI.getWhereQueryCondition(perms)); }

        // 字段选择已在方法开头统一应用
        return queryWrapper;
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
                                                           MetadataPermissionContext permissionContext,
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
