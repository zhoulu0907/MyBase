package com.cmsr.chart.charts.impl.others;

import com.cmsr.chart.charts.impl.ExtQuotaChartHandler;
import com.cmsr.extensions.view.dto.AxisFormatResult;
import com.cmsr.extensions.view.dto.ChartAxis;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class WordCloudHandler extends ExtQuotaChartHandler {
    @Getter
    private String type = "word-cloud";
}
