package com.cmsr.onebase.framework.data;


import com.mybatisflex.annotation.Column;
import lombok.Data;

@Data
public class BaseBizEntity extends BaseTenantEntity {

    @Column(value = "version_id", comment = "版本ID")
    private Long versionId;

    @Column(value = "version_status", comment = "版本状态")
    private String versionStatus;

}
