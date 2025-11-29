package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;

public interface FlowConnectorService {

    PageResult<FlowConnectorVO> pageConnectors(PageConnectorReqVO pageReqVO);

    FlowConnectorVO getConnectorDetail(Long connectorId);

    Long createConnector(CreateFlowConnectorReqVO createVO);

    void updateConnector(UpdateFlowConnectorReqVO updateVO);

    void deleteById(Long connectorId);
}
