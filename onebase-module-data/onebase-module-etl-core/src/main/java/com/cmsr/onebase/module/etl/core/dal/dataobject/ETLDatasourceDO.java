package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDateTime;

@Table(name = "etl_datasource")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ETLDatasourceDO extends TenantBaseDO {

    public ETLDatasourceDO setId(Long id) {
        super.setId(id);
        return this;
    }

    @Column(name = "datasource_code")
    private String datasourceCode;

    @Column(name = "datasource_name")
    private String datasourceName;

    @Column(name = "datasource_type")
    private String datasourceType;

    @Column(name = "config")
    private String config;

    @Column(name = "app_id")
    private Long appId;

    @Column(name = "collect_status")
    private String collectStatus;

    @Column(name = "collect_start_time")
    private LocalDateTime collectStartTime;

    @Column(name = "collect_end_time")
    private LocalDateTime collectEndTime;

    @Column(name = "readonly")
    private Integer readonly;

    public Boolean getReadonly() {
        return BooleanUtils.toBoolean(readonly);
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = BooleanUtils.toInteger(readonly);
    }

    public CollectStatus getCollectStatus() {
        return CollectStatus.parse(collectStatus);
    }

    public void setCollectStatus(CollectStatus collectStatus) {
        this.collectStatus = collectStatus.getValue();
    }
}
