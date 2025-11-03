package com.cmsr.onebase.module.metadata.core.service.permission;

import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;

/**
 * 权限校验器接口
 * 
 * 所有具体的权限校验器都需要实现此接口，统一权限校验逻辑
 *
 * @author matianyu
 * @date 2025-10-31
 */
public interface PermissionChecker {

    /**
     * 获取权限校验类型
     *
     * @return 权限类型标识（如：OPERATION, DATA, FIELD）
     */
    String getPermissionType();

    /**
     * 判断是否支持当前操作类型的权限校验
     *
     * @param context 处理上下文
     * @return true表示支持，false表示不支持
     */
    boolean supports(ProcessContext context);

    /**
     * 执行权限校验
     * 
     * 如果校验不通过，应抛出 PermissionDeniedException 异常
     *
     * @param context 处理上下文（包含操作类型、实体信息、权限上下文等）
     * @throws com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException 权限校验失败时抛出
     */
    void check(ProcessContext context);

    /**
     * 获取执行顺序（数值越小优先级越高）
     * 
     * 默认顺序：
     * 1. 操作权限（10） - 最先校验，确保用户有操作权限
     * 2. 数据权限（20） - 其次校验数据范围权限
     * 3. 字段权限（30） - 最后校验字段级别权限
     *
     * @return 执行顺序
     */
    default int getOrder() {
        return 100;
    }
}


