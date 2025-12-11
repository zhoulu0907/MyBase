package com.cmsr.chart.charts.impl.map;

import com.cmsr.chart.charts.impl.ExtQuotaChartHandler;
import com.cmsr.extensions.view.dto.AxisFormatResult;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class BubbleMapHandler extends ExtQuotaChartHandler {
    @Getter
    private String type = "bubble-map";

    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        return super.formatAxis(view);
    }
}


