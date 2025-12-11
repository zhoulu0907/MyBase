package com.cmsr.visualization.manage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.cmsr.api.visualization.vo.DataVisualizationVO;
import com.cmsr.chart.constant.ChartConstants;
import com.cmsr.chart.manage.ChartDataManage;
import com.cmsr.chart.manage.ChartViewManege;
import com.cmsr.constant.CommonConstants;
import com.cmsr.constant.DeTypeConstants;
import com.cmsr.dataset.server.DatasetFieldServer;
import com.cmsr.exception.DEException;
import com.cmsr.exportCenter.util.ExportCenterUtils;
import com.cmsr.extensions.view.dto.ChartExtFilterDTO;
import com.cmsr.extensions.view.dto.ChartExtRequest;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import com.cmsr.extensions.view.dto.ChartViewFieldDTO;
import com.cmsr.utils.AuthUtils;
import com.cmsr.utils.JsonUtil;
import com.cmsr.visualization.bo.ExcelSheetModel;
import com.cmsr.visualization.dao.ext.mapper.ExtDataVisualizationMapper;
import com.cmsr.visualization.template.FilterBuildTemplate;
import com.cmsr.visualization.utils.VisualizationExcelUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class CoreVisualizationExportManage {
    @Resource
    private ExtDataVisualizationMapper extDataVisualizationMapper;

    @Resource
    private ChartViewManege chartViewManege;

    @Resource
    private ChartDataManage chartDataManage;

    @Resource
    private VisualizationTemplateExtendDataManage extendDataManage;

    @Resource
    private DatasetFieldServer datasetFieldServer;

    public String getResourceName(Long dvId, String busiFlag) {
        DataVisualizationVO visualization = extDataVisualizationMapper.findDvInfo(dvId, busiFlag, "core");
        if (ObjectUtils.isEmpty(visualization)) DEException.throwException("资源不存在或已经被删除...");
        return visualization.getName();
    }

    public File exportExcel(Long dvId, String busiFlag, List<Long> viewIdList, boolean onlyDisplay, String filterJson) throws Exception {
        DataVisualizationVO visualization = extDataVisualizationMapper.findDvInfo(dvId, busiFlag, "core");
        if (ObjectUtils.isEmpty(visualization)) DEException.throwException("资源不存在或已经被删除...");
        List<ChartViewDTO> chartViewDTOS = chartViewManege.listBySceneId(dvId, CommonConstants.RESOURCE_TABLE.CORE);

        String componentsJson = visualization.getComponentData();
        List<Map<String, Object>> components = JsonUtil.parseList(componentsJson, tokenType);
        List<Long> idList = components.stream().filter(c -> ObjectUtils.isNotEmpty(c.get("id"))).map(component -> Long.parseLong(component.get("id").toString())).toList();

        if (CollectionUtils.isNotEmpty(viewIdList)) {
            chartViewDTOS = chartViewDTOS.stream().filter(item -> idList.contains(item.getId()) && viewIdList.contains(item.getId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(chartViewDTOS)) return null;
        Map<Long, ChartExtRequest> chartExtRequestMap = buildViewRequest(filterJson);
        List<ExcelSheetModel> sheets = new ArrayList<>();
        for (int i = 0; i < chartViewDTOS.size(); i++) {
            ChartViewDTO view = chartViewDTOS.get(i);
            ChartExtRequest extRequest = chartExtRequestMap.get(view.getId());
            if (ObjectUtils.isNotEmpty(extRequest)) {
                view.setChartExtRequest(extRequest);
            }
            view.getChartExtRequest().setUser(AuthUtils.getUser().getUserId());
            view.setTitle((i + 1) + "-" + view.getTitle());
            sheets.addAll(exportViewData(view));
        }

        return VisualizationExcelUtils.exportExcel(sheets, visualization.getName(), visualization.getId().toString());
    }

    private ExcelSheetModel exportSingleData(Map<String, Object> chart, String title) {
        ExcelSheetModel result = new ExcelSheetModel();
        Object objectFields = chart.get("fields");
        List<ChartViewFieldDTO> fields = (List<ChartViewFieldDTO>) objectFields;
        List<String> heads = new ArrayList<>();
        List<String> headKeys = new ArrayList<>();
        List<Integer> fieldTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(field -> {
                Object name = field.getName();
                Object dataeaseName = field.getDataeaseName();
                Object deType = field.getDeType();
                if (ObjectUtils.isNotEmpty(name) && ObjectUtils.isNotEmpty(dataeaseName)) {
                    heads.add(name.toString());
                    headKeys.add(dataeaseName.toString());
                    if (deType == null) {
                        field.setDeType(DeTypeConstants.DE_STRING);
                        deType = DeTypeConstants.DE_STRING;
                    }
                    fieldTypes.add((int) deType);
                }
            });
        }
        Object objectTableRow = chart.get("tableRow");
        if (objectTableRow == null) {
            objectTableRow = chart.get("sourceData");
        }
        List<Map<String, Object>> tableRow = (List<Map<String, Object>>) objectTableRow;

        List<List<String>> details = tableRow.stream().map(row -> {
            List<String> tempList = new ArrayList<>();
            for (int i = 0; i < headKeys.size(); i++) {
                String key = headKeys.get(i);
                Object val = row.get(key);
                if (ObjectUtils.isEmpty(val)) {
                    tempList.add(StringUtils.EMPTY);
                } else if (fieldTypes.get(i) == 3) {
                    tempList.add(filterInvalidDecimal(val.toString()));
                } else {
                    tempList.add(val.toString());
                }
            }
            return tempList;
        }).collect(Collectors.toList());
        result.setHeads(heads);
        result.setData(details);
        result.setFiledTypes(fieldTypes);
        result.setSheetName(title);
        return result;
    }

    private List<ExcelSheetModel> exportViewData(ChartViewDTO request) {

        ChartViewDTO chartViewDTO = null;
        request.setIsExcelExport(true);
        String type = request.getType();
        if (StringUtils.equals("table-info", type)) {
            request.setResultCount(Math.toIntExact(ExportCenterUtils.getExportLimit("view")));
            request.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);
        }
        if (CommonConstants.VIEW_DATA_FROM.TEMPLATE.equalsIgnoreCase(request.getDataFrom())) {
            chartViewDTO = extendDataManage.getChartDataInfo(request.getId(), request);
        } else {
            try {
                chartViewDTO = chartDataManage.calcData(request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String title = chartViewDTO.getTitle();
        Map<String, Object> chart = chartViewDTO.getData();
        List<ExcelSheetModel> resultList = new ArrayList<>();
        boolean leftExist = ObjectUtils.isNotEmpty(chart.get("left"));
        boolean rightExist = ObjectUtils.isNotEmpty(chart.get("right"));
        if (!leftExist && !rightExist) {
            ExcelSheetModel sheetModel = exportSingleData(chart, title);
            resultList.add(sheetModel);
            return resultList;
        }
        if (leftExist) {
            ExcelSheetModel sheetModel = exportSingleData((Map<String, Object>) chart.get("left"), title + "_left");
            resultList.add(sheetModel);
        }
        if (rightExist) {
            ExcelSheetModel sheetModel = exportSingleData((Map<String, Object>) chart.get("right"), title + "_right");
            resultList.add(sheetModel);
        }
        return resultList;
    }

    private String filterInvalidDecimal(String sourceNumberStr) {
        if (StringUtils.isNotBlank(sourceNumberStr) && StringUtils.contains(sourceNumberStr, ".")) {
            sourceNumberStr = sourceNumberStr.replaceAll("0+?$", "");
            sourceNumberStr = sourceNumberStr.replaceAll("[.]$", "");
        }
        return sourceNumberStr;
    }

    private final TypeReference<List<Map<String, Object>>> tokenType = new TypeReference<List<Map<String, Object>>>() {
    };

    private Map<Long, ChartExtRequest> buildViewRequest(String filterJson) {
        if (StringUtils.isBlank(filterJson)) {
            return new HashMap<>();
        }
        Map<Long, ChartExtRequest> extRequestMap = JsonUtil.parseObject(filterJson, new TypeReference<Map<Long, ChartExtRequest>>() {
        });
        extRequestMap.forEach((key, chartExtRequest) -> {
            chartExtRequest.setQueryFrom("panel");
            chartExtRequest.setResultCount(Math.toIntExact(ExportCenterUtils.getExportLimit("view")));
            chartExtRequest.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);
            chartExtRequest.setPageSize(ExportCenterUtils.getExportLimit("view"));
        });
        return extRequestMap;
    }

    private Map<String, ChartExtRequest> buildViewRequest(DataVisualizationVO panelDto, Boolean justView) {
        String componentsJson = panelDto.getComponentData();
        List<Map<String, Object>> components = JsonUtil.parseList(componentsJson, tokenType);
        Map<String, ChartExtRequest> result = new HashMap<>();
        Map<String, List<ChartExtFilterDTO>> panelFilters = FilterBuildTemplate.buildEmpty(components);
        for (Map.Entry<String, List<ChartExtFilterDTO>> entry : panelFilters.entrySet()) {
            List<ChartExtFilterDTO> chartExtFilterRequests = entry.getValue();
            ChartExtRequest chartExtRequest = new ChartExtRequest();
            chartExtRequest.setQueryFrom("panel");
            chartExtRequest.setFilter(chartExtFilterRequests);
            chartExtRequest.setResultCount(Math.toIntExact(ExportCenterUtils.getExportLimit("view")));
            chartExtRequest.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);
            chartExtRequest.setPageSize(ExportCenterUtils.getExportLimit("view"));
            result.put(entry.getKey(), chartExtRequest);
        }
        return result;
    }

}
