package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.data.BaseTenantEntity;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(value = "etl_datasource")
public class ETLDatasourceDO extends BaseTenantEntity {

    @Column(value = "datasource_code")
    private String datasourceCode;

    @Column(value = "datasource_name")
    private String datasourceName;

    @Column(value = "declaration")
    private String declaration;

    @Column(value = "datasource_type")
    private String datasourceType;

    @Column(value = "config")
    private String config;

    @Column(value = "collect_status")
    private CollectStatus collectStatus;

    @Column(value = "collect_start_time")
    private LocalDateTime collectStartTime;

    @Column(value = "collect_end_time")
    private LocalDateTime collectEndTime;

    @Column(value = "readonly")
    private Integer readonly;

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }
}
