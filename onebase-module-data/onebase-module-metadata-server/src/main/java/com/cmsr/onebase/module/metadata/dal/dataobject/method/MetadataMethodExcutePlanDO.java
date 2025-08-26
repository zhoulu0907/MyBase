package com.cmsr.onebase.module.metadata.dal.dataobject.method;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 持久化查询计划（QueryPlan）DO
 *
 * @author bty418
 * @date 2025-08-22
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_method_excute_plan")
public class MetadataMethodExcutePlanDO extends BaseDO {

    public static final String METHOD_CODE = "method_code";
    public static final String PLAN_JSON = "plan_json";
    public static final String VERSION = "version";
    public static final String ENABLED = "enabled";

    private String methodCode;

    /**
     * 存储 QueryPlan 的 JSON（jsonb），此处使用 String 保存原始 JSON 文本。
     */
    @Column(name = "plan_json")
    private String planJson;

    private String version;

    private Boolean enabled;

}
