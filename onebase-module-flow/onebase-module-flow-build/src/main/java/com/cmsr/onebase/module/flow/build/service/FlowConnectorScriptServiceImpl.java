package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.build.vo.ConnectorScriptVO;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorScriptRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorScriptDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorScriptReqVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Preconditions;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Setter
@Slf4j
@Service
public class FlowConnectorScriptServiceImpl implements FlowConnectorScriptService {

    @Autowired
    private FlowConnectorRepository connectorRepository;

    @Autowired
    private FlowConnectorScriptRepository connectorScriptRepository;

    @Override
    public PageResult<ConnectorScriptVO> pageConnectorScripts(PageConnectorScriptReqVO pageReqVO) {
        if (pageReqVO.getConnectorId() != null
                && (pageReqVO.getConnectorUuid() == null || pageReqVO.getApplicationId() == null)) {
            FlowConnectorDO connectorDO = connectorRepository.getById(pageReqVO.getConnectorId());
            Preconditions.checkNotNull(connectorDO, "连接器不存在");
            pageReqVO.setConnectorUuid(connectorDO.getConnectorUuid());
            pageReqVO.setApplicationId(connectorDO.getApplicationId());
        }
        PageResult<FlowConnectorScriptDO> pageDO = connectorScriptRepository.selectConnectorScriptPage(pageReqVO);
        List<ConnectorScriptVO> voList = new ArrayList<>();
        for (FlowConnectorScriptDO scriptDO : pageDO.getList()) {
            ConnectorScriptVO scriptVO = BeanUtils.toBean(scriptDO, ConnectorScriptVO.class);
            voList.add(scriptVO);
        }
        return new PageResult<>(voList, pageDO.getTotal());
    }

    @Override
    public ConnectorScriptVO getConnectorScript(Long scriptId) {
        FlowConnectorScriptDO scriptDO = connectorScriptRepository.getById(scriptId);
        if (scriptDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_SCRIPT_NOT_EXISTS);
        }
        return BeanUtils.toBean(scriptDO, ConnectorScriptVO.class);
    }

    @Override
    public Long createConnectorScript(CreateFlowConnectorScriptReqVO createVO) {
        FlowConnectorScriptDO connectorScriptDO = BeanUtils.toBean(createVO, FlowConnectorScriptDO.class);
        //
        FlowConnectorDO connectorDO = connectorRepository.getById(createVO.getConnectorId());
        if (connectorDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        connectorScriptDO.setConnectorUuid(connectorDO.getConnectorUuid());
        connectorScriptDO.setApplicationId(connectorDO.getApplicationId());
        connectorScriptDO.setInputParameter(jsonNodeToString(createVO.getInputParameter()));
        connectorScriptDO.setOutputParameter(jsonNodeToString(createVO.getOutputParameter()));

        connectorScriptRepository.save(connectorScriptDO);
        return connectorScriptDO.getId();
    }

    @Override
    public void updateConnectorScript(UpdateFlowConnectorScriptReqVO updateVO) {
        Long scriptId = updateVO.getId();
        FlowConnectorScriptDO oldDO = connectorScriptRepository.getById(scriptId);
        if (oldDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_SCRIPT_NOT_EXISTS);
        }
        oldDO.setScriptName(updateVO.getScriptName());
        oldDO.setScriptType(updateVO.getScriptType());
        oldDO.setDescription(updateVO.getDescription());
        oldDO.setRawScript(updateVO.getRawScript());
        oldDO.setInputParameter(JsonUtils.toJsonString(updateVO.getInputParameter()));
        oldDO.setOutputParameter(JsonUtils.toJsonString(updateVO.getOutputParameter()));

        connectorScriptRepository.updateById(oldDO);
    }

    @Override
    public void deleteById(Long scriptId) {
        connectorScriptRepository.removeById(scriptId);
    }


    public String jsonNodeToString(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode instanceof NullNode) {
            return null;
        }
        if (jsonNode instanceof TextNode) {
            return jsonNode.asText();
        }
        return JsonUtils.toJsonString(jsonNode);
    }


}
