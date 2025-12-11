package com.cmsr.chart.charts.impl.others;

import com.cmsr.chart.charts.impl.YoyChartHandler;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class RichTextHandler extends YoyChartHandler {
    @Getter
    private String type = "rich-text";
    @Getter
    private String render = "custom";
}
