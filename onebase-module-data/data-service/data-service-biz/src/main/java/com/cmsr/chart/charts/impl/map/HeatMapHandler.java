package com.cmsr.chart.charts.impl.map;

import com.cmsr.chart.charts.impl.DefaultChartHandler;
import com.cmsr.chart.utils.ChartDataBuild;
import com.cmsr.extensions.view.dto.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HeatMapHandler extends DefaultChartHandler {
    @Getter
    private String type = "heat-map";

    @Override
    public Map<String, Object> buildResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult
                .getFilterList()
                .stream()
                .anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        Map<String, Object> result = ChartDataBuild.transHeatMapChartDataAntV(xAxis, xAxis, yAxis, view, data, isDrill);
        return result;
    }
}
