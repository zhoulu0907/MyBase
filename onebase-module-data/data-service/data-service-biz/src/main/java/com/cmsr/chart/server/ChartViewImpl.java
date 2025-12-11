package com.cmsr.chart.server;

import com.cmsr.api.chart.ChartView;
import com.cmsr.api.chart.vo.ChartBaseVO;
import com.cmsr.api.chart.vo.ViewSelectorVO;
import com.cmsr.chart.manage.ChartViewManege;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import com.cmsr.extensions.view.dto.ChartViewFieldDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("chartN")
public class ChartViewImpl implements ChartView {

    @Resource
    private ChartViewManege chartViewManege;

    @Override
    public ChartViewDTO getData(Long id) throws Exception {
        return null;
    }

    @Override
    public Map<String, List<ChartViewFieldDTO>> listByDQ(Long id, Long chartId, ChartViewDTO dto) {
        return chartViewManege.listByDQ(id, chartId, dto);
    }

    @Override
    public ChartViewDTO save(ChartViewDTO dto) throws Exception {
        return null;
    }

    @Override
    public String checkSameDataSet(String viewIdSource, String viewIdTarget) {
        return "";
    }

    @Override
    public ChartViewDTO getDetail(Long id, String resourceTable) {
        return null;
    }

    @Override
    public List<ViewSelectorVO> viewOption(Long resourceId) {
        return List.of();
    }

    @Override
    public void copyField(Long id, Long chartId) {

    }

    @Override
    public void deleteField(Long id) {

    }

    @Override
    public void deleteFieldByChart(Long chartId) {

    }

    @Override
    public ChartBaseVO chartBaseInfo(Long id, String resourceTable) {
        return null;
    }
}
