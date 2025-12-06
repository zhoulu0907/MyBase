package com.cmsr.onebase.module.metadata.core.service.permission;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.service.permission.filter.DataPermissionFilterBuilder;
import com.cmsr.onebase.module.metadata.core.service.permission.filter.FieldPermissionFilter;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 权限查询辅助类
 * 
 * 提供统一的接口，用于在查询时应用数据权限和字段权限过滤
 * 供业务服务层调用，简化权限过滤的使用
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class PermissionQueryHelper {

    @Resource
    private DataPermissionFilterBuilder dataPermissionFilterBuilder;

    @Resource
    private FieldPermissionFilter fieldPermissionFilter;

    /**
     * 应用查询权限过滤（MyBatis-Flex QueryWrapper版本）
     * 
     * 在查询前调用，向 QueryWrapper 添加数据权限过滤条件
     *
     * @param queryWrapper MyBatis-Flex 查询包装器
     * @param permissionContext 权限上下文
     * @param loginUserCtx 当前登录用户
     * @param fields 实体字段列表
     */
    public void applyQueryPermissionFilter(QueryWrapper queryWrapper,
                                            MetadataPermissionContext permissionContext,
                                            LoginUserCtx loginUserCtx,
                                            List<MetadataEntityFieldDO> fields) {
        if (permissionContext == null) {
            log.debug("权限上下文为空，跳过查询权限过滤");
            return;
        }

        // 应用数据权限过滤
        DataPermission dataPermission = permissionContext.getDataPermission();
        if (dataPermission != null) {
            dataPermissionFilterBuilder.applyDataPermissionFilter(
                    queryWrapper, dataPermission, loginUserCtx, fields);
        }

        log.debug("查询权限过滤已应用");
    }

    /**
     * 过滤查询结果中的字段
     * 
     * 在查询后调用，移除用户无权读取的字段
     *
     * @param data 查询结果数据
     * @param permissionContext 权限上下文
     * @param fields 实体字段列表
     * @return 过滤后的数据
     */
    public Map<String, Object> filterQueryResult(Map<String, Object> data,
                                                   MetadataPermissionContext permissionContext,
                                                   List<MetadataEntityFieldDO> fields) {
        if (permissionContext == null) {
            log.debug("权限上下文为空，不过滤查询结果");
            return data;
        }

        // 应用字段权限过滤
        FieldPermission fieldPermission = permissionContext.getFieldPermission();
        if (fieldPermission != null) {
            return fieldPermissionFilter.filterFields(data, fieldPermission, fields);
        }

        return data;
    }

    /**
     * 批量过滤查询结果列表中的字段
     * 
     * 在查询后调用，移除用户无权读取的字段
     *
     * @param dataList 查询结果列表
     * @param permissionContext 权限上下文
     * @param fields 实体字段列表
     * @return 过滤后的数据列表
     */
    public List<Map<String, Object>> filterQueryResultList(List<Map<String, Object>> dataList,
                                                             MetadataPermissionContext permissionContext,
                                                             List<MetadataEntityFieldDO> fields) {
        if (permissionContext == null) {
            log.debug("权限上下文为空，不过滤查询结果列表");
            return dataList;
        }

        // 应用字段权限过滤
        FieldPermission fieldPermission = permissionContext.getFieldPermission();
        if (fieldPermission != null) {
            return fieldPermissionFilter.filterFieldsInList(dataList, fieldPermission, fields);
        }

        return dataList;
    }

    /**
     * 获取可查询的字段名列表
     * 
     * 用于优化查询性能，只查询用户有权限读取的字段
     *
     * @param permissionContext 权限上下文
     * @param fields 实体字段列表
     * @return 可查询的字段名列表
     */
    public List<String> getQueryableFieldNames(MetadataPermissionContext permissionContext,
                                                List<MetadataEntityFieldDO> fields) {
        if (permissionContext == null) {
            log.debug("权限上下文为空，返回所有字段");
            return fields.stream()
                    .map(MetadataEntityFieldDO::getFieldName)
                    .toList();
        }

        FieldPermission fieldPermission = permissionContext.getFieldPermission();
        if (fieldPermission != null) {
            return fieldPermissionFilter.getQueryableFieldNames(fieldPermission, fields);
        }

        return fields.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .toList();
    }
}

