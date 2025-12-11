package com.cmsr.api.chart.dto;

import com.cmsr.extensions.view.dto.ChartDimensionDTO;
import com.cmsr.extensions.view.dto.ChartQuotaDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author gin
 */
@Data
public class ScatterChartDataDTO {
    private Object[] value;
    private List<ChartDimensionDTO> dimensionList;
    private List<ChartQuotaDTO> quotaList;
}
