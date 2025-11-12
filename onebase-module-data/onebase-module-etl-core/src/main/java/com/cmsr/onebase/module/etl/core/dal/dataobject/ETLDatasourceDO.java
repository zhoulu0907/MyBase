package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.common.entity.JdbcDatasourceConfig;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
@Table(name = "etl_datasource")
public class ETLDatasourceDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "datasource_code")
    private String datasourceCode;

    @Column(name = "datasource_name")
    private String datasourceName;

    @Column(name = "declaration")
    private String declaration;

    @Column(name = "datasource_type")
    private String datasourceType;

    @Column(name = "config")
    private String config;

    @Column(name = "collect_status")
    private String collectStatus;

    @Column(name = "collect_start_time")
    private LocalDateTime collectStartTime;

    @Column(name = "collect_end_time")
    private LocalDateTime collectEndTime;

    @Column(name = "readonly")
    private Integer readonly;

    public void setConfig(Object config) {
        this.config = JsonUtils.toJsonString(config);
    }

    public Boolean getReadonly() {
        return BooleanUtils.toBoolean(readonly);
    }

    public void setReadonly(Integer readonly) {
        this.readonly = readonly;
    }

    public void setReadonly(Boolean readonly) {
        if (readonly == null) {
            return;
        }
        this.readonly = BooleanUtils.toInteger(readonly);
    }

    public CollectStatus getCollectStatus() {
        if (collectStatus == null) {
            return null;
        }
        return CollectStatus.parse(collectStatus);
    }

    public void setCollectStatus(CollectStatus collectStatus) {
        this.collectStatus = collectStatus.getValue();
    }

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }
}
