package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.graph.conf.Field;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataPreview {

    private List<Field> columns = new ArrayList<>();

    private List<List<Object>> data = new ArrayList<>();

}
