package com.cmsr.chart.charts.impl.others;

import com.cmsr.chart.charts.impl.ExtQuotaChartHandler;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class WaterfallHandler extends ExtQuotaChartHandler {
    @Getter
    private String type = "waterfall";
}
