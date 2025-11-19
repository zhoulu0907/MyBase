package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.ConnectorScriptVO;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorScriptReqVO;

public interface FlowConnectorScriptService {

    PageResult<ConnectorScriptVO> getConnectorScriptPage(PageConnectorScriptReqVO pageReqVO);

    ConnectorScriptVO getConnectorScript(Long scriptId);

    Long createConnectorScript(CreateFlowConnectorScriptReqVO createVO);

    void updateConnectorScript(UpdateFlowConnectorScriptReqVO updateVO);

    void deleteById(Long scriptId);
}
