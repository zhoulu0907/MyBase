package com.cmsr.onebase.module.metadata.dal.dataobject.method;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Outbox DO
 * 用于记录跨数据源写操作用于异步可靠投递及后续补偿
 *
 * @author bty418
 * @date 2025-08-22
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_outbox")
public class MetadataOutboxDO extends BaseDO {

    public static final String AGGREGATE_TYPE = "aggregate_type";
    public static final String AGGREGATE_ID = "aggregate_id";
    public static final String ACTION = "action";
    public static final String PAYLOAD = "payload";
    public static final String STATE = "state";
    public static final String RETRIES = "retries";
    public static final String NEXT_RETRY_AT = "next_retry_at";

    private String aggregateType;

    private String aggregateId;

    private String action;

    @Column(name = "payload")
    private String payload; // jsonb

    private String state;

    private Integer retries;

    private java.time.LocalDateTime nextRetryAt;

    private java.time.LocalDateTime processedAt;

}
