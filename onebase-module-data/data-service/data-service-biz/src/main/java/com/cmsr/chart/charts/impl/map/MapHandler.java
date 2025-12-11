package com.cmsr.chart.charts.impl.map;

import com.cmsr.chart.charts.impl.ExtQuotaChartHandler;
import com.cmsr.extensions.view.dto.AxisFormatResult;
import com.cmsr.extensions.view.dto.ChartAxis;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MapHandler extends ExtQuotaChartHandler {
    @Getter
    private String type = "map";
}
