package com.cmsr.onebase.module.metadata.core.service.permission.checker;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.PermissionChecker;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * TODO: 完整实现需要：
     * 1. 根据权限组的scopeTags、scopeLevel、scopeValue判断数据可见性
     * 2. 根据filters中的条件判断数据是否满足自定义过滤条件
     * 3. 对于UPDATE操作，检查canEdit权限
     * 4. 对于DELETE操作，检查canDelete权限
     * 5. 可能需要查询数据库获取数据的创建人、所属部门等信息进行比对
     *
     * @param context 处理上下文
     * @param groups 数据权限组列表
     */
    private void checkDataRowPermission(ProcessContext context, List<DataPermissionGroup> groups) {
        MetadataDataMethodOpEnum operationType = context.getOperationType();
        LoginUserCtx loginUserCtx = context.getLoginUserCtx();
        Object dataId = context.getId();

        log.debug("开始校验数据行级权限：operationType={}, dataId={}, userId={}, groupCount={}", 
                operationType, 
                dataId, 
                loginUserCtx != null ? loginUserCtx.getUserId() : "unknown",
                groups.size());

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

            if (canOperate) {
                // TODO: 这里应该进一步检查数据是否满足该权限组的范围条件
                // 1. 检查scopeTags（全部数据、本人提交、本部门提交等）
                // 2. 检查scopeLevel和scopeValue（本人、指定部门、指定人员等）
                // 3. 检查filters中的自定义过滤条件
                // 
                // 当前简化实现：如果有任何一个权限组允许该操作，就认为有权限
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
}

