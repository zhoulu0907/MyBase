package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class DataPreview {

    private List<Field> columns;

    private List<Collection<Object>> data = new ArrayList<>();

}
