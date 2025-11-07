package com.cmsr.onebase.module.metadata.core.service.permission.checker;

import cn.hutool.core.convert.Convert;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.security.bo.*;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.PermissionChecker;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限校验器
 *
 * 校验用户对特定数据行的访问权限，包括：
 * 1. 数据范围权限（全部数据、本人提交、本部门提交等）
 * 2. 数据级别权限（本人、本人及下属、部门等）
 * 3. 自定义过滤条件
 * 4. 数据行级操作权限（编辑、删除）
 *
 * 注意：此校验器主要在 UPDATE 和 DELETE 操作时生效，
 * 对于 QUERY、LIST、PAGE 等查询操作，数据权限过滤应在SQL层面通过WHERE条件实现
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class DataPermissionChecker implements PermissionChecker {

    private static final String PERMISSION_TYPE = "数据权限";

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @Override
    public String getPermissionType() {
        return PERMISSION_TYPE;
    }

    @Override
    public boolean supports(ProcessContext context) {
        // 数据权限主要针对UPDATE和DELETE操作
        // GET、GET_PAGE、GET_PAGE_OR等查询操作的数据权限过滤应在查询层面实现
        if (context == null || context.getMetadataPermissionContext() == null) {
            return false;
        }

        MetadataDataMethodOpEnum operationType = context.getOperationType();
        return operationType == MetadataDataMethodOpEnum.UPDATE
                || operationType == MetadataDataMethodOpEnum.DELETE
                || operationType == MetadataDataMethodOpEnum.GET;
    }

    @Override
    public void check(ProcessContext context) {
        MetadataPermissionContext permissionContext = context.getMetadataPermissionContext();
        DataPermission dataPermission = permissionContext.getDataPermission();

        if (dataPermission == null) {
            log.warn("数据权限对象为空，跳过数据权限校验");
            return;
        }

        // 如果配置了全部拒绝，直接抛出异常
        if (dataPermission.isAllDenied()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "ALL_DENIED",
                    "无权访问任何数据"
            );
        }

        // 如果配置了全部允许，直接通过
        if (dataPermission.isAllAllowed()) {
            log.debug("数据权限：全部允许");
            return;
        }

        // 检查数据权限组
        List<DataPermissionGroup> groups = dataPermission.getGroups();
        if (groups == null || groups.isEmpty()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "NO_GROUPS",
                    "无权访问任何数据"
            );
        }

        // 校验数据行级操作权限
        checkDataRowPermission(context, groups);

        log.debug("数据权限校验通过：operationType={}, dataId={}",
                context.getOperationType(),
                context.getId());
    }

    @Override
    public int getOrder() {
        return 20;
    }

    /**
     * 校验数据行级操作权限
     *
     * 根据数据权限组的配置，判断用户是否有权对特定数据行进行操作
     *
     * 实现逻辑：
     * 1. 查询数据行的详细信息（creator、部门等字段）
     * 2. 根据权限组的scopeTags、scopeLevel、scopeValue判断数据可见性
     * 3. 根据filters中的条件判断数据是否满足自定义过滤条件
     * 4. 对于UPDATE操作，检查canEdit权限
     * 5. 对于DELETE操作，检查canDelete权限
     *
     * @param context 处理上下文
     * @param groups 数据权限组列表
     */
    private void checkDataRowPermission(ProcessContext context, List<DataPermissionGroup> groups) {
        MetadataDataMethodOpEnum operationType = context.getOperationType();
        LoginUserCtx loginUserCtx = context.getRequestContext().getLoginUserCtx();
        Object dataId = context.getId();

        if (loginUserCtx == null || loginUserCtx.getUserId() == null) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "NO_USER",
                    "未获取到登录用户信息"
            );
        }

        log.debug("开始校验数据行级权限：operationType={}, dataId={}, userId={}, groupCount={}",
                operationType,
                dataId,
                loginUserCtx.getUserId(),
                groups.size());

        // 查询数据行信息
        Map<String, Object> dataRow = queryDataRow(context);
        if (dataRow == null || dataRow.isEmpty()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "DATA_NOT_FOUND",
                    "数据不存在"
            );
        }

        // 获取用户信息
        AdminUserRespDTO currentUser = adminUserApi.getUser(loginUserCtx.getUserId()).getCheckedData();
        if (currentUser == null) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "USER_NOT_FOUND",
                    "用户信息不存在"
            );
        }

        // 构建字段ID到字段名的映射
        Map<Long, String> fieldIdToNameMap = context.getFields().stream()
                .collect(Collectors.toMap(
                        MetadataEntityFieldDO::getId,
                        MetadataEntityFieldDO::getFieldName
                ));

        // 检查是否至少有一个权限组满足条件
        boolean hasPermission = false;

        for (DataPermissionGroup group : groups) {
            // 根据操作类型检查对应的权限
            boolean canOperate = switch (operationType) {
                case UPDATE -> group.isCanEdit();
                case DELETE -> group.isCanDelete();
                case GET -> true; // 查询操作只要能看到数据就可以
                default -> false;
            };

            if (!canOperate) {
                continue;
            }

            // 检查数据是否满足该权限组的范围条件
            boolean matchesScope = (checkScopeTags(group, dataRow, currentUser, context)
                    || checkScopeLevel(group, dataRow, currentUser, fieldIdToNameMap))
                    && checkFilters(group, dataRow, fieldIdToNameMap);

            if (matchesScope) {
                hasPermission = true;
                log.debug("找到满足条件的权限组：canEdit={}, canDelete={}",
                        group.isCanEdit(),
                        group.isCanDelete());
                break;
            }
        }

        if (!hasPermission) {
            String operation = switch (operationType) {
                case UPDATE -> "编辑";
                case DELETE -> "删除";
                case GET -> "查看";
                default -> "操作";
            };

            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    operationType.name(),
                    String.format("无权%s该数据", operation)
            );
        }
    }

    /**
     * 查询数据行信息
     *
     * @param context 处理上下文
     * @return 数据行Map
     */
    private Map<String, Object> queryDataRow(ProcessContext context) {
        Object dataId = context.getId();
        if (dataId == null) {
            return null;
        }

        // 获取实体和字段信息
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();

        if (entity == null || fields == null || temporaryService == null) {
            log.warn("缺少必要的上下文信息，无法查询数据行");
            return null;
        }

        // 获取主键字段名
        String primaryKeyField = getPrimaryKeyFieldName(fields);

        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, dataId);

        // 检查表中是否有软删除字段
        boolean hasDeletedField = fields.stream()
                .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));

        if (hasDeletedField) {
            configStore.and("deleted", 0);
        }

        // 查询数据
        DataSet dataSet = temporaryService.querys(entity.getTableName(), configStore);
        if (dataSet == null || dataSet.size() == 0) {
            return null;
        }

        DataRow dataRow = dataSet.getRow(0);
        return convertDataRowToMap(dataRow, fields);
    }

    /**
     * 获取主键字段名
     *
     * @param fields 字段列表
     * @return 主键字段名
     */
    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        // 优先查找非系统字段中的主键
        Optional<MetadataEntityFieldDO> primaryKeyField = fields.stream()
                .filter(field -> "1".equals(String.valueOf(field.getIsPrimaryKey())))
                .filter(field -> !"1".equals(String.valueOf(field.getIsSystemField())))
                .findFirst();

        if (primaryKeyField.isPresent()) {
            return primaryKeyField.get().getFieldName();
        }

        // 如果未找到，回退到id字段
        boolean hasId = fields.stream()
                .anyMatch(field -> "id".equalsIgnoreCase(field.getFieldName()));

        if (hasId) {
            return "id";
        }

        log.warn("未找到主键字段，使用默认id作为主键名称");
        return "id";
    }

    /**
     * 转换DataRow为Map
     *
     * @param dataRow 数据行
     * @param fields 字段列表
     * @return 数据Map
     */
    private Map<String, Object> convertDataRowToMap(DataRow dataRow, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> resultMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            Object value = dataRow.get(fieldName);
            if (value != null) {
                resultMap.put(fieldName, value);
            }
        }
        return resultMap;
    }

    /**
     * 检查scopeTags权限范围
     *
     * @param group 权限组
     * @param dataRow 数据行
     * @param currentUser 当前用户
     * @param context 处理上下文
     * @return 是否满足条件
     */
    private boolean checkScopeTags(DataPermissionGroup group, Map<String, Object> dataRow,
                                   AdminUserRespDTO currentUser, ProcessContext context) {
        List<DataPermissionTag> scopeTags = group.getScopTags();
        if (scopeTags == null || scopeTags.isEmpty()) {
            return true; // 没有scopeTags限制，认为满足条件
        }

        // 如果包含ALL_DATA，直接通过
        if (scopeTags.contains(DataPermissionTag.ALL_DATA)) {
            return true;
        }

        // 检查各个标签，只要满足其中一个即可
        for (DataPermissionTag tag : scopeTags) {
            switch (tag) {
                case ALL_DATA:
                    return true;

                case OWN_SUBMIT:
                    // 判断是否是本人提交：creator字段等于当前用户ID
                    Object creator = dataRow.get("creator");
                    if (creator != null && String.valueOf(creator).equals(String.valueOf(currentUser.getId()))) {
                        return true;
                    }
                    break;

                case DEPARTMENT_SUBMIT:
                    // 判断是否是本部门提交：需要获取数据的部门字段，然后判断是否与当前用户部门一致
                    if (checkDepartmentMatch(dataRow, currentUser.getDeptId())) {
                        return true;
                    }
                    break;

                case SUB_DEPARTMENT_SUBMIT:
                    // 判断是否是下级部门提交：需要获取数据的部门字段，然后判断是否在当前用户的下级部门列表中
                    if (checkSubDepartmentMatch(dataRow, currentUser.getDeptId())) {
                        return true;
                    }
                    break;

                case CUSTOM_CONDITION:
                    // 自定义条件由filters处理，这里不处理
                    break;

                default:
                    log.warn("未知的权限标签：{}", tag);
            }
        }

        return false;
    }

    /**
     * 检查部门匹配
     *
     * @param dataRow 数据行
     * @param userDeptId 用户部门ID
     * @return 是否匹配
     */
    private boolean checkDepartmentMatch(Map<String, Object> dataRow, Long userDeptId) {
        if (userDeptId == null) {
            return false;
        }

        // 尝试从常见部门字段名中查找
        String[] deptFieldNames = {"owner_dept"};
        for (String fieldName : deptFieldNames) {
            Object deptId = dataRow.get(fieldName);
            if (deptId != null && String.valueOf(deptId).equals(String.valueOf(userDeptId))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查下级部门匹配
     *
     * @param dataRow 数据行
     * @param userDeptId 用户部门ID
     * @return 是否匹配
     */
    private boolean checkSubDepartmentMatch(Map<String, Object> dataRow, Long userDeptId) {
        if (userDeptId == null) {
            return false;
        }

        // 获取用户的下级部门列表
        List<DeptRespDTO> childDepts = deptApi.getChildDeptList(userDeptId).getCheckedData();
        if (childDepts == null || childDepts.isEmpty()) {
            return false;
        }

        Set<Long> childDeptIds = childDepts.stream()
                .map(DeptRespDTO::getId)
                .collect(Collectors.toSet());

        // 从部门字段名中查找
        String[] deptFieldNames = {"owner_dept"};
        for (String fieldName : deptFieldNames) {
            Object deptId = dataRow.get(fieldName);
            if (deptId != null && childDeptIds.contains(Convert.toLong(deptId))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查scopeLevel权限级别
     *
     * @param group 权限组
     * @param dataRow 数据行
     * @param currentUser 当前用户
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 是否满足条件
     */
    private boolean checkScopeLevel(DataPermissionGroup group, Map<String, Object> dataRow,
                                    AdminUserRespDTO currentUser, Map<Long, String> fieldIdToNameMap) {
        DataPermissionLevel scopeLevel = group.getScopeLevel();
        Long scopeFieldId = group.getScopeFieldId();
        String scopeValue = group.getScopeValue();

        if (scopeLevel == null || scopeFieldId == null) {
            return false; // 没有scopeLevel限制，认为不满足条件
        }

        String fieldName = fieldIdToNameMap.get(scopeFieldId);
        if (fieldName == null) {
            log.warn("未找到字段名：fieldId={}", scopeFieldId);
            return false;
        }

        Object fieldValue = dataRow.get(fieldName);

        switch (scopeLevel) {
            case SELF:
                // 本人：字段值 = 当前用户ID
                return fieldValue != null && String.valueOf(fieldValue).equals(String.valueOf(currentUser.getId()));

            case SELF_AND_SUBORDINATES:
                // 本人及下属：需要查询下属列表，判断字段值是否在列表中
                List<AdminUserRespDTO> subordinates = adminUserApi.getUserListBySubordinate(currentUser.getId()).getCheckedData();
                if (subordinates == null || subordinates.isEmpty()) {
                    // 如果没有下属，只检查本人
                    return fieldValue != null && String.valueOf(fieldValue).equals(String.valueOf(currentUser.getId()));
                }
                Set<Long> subordinateIds = subordinates.stream()
                        .map(AdminUserRespDTO::getId)
                        .collect(Collectors.toSet());
                subordinateIds.add(currentUser.getId()); // 包含本人
                return fieldValue != null && subordinateIds.contains(Convert.toLong(fieldValue));

            case MAIN_DEPARTMENT:
                // 当前员工所在主部门：字段值 = 当前用户部门ID
                return fieldValue != null && currentUser.getDeptId() != null
                        && String.valueOf(fieldValue).equals(String.valueOf(currentUser.getDeptId()));

            case MAIN_DEPARTMENT_AND_SUBS:
                // 当前员工所在主部门及下级部门：需要获取主部门及下级部门列表
                if (currentUser.getDeptId() == null) {
                    return false;
                }
                List<DeptRespDTO> childDepts = deptApi.getChildDeptList(currentUser.getDeptId()).getCheckedData();
                Set<Long> deptIds = new HashSet<>();
                deptIds.add(currentUser.getDeptId());
                if (childDepts != null && !childDepts.isEmpty()) {
                    childDepts.forEach(dept -> deptIds.add(dept.getId()));
                }
                return fieldValue != null && deptIds.contains(Convert.toLong(fieldValue));

            case SPECIFIED_DEPARTMENT:
                // 指定部门：从scopeValue解析部门列表
                if (scopeValue == null || scopeValue.isEmpty()) {
                    return false;
                }
                return checkSpecifiedScopeValue(scopeValue, fieldValue, "department");

            case SPECIFIED_PERSON:
                // 指定人员：从scopeValue解析人员列表
                if (scopeValue == null || scopeValue.isEmpty()) {
                    return false;
                }
                return checkSpecifiedScopeValue(scopeValue, fieldValue, "person");

            default:
                log.warn("未知的权限级别：{}", scopeLevel);
                return false;
        }
    }

    /**
     * 检查指定的范围值
     *
     * @param scopeValue JSON字符串，包含key列表
     * @param fieldValue 字段值
     * @param type 类型：department或person
     * @return 是否匹配
     */
    private boolean checkSpecifiedScopeValue(String scopeValue, Object fieldValue, String type) {
        if (fieldValue == null) {
            return false;
        }

        try {
            // 解析JSON数组
            List<Map<String, Object>> scopeList = JsonUtils.parseObject(scopeValue, new TypeReference<List<Map<String, Object>>>() {
            });
            if (scopeList == null || scopeList.isEmpty()) {
                return false;
            }

            // 提取key列表
            Set<String> keySet = scopeList.stream()
                    .map(item -> {
                        Object key = item.get("key");
                        return key != null ? String.valueOf(key) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            String fieldValueStr = String.valueOf(fieldValue);
            return keySet.contains(fieldValueStr);
        } catch (Exception e) {
            log.warn("解析scopeValue失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查自定义过滤条件
     *
     * @param group 权限组
     * @param dataRow 数据行
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 是否满足条件
     */
    private boolean checkFilters(DataPermissionGroup group, Map<String, Object> dataRow,
                                 Map<Long, String> fieldIdToNameMap) {
        List<List<DataPermissionFilter>> filters = group.getFilters();
        if (filters == null || filters.isEmpty()) {
            return true; // 没有filters限制，认为满足条件
        }

        // 外层是OR关系，内层是AND关系
        // 只要有一个外层组合满足条件即可
        for (List<DataPermissionFilter> filterGroup : filters) {
            boolean groupMatches = true;

            // 内层是AND关系，所有条件都必须满足
            for (DataPermissionFilter filter : filterGroup) {
                if (!checkSingleFilter(filter, dataRow, fieldIdToNameMap)) {
                    groupMatches = false;
                    break;
                }
            }

            if (groupMatches) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查单个过滤条件
     *
     * @param filter 过滤条件
     * @param dataRow 数据行
     * @param fieldIdToNameMap 字段ID到字段名的映射
     * @return 是否满足条件
     */
    private boolean checkSingleFilter(DataPermissionFilter filter, Map<String, Object> dataRow,
                                      Map<Long, String> fieldIdToNameMap) {
        Long fieldId = filter.getFieldId();
        String fieldName = fieldIdToNameMap.get(fieldId);
        if (fieldName == null) {
            log.warn("未找到字段名：fieldId={}", fieldId);
            return false;
        }

        Object fieldValue = dataRow.get(fieldName);
        String operator = filter.getFieldOperator();
        String filterValue = filter.getFieldValue();

        if (operator == null || filterValue == null) {
            return false;
        }

        // 根据操作符进行匹配
        switch (operator.toUpperCase()) {
            case "EQUALS":
            case "EQUAL":
                return fieldValue != null && String.valueOf(fieldValue).equals(filterValue);

            case "NOT_EQUALS":
            case "NOT_EQUAL":
                return fieldValue == null || !String.valueOf(fieldValue).equals(filterValue);

            case "GREATER_THAN":
            case "GT":
                return compareNumbers(fieldValue, filterValue) > 0;

            case "GREATER_EQUALS":
            case "GTE":
                return compareNumbers(fieldValue, filterValue) >= 0;

            case "LESS_THAN":
            case "LT":
                return compareNumbers(fieldValue, filterValue) < 0;

            case "LESS_EQUALS":
            case "LTE":
                return compareNumbers(fieldValue, filterValue) <= 0;

            case "CONTAINS":
            case "LIKE":
                return fieldValue != null && String.valueOf(fieldValue).contains(filterValue);

            case "NOT_CONTAINS":
            case "NOT_LIKE":
                return fieldValue == null || !String.valueOf(fieldValue).contains(filterValue);

            case "IN":
                // 假设filterValue是逗号分隔的值列表
                String[] values = filterValue.split(",");
                String fieldValueStr = fieldValue != null ? String.valueOf(fieldValue) : null;
                return fieldValueStr != null && Arrays.asList(values).contains(fieldValueStr.trim());

            case "NOT_IN":
                String[] notInValues = filterValue.split(",");
                String fieldValueStr2 = fieldValue != null ? String.valueOf(fieldValue) : null;
                return fieldValueStr2 == null || !Arrays.asList(notInValues).contains(fieldValueStr2.trim());

            default:
                log.warn("未知的操作符：{}", operator);
                return false;
        }
    }

    /**
     * 比较数字
     *
     * @param fieldValue 字段值
     * @param filterValue 过滤值
     * @return 比较结果：>0表示fieldValue>filterValue，=0表示相等，<0表示fieldValue<filterValue
     */
    private int compareNumbers(Object fieldValue, String filterValue) {
        if (fieldValue == null) {
            return -1;
        }

        try {
            Double fieldNum = Double.parseDouble(String.valueOf(fieldValue));
            Double filterNum = Double.parseDouble(filterValue);
            return fieldNum.compareTo(filterNum);
        } catch (NumberFormatException e) {
            log.warn("数值比较失败：fieldValue={}, filterValue={}", fieldValue, filterValue);
            return 0;
        }
    }
}

