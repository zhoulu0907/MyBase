package com.cmsr.chart.charts.impl.table;

import com.cmsr.chart.charts.impl.DefaultChartHandler;
import com.cmsr.chart.utils.ChartDataBuild;
import com.cmsr.engine.sql.SQLProvider;
import com.cmsr.engine.trans.Dimension2SQLObj;
import com.cmsr.engine.trans.Quota2SQLObj;
import com.cmsr.engine.utils.Utils;
import com.cmsr.extensions.datasource.dto.DatasourceRequest;
import com.cmsr.extensions.datasource.dto.DatasourceSchemaDTO;
import com.cmsr.extensions.datasource.model.SQLMeta;
import com.cmsr.extensions.datasource.provider.Provider;
import com.cmsr.extensions.view.dto.*;
import com.cmsr.extensions.view.util.ChartDataUtil;
import com.cmsr.extensions.view.util.FieldUtil;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TableHeatmapHandler extends DefaultChartHandler {

    @Getter
    private String type = "t-heatmap";

    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result =  super.formatAxis(view);
        var xAxis = new ArrayList<ChartViewFieldDTO>(view.getXAxis());
        xAxis.addAll(view.getXAxisExt());
        var yAxis = new ArrayList<ChartViewFieldDTO>(view.getYAxis());
        yAxis.addAll(view.getExtColor());
        result.getAxisMap().put(ChartAxis.xAxis, xAxis);
        result.getAxisMap().put(ChartAxis.yAxis, yAxis);
        return result;
    }

    public Map<String, Object> buildResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        Map<String, Object> result = ChartDataBuild.transChartData( xAxis, new ArrayList<>(), view, data, false);
        return result;
    }

    @Override
    public <T extends ChartCalcDataResult> T calcChartResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, Map<String, Object> sqlMap, SQLMeta sqlMeta, Provider provider) {
        var dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }
        boolean needOrder = Utils.isNeedOrder(dsList);
        boolean crossDs = Utils.isCrossDs(dsMap);
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDsList(dsMap);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        var allFields = (List<ChartViewFieldDTO>) filterResult.getContext().get("allFields");
        Dimension2SQLObj.dimension2sqlObj(sqlMeta, xAxis, FieldUtil.transFields(allFields), crossDs, dsMap, Utils.getParams(FieldUtil.transFields(allFields)),  view.getCalParams(), pluginManage);
        Quota2SQLObj.quota2sqlObj(sqlMeta, yAxis, FieldUtil.transFields(allFields), crossDs, dsMap, Utils.getParams(FieldUtil.transFields(allFields)),  view.getCalParams(), pluginManage);
        String querySql = SQLProvider.createQuerySQL(sqlMeta, true, needOrder, view);
        querySql = provider.rebuildSQL(querySql, sqlMeta, crossDs, dsMap);
        datasourceRequest.setQuery(querySql);
        logger.debug("calcite chart sql: " + querySql);
        List<String[]> data = (List<String[]>) provider.fetchResultField(datasourceRequest).get("data");
        //自定义排序
        data = ChartDataUtil.resultCustomSort(xAxis, yAxis, view.getSortPriority(), data);
        //数据重组逻辑可重载
        var result = this.buildResult(view, formatResult, filterResult, data);
        T calcResult = (T) new ChartCalcDataResult();
        calcResult.setData(result);
        calcResult.setContext(filterResult.getContext());
        calcResult.setQuerySql(querySql);
        calcResult.setOriginData(data);
        return calcResult;
    }

}


