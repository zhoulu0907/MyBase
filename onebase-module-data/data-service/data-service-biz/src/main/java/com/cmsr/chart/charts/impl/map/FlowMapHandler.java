package com.cmsr.chart.charts.impl.map;

import com.cmsr.chart.charts.impl.GroupChartHandler;
import com.cmsr.extensions.view.dto.AxisFormatResult;
import com.cmsr.extensions.view.dto.ChartAxis;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class FlowMapHandler extends GroupChartHandler {
    @Getter
    private String type = "flow-map";
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var xAxis = result.getAxisMap().get(ChartAxis.xAxis);
        xAxis.addAll(Optional.ofNullable(view.getFlowMapStartName()).orElse(new ArrayList<>()));
        xAxis.addAll(Optional.ofNullable(view.getFlowMapEndName()).orElse(new ArrayList<>()));
        result.getAxisMap().put(ChartAxis.xAxis, xAxis);
        return result;
    }
}
