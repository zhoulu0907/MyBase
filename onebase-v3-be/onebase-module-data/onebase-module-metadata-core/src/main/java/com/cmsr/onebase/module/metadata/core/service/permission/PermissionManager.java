package com.cmsr.onebase.module.metadata.core.service.permission;

import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * 权限校验管理器
 * 
 * 统一管理所有权限校验器，按照优先级顺序执行权限校验
 * 采用责任链模式，依次执行：操作权限 -> 数据权限 -> 字段权限
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class PermissionManager {

    @Resource
    private List<PermissionChecker> permissionCheckers;

    /**
     * 执行完整的权限校验流程
     * 
     * 按照优先级顺序执行所有支持的权限校验器
     *
     * @param context 处理上下文
     * @throws PermissionDeniedException 权限校验失败时抛出
     */
    public void checkPermission(ProcessContext context) {
        MetadataPermissionContext permissionContext = context.getMetadataPermissionContext();
        
        // 如果没有权限上下文，直接返回（可能是系统内部调用）
        if (permissionContext == null) {
            log.warn("权限上下文为空，跳过权限校验：entityId={}, operationType={}", 
                    context.getEntityId(), 
                    context.getOperationType());
            return;
        }

        log.info("开始执行权限校验：entityId={}, operationType={}, userId={}", 
                context.getEntityId(), 
                context.getOperationType(), 
                context.getLoginUserCtx() != null ? context.getLoginUserCtx().getUserId() : "unknown");

        // 按照优先级排序权限校验器
        List<PermissionChecker> sortedCheckers = permissionCheckers.stream()
                .filter(checker -> checker.supports(context))
                .sorted(Comparator.comparingInt(PermissionChecker::getOrder))
                .toList();

        if (sortedCheckers.isEmpty()) {
            log.warn("没有找到支持的权限校验器：entityId={}, operationType={}", 
                    context.getEntityId(), 
                    context.getOperationType());
            return;
        }

        // 依次执行权限校验
        for (PermissionChecker checker : sortedCheckers) {
            try {
                log.debug("执行{}权限校验：entityId={}", 
                        checker.getPermissionType(), 
                        context.getEntityId());
                
                checker.check(context);
                
                log.debug("{}权限校验通过", checker.getPermissionType());
            } catch (PermissionDeniedException e) {
                log.warn("{}权限校验失败：permissionType={}, deniedPermission={}, message={}", 
                        checker.getPermissionType(),
                        e.getPermissionType(),
                        e.getDeniedPermission(),
                        e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("{}权限校验执行异常：entityId={}", 
                        checker.getPermissionType(), 
                        context.getEntityId(), 
                        e);
                throw new PermissionDeniedException(
                        checker.getPermissionType(),
                        "UNKNOWN",
                        "权限校验执行异常：" + e.getMessage(),
                        e
                );
            }
        }

        log.info("权限校验全部通过：entityId={}, operationType={}", 
                context.getEntityId(), 
                context.getOperationType());
    }

    /**
     * 检查是否具有指定的操作权限
     * 
     * 提供便捷方法，用于快速检查单个操作权限
     *
     * @param permissionContext 权限上下文
     * @param operationType 操作类型
     * @return true表示有权限，false表示无权限
     */
    public boolean hasOperationPermission(MetadataPermissionContext permissionContext, String operationType) {
        if (permissionContext == null || permissionContext.getOperationPermission() == null) {
            return false;
        }

        return switch (operationType.toUpperCase()) {
            case "CREATE" -> permissionContext.getOperationPermission().isCanCreate();
            case "UPDATE", "EDIT" -> permissionContext.getOperationPermission().isCanEdit();
            case "DELETE" -> permissionContext.getOperationPermission().isCanDelete();
            case "IMPORT" -> permissionContext.getOperationPermission().isCanImport();
            case "EXPORT" -> permissionContext.getOperationPermission().isCanExport();
            case "SHARE" -> permissionContext.getOperationPermission().isCanShare();
            default -> false;
        };
    }
}


