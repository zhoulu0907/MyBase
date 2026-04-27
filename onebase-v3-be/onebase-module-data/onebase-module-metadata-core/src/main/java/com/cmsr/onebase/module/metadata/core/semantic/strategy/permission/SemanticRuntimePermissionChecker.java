package com.cmsr.onebase.module.metadata.core.semantic.strategy.permission;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;

/**
 * 运行时权限检查器接口
 *
 * 定义语义执行阶段的权限校验能力，通过统一的 RecordDTO 承载上下文与数据，
 * 由不同类型的权限检查器实现具体的支持判断与校验逻辑。
 */
public interface SemanticRuntimePermissionChecker {
    /**
     * 返回权限类型名称，用于错误提示与日志标识
     */
    String getPermissionType();
    /**
     * 判断当前检查器是否支持对该记录进行校验
     */
    boolean supports(SemanticRecordDTO recordDTO);
    /**
     * 执行权限校验，不通过时抛出业务异常
     */
    void check(SemanticRecordDTO recordDTO);
    /**
     * 权限检查器的执行顺序，值越小优先级越高
     */
    default int getOrder() { return 100; }
}
