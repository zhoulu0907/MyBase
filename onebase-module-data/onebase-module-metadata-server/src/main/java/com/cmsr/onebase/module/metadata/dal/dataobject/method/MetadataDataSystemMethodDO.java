package com.cmsr.onebase.module.metadata.dal.dataobject.method;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName MetadataDataSystemMethodDO
 * @Description 系统数据方法表 DO
 * @Author bty418
 * @Date 2025/08/06 14:00
 */
@Table(name = "metadata_data_system_method")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDataSystemMethodDO extends BaseDO {
    /**
     * 方法编码
     */
    private String methodCode;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法类型：CREATE-新增,READ-查询,UPDATE-更新,DELETE-删除,BATCH-批量操作,DRAFT-草稿
     */
    private String methodType;

    /**
     * 方法URL地址
     */
    private String methodUrl;

    /**
     * 方法描述
     */
    private String methodDescription;

    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isEnabled;

    /**
     * HTTP请求方法：GET,POST,PUT,DELETE
     */
    private String requestMethod;
}
