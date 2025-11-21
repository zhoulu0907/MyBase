package com.cmsr.onebase.module.etl.common.preview;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class DataPreview {

    private List<PreviewColumn> columns = new ArrayList<>();

    private List<Map<String, Object>> data = new ArrayList<>();

}
