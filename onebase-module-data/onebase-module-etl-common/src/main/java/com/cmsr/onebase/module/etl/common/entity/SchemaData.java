package com.cmsr.onebase.module.etl.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/11 8:51
 */
@Data
public class SchemaData {

    private String catalogName;

    private String name;

    @JsonIgnore
    private List<TableData> tables = new ArrayList<>();
}
