package com.cmsr.onebase.module.etl.common.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/11 8:50
 */
@Data
public class CatalogData {

    private String name;

    private List<SchemaData> schemas = new ArrayList<>();
}
