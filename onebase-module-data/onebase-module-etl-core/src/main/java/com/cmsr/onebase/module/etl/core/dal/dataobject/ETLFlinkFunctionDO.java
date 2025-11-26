package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "etl_flink_function")
public class ETLFlinkFunctionDO extends BaseEntity {

    @Column(value = "function_type")
    private String functionType;

    @Column(value = "function_name")
    private String functionName;

    @Column(value = "function_desc")
    private String functionDesc;

}
