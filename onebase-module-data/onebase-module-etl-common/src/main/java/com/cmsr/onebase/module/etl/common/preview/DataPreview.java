package com.cmsr.onebase.module.etl.common.preview;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class DataPreview {

    private List<ColumnDefine> columns = new ArrayList<>();

    private List<List<Object>> data = new ArrayList<>();

}
