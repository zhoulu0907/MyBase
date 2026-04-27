package com.cmsr.onebase.module.metadata.core.dal.dataobject.method;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统数据方法表 DO
 *
 * @author bty418
 * @date 2025/08/06 14:00
 */
@Table(value = "metadata_data_system_method")
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataDataSystemMethodDO extends BaseEntity {

    /**
     * 方法编码
     */
    @Column(value = "method_code", comment = "方法编码")
    private String methodCode;

    /**
     * 方法名称
     */
    @Column(value = "method_name", comment = "方法名称")
    private String methodName;

    /**
     * 方法类型：CREATE-新增,READ-查询,UPDATE-更新,DELETE-删除,BATCH-批量操作,DRAFT-草稿
     */
    @Column(value = "method_type", comment = "方法类型")
    private String methodType;

    /**
     * 方法URL地址
     */
    @Column(value = "method_url", comment = "方法URL地址")
    private String methodUrl;

    /**
     * 方法描述
     */
    @Column(value = "method_description", comment = "方法描述")
    private String methodDescription;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    /**
     * HTTP请求方法：GET,POST,PUT,DELETE
     */
    @Column(value = "request_method", comment = "HTTP请求方法")
    private String requestMethod;
}
