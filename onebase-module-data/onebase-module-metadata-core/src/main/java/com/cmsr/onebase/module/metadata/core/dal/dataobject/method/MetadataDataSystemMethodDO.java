package com.cmsr.onebase.module.metadata.core.dal.dataobject.method;

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

    // 列名常量
    public static final String METHOD_CODE        = "method_code";
    public static final String METHOD_NAME        = "method_name";
    public static final String METHOD_TYPE        = "method_type";
    public static final String METHOD_URL         = "method_url";
    public static final String METHOD_DESCRIPTION = "method_description";
    public static final String IS_ENABLED        = "is_enabled";
    public static final String REQUEST_METHOD    = "request_method";

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
     * 是否启用：1-启用，0-禁用
     * @see CommonStatusEnum
     */
    private Integer isEnabled;

    /**
     * HTTP请求方法：GET,POST,PUT,DELETE
     */
    private String requestMethod;
}
