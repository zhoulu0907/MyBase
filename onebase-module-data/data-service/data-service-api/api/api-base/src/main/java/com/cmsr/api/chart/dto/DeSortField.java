package com.cmsr.api.chart.dto;

import com.cmsr.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;

@Data
public class DeSortField extends DatasetTableFieldDTO {

    private String orderDirection;
}
