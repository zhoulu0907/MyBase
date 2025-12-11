package com.cmsr.api.visualization.dto;

import com.cmsr.api.visualization.vo.VisualizationOuterParamsDsInfoVO;
import com.cmsr.api.visualization.vo.VisualizationOuterParamsFilterInfoVO;
import com.cmsr.api.visualization.vo.VisualizationOuterParamsInfoVO;
import com.cmsr.api.visualization.vo.VisualizationOuterParamsTargetViewInfoVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class VisualizationOuterParamsInfoDTO extends VisualizationOuterParamsInfoVO {
    private String dvId;

    private List<VisualizationOuterParamsTargetViewInfoVO> targetViewInfoList=new ArrayList<>();

    //仪表板外部参数信息 dvId#paramName
    private String sourceInfo;

    //目标联动参数 targetViewId#targetFieldId
    private List<String> targetInfoList;

    private List<VisualizationOuterParamsDsInfoVO> dsInfoVOList = new ArrayList<>();

    private List<VisualizationOuterParamsFilterInfoVO> filterInfoVOList = new ArrayList<>();

}
