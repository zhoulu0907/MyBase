package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.mybatis.BaseBizEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_flink_mapping")
public class EtlFlinkMapping extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

}
