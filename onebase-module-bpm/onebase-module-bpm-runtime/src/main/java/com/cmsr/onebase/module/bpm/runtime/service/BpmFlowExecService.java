package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowChartVO;

import java.util.List;

public interface BpmFlowExecService {
    /**
     * 获取流程图
     *
     * @param flowCode 流程编码
     * @param appId    应用ID
     * @return 流程图
     */
   List<BpmFlowChartVO> getFlowChart(String flowCode, String appId);
}
