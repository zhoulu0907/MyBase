package com.cmsr.visualization.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmsr.extensions.view.dto.ChartViewDTO;
import com.cmsr.exception.DEException;
import com.cmsr.template.dao.auto.entity.VisualizationTemplateExtendData;
import com.cmsr.template.dao.auto.mapper.VisualizationTemplateExtendDataMapper;
import com.cmsr.utils.JsonUtil;
import com.cmsr.utils.LogUtil;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author : WangJiaHao
 * @date : 2023/11/13 13:25
 */
@Service
public class VisualizationTemplateExtendDataManage {

    @Resource
    private VisualizationTemplateExtendDataMapper extendDataMapper;

    public ChartViewDTO getChartDataInfo(Long viewId, ChartViewDTO view) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("view_id",viewId);
        List<VisualizationTemplateExtendData> extendDataList = extendDataMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(extendDataList)) {
            try{
                ChartViewDTO chartViewTemplate = JsonUtil.parseObject(extendDataList.get(0).getViewDetails(),ChartViewDTO.class);
                if(chartViewTemplate != null){
                    view.setData(chartViewTemplate.getData());
                }
            }catch (Exception e){
                LogUtil.error("未获取内置数据："+viewId);
            }

        } else {
            DEException.throwException("模板缓存数据中未获取指定图表数据：" + viewId);
        }
        return view;
    }
}
