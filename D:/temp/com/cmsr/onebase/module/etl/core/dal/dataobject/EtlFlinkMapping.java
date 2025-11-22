package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import java.io.Serial;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  实体类。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_flink_mapping")
public class EtlFlinkMapping extends TenantBaseDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 数据库类型
     */
    private String datasourceType;

    /**
     * 原始列类型
     */
    private String originType;

    /**
     * 对应Flink类型
     */
    private String flinkType;

    private Long deleted;

    private Long creator;

    private Timestamp createTime;

    private Long updater;

    private Timestamp updateTime;

    private Integer lockVersion;

}
