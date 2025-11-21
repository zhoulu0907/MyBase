package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.ConnectorScriptVO;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.connector.FlowConnectorScriptRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.connector.FlowConnectorScriptDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorScriptReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FlowConnectorScriptServiceImpl implements FlowConnectorScriptService {

    @Resource
    private FlowConnectorRepository connectorRepository;

    @Resource
    private FlowConnectorScriptRepository connectorScriptRepository;

    @Override
    public PageResult<ConnectorScriptVO> getConnectorScriptPage(PageConnectorScriptReqVO pageReqVO) {
        PageResult<FlowConnectorScriptDO> pageDO = connectorScriptRepository.getConnectorScriptPage(pageReqVO);
        List<ConnectorScriptVO> voList = new ArrayList<>();
        for (FlowConnectorScriptDO scriptDO : pageDO.getList()) {
            ConnectorScriptVO scriptVO = convertToVO(scriptDO);
            voList.add(scriptVO);
        }

        return new PageResult<>(voList, pageDO.getTotal());
    }

    @Override
    public ConnectorScriptVO getConnectorScript(Long scriptId) {
        FlowConnectorScriptDO scriptDO = connectorScriptRepository.findById(scriptId);
        if (scriptDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_SCRIPT_NOT_EXISTS);
        }
        return convertToVO(scriptDO);
    }

    private ConnectorScriptVO convertToVO(FlowConnectorScriptDO connectorScriptDO) {
        ConnectorScriptVO connectorScriptVO = new ConnectorScriptVO();
        connectorScriptVO.setScriptId(connectorScriptDO.getId());
        connectorScriptVO.setConnectorId(connectorScriptDO.getConnectorId());
        connectorScriptVO.setScriptName(connectorScriptDO.getScriptName());
        connectorScriptVO.setScriptType(connectorScriptDO.getScriptType());
        connectorScriptVO.setDescription(connectorScriptDO.getDescription());
        connectorScriptVO.setRawScript(connectorScriptDO.getRawScript());
        connectorScriptVO.setInputParameter(connectorScriptDO.getInputParameter());
        connectorScriptVO.setOutputParameter(connectorScriptDO.getOutputParameter());
        connectorScriptVO.setCreateTime(connectorScriptDO.getCreateTime());

        return connectorScriptVO;
    }

    @Override
    public Long createConnectorScript(CreateFlowConnectorScriptReqVO createVO) {
        FlowConnectorScriptDO connectorScriptDO = new FlowConnectorScriptDO();
        connectorScriptDO.setConnectorId(createVO.getConnectorId());
        connectorScriptDO.setScriptName(createVO.getScriptName());
        connectorScriptDO.setScriptType(createVO.getScriptType());
        connectorScriptDO.setDescription(createVO.getDescription());
        connectorScriptDO.setRawScript(createVO.getRawScript());
        connectorScriptDO.setInputParameter(createVO.getInputParameter());
        connectorScriptDO.setOutputParameter(createVO.getOutputParameter());

        connectorScriptDO = connectorScriptRepository.insert(connectorScriptDO);
        return connectorScriptDO.getId();
    }

    @Override
    public void updateConnectorScript(UpdateFlowConnectorScriptReqVO updateVO) {
        Long scriptId = updateVO.getScriptId();
        FlowConnectorScriptDO oldDO = connectorScriptRepository.findById(scriptId);
        if (oldDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_SCRIPT_NOT_EXISTS);
        }
        oldDO.setScriptName(updateVO.getScriptName());
        oldDO.setScriptType(updateVO.getScriptType());
        oldDO.setDescription(updateVO.getDescription());
        oldDO.setRawScript(updateVO.getRawScript());
        oldDO.setInputParameter(updateVO.getInputParameter());
        oldDO.setOutputParameter(updateVO.getOutputParameter());

        connectorScriptRepository.update(oldDO);
    }

    @Override
    public void deleteById(Long scriptId) {
        connectorScriptRepository.deleteById(scriptId);
    }
}
