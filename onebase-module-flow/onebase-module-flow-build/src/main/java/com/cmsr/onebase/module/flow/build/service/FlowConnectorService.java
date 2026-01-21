package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;

import java.util.List;

public interface FlowConnectorService {

    PageResult<FlowConnectorVO> pageConnectors(PageConnectorReqVO pageReqVO);

    FlowConnectorVO getConnectorDetail(Long connectorId);

    Long createConnector(CreateFlowConnectorReqVO createVO);

    void updateConnector(UpdateFlowConnectorReqVO updateVO);

    void deleteById(Long connectorId);

    /**
     * List connector instances by type code
     */
    List<FlowConnectorVO> listByType(String typeCode);

    /**
     * Get action list by connector UUID
     *
     * @param connectorUuid the connector UUID
     * @return list of action keys
     */
    List<String> getActionsByConnectorUuid(String connectorUuid);
}
