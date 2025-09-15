package com.cmsr.onebase.module.metadata.dal.dataobject.method;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据方法执行日志 DO
 * 记录输入参数、主键集合、数据源信息、耗时、状态与异常
 *
 * @author bty418
 * @date 2025-08-22
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_data_method_execution_log")
public class MetadataDataMethodExecutionLogDO extends BaseDO {

    public static final String METHOD_ID = "method_id";
    public static final String REQUEST_PARAMS = "request_params";
    public static final String PRIMARY_KEYS = "primary_keys";
    public static final String DATA_SOURCES = "data_sources";
    public static final String DURATION_MS = "duration_ms";
    public static final String STATUS = "status";
    public static final String ERROR_MSG = "error_msg";

    /**
     * 关联系统数据方法ID（method_id）
     */
    private Long methodId;

    @Column(name = "request_params")
    private String requestParams; // jsonb

    @Column(name = "primary_keys")
    private String primaryKeys; // jsonb

    @Column(name = "data_sources")
    private String dataSources; // jsonb

    private Integer durationMs;

    private String status;

    @Column(columnDefinition = "text")
    private String errorMsg;

}
