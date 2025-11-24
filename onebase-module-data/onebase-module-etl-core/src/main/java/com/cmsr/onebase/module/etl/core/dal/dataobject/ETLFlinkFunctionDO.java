package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.data.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "etl_flink_function")
@com.mybatisflex.annotation.Table("etl_flink_function")
public class ETLFlinkFunctionDO extends BaseEntity {

    @Column(name = "function_type")
    private String functionType;

    @Column(name = "function_name")
    private String functionName;

    @Column(name = "function_desc")
    private String functionDesc;

}
