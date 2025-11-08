package com.cmsr.onebase.module.etl.executor.provider.dao;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/8 12:58
 */
@Data
public class EtlTable {

    @SerializedName("table_name")
    private String tableName;

    @SerializedName("meta_info")
    private EtlTableColumn metaInfo;

}
