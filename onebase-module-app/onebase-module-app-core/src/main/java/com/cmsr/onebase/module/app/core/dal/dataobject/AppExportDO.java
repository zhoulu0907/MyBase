package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

import lombok.Data;

/**
 * @ClassName AppExportDO
 * @Description TODO
 * @Author mickey
 * @Date 2026/1/26 11:14
 */
@Data
@Table("app_export")
public class AppExportDO extends BaseAppEntity {
    @Column(value = "object_id", comment = "资源在s3中的id")
    private String objectId;

    @Column(value = "version_id", comment = "版本ID")
    private Long versionId;

    @Column(value = "export_status", comment = "导出状态 0-未知 1-导出中 2-导出成功 3-导出失败")
    private Integer exportStatus;
}
