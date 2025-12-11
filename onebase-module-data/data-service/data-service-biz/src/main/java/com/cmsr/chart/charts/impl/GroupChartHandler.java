package com.cmsr.chart.charts.impl;


import com.cmsr.extensions.view.dto.AxisFormatResult;
import com.cmsr.extensions.view.dto.ChartAxis;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import com.cmsr.extensions.view.dto.ChartViewFieldDTO;

import java.util.ArrayList;

public class GroupChartHandler extends YoyChartHandler {
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var xAxis = new ArrayList<ChartViewFieldDTO>(view.getXAxis());
        xAxis.addAll(view.getXAxisExt());
        result.getAxisMap().put(ChartAxis.xAxis, xAxis);
        return result;
    }
}
