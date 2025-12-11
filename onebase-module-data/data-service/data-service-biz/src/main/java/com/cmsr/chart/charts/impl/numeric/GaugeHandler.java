package com.cmsr.chart.charts.impl.numeric;

import com.cmsr.extensions.view.dto.AxisFormatResult;
import com.cmsr.extensions.view.dto.ChartAxis;
import com.cmsr.dataset.manage.DatasetTableFieldManage;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import com.cmsr.extensions.view.dto.ChartViewFieldDTO;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GaugeHandler extends NumericalChartHandler {
    @Getter
    private String type = "gauge";
    @Resource
    private DatasetTableFieldManage datasetTableFieldManage;

    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var axisMap = new HashMap<ChartAxis, List<ChartViewFieldDTO>>();
        var yAxis = new ArrayList<>(view.getYAxis());
        Map<String, Object> customAttr = view.getCustomAttr();
        Map<String, Object> size = (Map<String, Object>) customAttr.get("misc");
        ChartViewFieldDTO gaugeMinViewField = getDynamicField(size, "gaugeMinType", "gaugeMinField");
        if (gaugeMinViewField != null) {
            yAxis.add(gaugeMinViewField);
        }
        ChartViewFieldDTO gaugeMaxViewField = getDynamicField(size, "gaugeMaxType", "gaugeMaxField");
        if (gaugeMaxViewField != null) {
            yAxis.add(gaugeMaxViewField);
        }
        axisMap.put(ChartAxis.xAxis, new ArrayList<>());
        axisMap.put(ChartAxis.yAxis, yAxis);
        var context = new HashMap<String, Object>();
        var result = new AxisFormatResult(axisMap, context);
        return result;
    }

}
