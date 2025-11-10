package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class DataPreview {

    private List<Field> columns = new ArrayList<>();

    private List<List<Object>> data = new ArrayList<>();

}
