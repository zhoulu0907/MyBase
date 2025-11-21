package com.cmsr.onebase.module.etl.common.preview;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class DataPreview {

    private List<PreviewColumn> columns = new ArrayList<>();

    private List<Map<String, Object>> data = new ArrayList<>();

}
