package com.cmsr.chart.server;

import com.cmsr.api.chart.ChartData;
import com.cmsr.api.chart.request.ChartExcelRequest;
import com.cmsr.chart.manage.ChartDataManage;
import com.cmsr.constant.CommonConstants;
import com.cmsr.exception.DEException;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import com.cmsr.result.ResultCode;
import com.cmsr.visualization.manage.VisualizationTemplateExtendDataManage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/chartDataN")
public class CharDataImpl implements ChartData {

    @Resource
    private ChartDataManage chartDataManage;

    @Resource
    private VisualizationTemplateExtendDataManage extendDataManage;

    @Override
    public Object getData(ChartViewDTO chartViewDTO) throws Exception {
        try {
            // 从模板数据获取
            if (CommonConstants.VIEW_DATA_FROM.TEMPLATE.equalsIgnoreCase(chartViewDTO.getDataFrom())) {
                chartViewDTO = extendDataManage.getChartDataInfo(chartViewDTO.getId(), chartViewDTO);
            } else {
                chartViewDTO = chartDataManage.calcData(chartViewDTO);
            }
            Object data = chartViewDTO.getData().get("data");
//            return chartViewDTO;
            return data;
        } catch (Exception e) {
            DEException.throwException(ResultCode.DATA_IS_WRONG.code(), e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public void innerExportDetails(ChartExcelRequest request, HttpServletResponse response) throws Exception {

    }

    @Override
    public void innerExportDataSetDetails(ChartExcelRequest request, HttpServletResponse response) throws Exception {

    }

    @Override
    public List<String> getFieldData(ChartViewDTO view, Long fieldId, String fieldType) throws Exception {
        return List.of();
    }

    @Override
    public List<String> getDrillFieldData(ChartViewDTO view, Long fieldId) throws Exception {
        return List.of();
    }
}
