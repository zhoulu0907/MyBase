package com.cmsr.onebase.module.metadata.dal.dataobject.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.util.Map;

/**
 * 数据源表 DO
 */
@TableName(value = "metadata_datasource", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDatasourceDO extends TenantBaseDO {

    public MetadataDatasourceDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源编码
     */
    private String code;

    /**
     * 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
     */
    private String datasourceType;

    /**
     * 数据源配置信息(JSON格式存储所有连接参数)
     * 使用自定义TypeHandler确保明文JSON存储
     */
    @TableField(typeHandler = PlainJsonTypeHandler.class)
    private String config;

    /**
     * 描述
     */
    private String description;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 自定义TypeHandler，确保JSON以明文格式存储
     */
    public static class PlainJsonTypeHandler extends com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler<String> {
        
        public PlainJsonTypeHandler() {
            super(String.class);
        }

        @Override
        public String parse(String json) {
            // 直接返回JSON字符串，不进行任何编码转换
            return json;
        }

        @Override
        public String toJson(String obj) {
            // 直接返回JSON字符串，不进行任何编码转换
            return obj;
        }
    }
}
