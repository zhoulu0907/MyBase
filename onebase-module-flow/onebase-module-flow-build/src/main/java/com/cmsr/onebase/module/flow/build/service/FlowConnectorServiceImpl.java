package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeTypeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Service
public class FlowConnectorServiceImpl implements FlowConnectorService {

    @Autowired
    private FlowConnectorRepository connectorRepository;

    @Autowired
    private FlowNodeTypeRepository nodeTypeRepository;

    @Override
    public PageResult<FlowConnectorVO> pageConnectors(PageConnectorReqVO pageReqVO) {
        PageResult<FlowConnectorDO> connectorPage = connectorRepository.selectConnectorPage(pageReqVO);
        List<FlowConnectorVO> voList = new ArrayList<>();
        for (FlowConnectorDO connectorDO : connectorPage.getList()) {
            FlowConnectorVO connectorVO = convertToVO(connectorDO);
            connectorVO.setConfig(null);
            voList.add(connectorVO);
        }
        return new PageResult<>(voList, connectorPage.getTotal());
    }

    private FlowConnectorVO convertToVO(FlowConnectorDO connectorDO) {
        FlowConnectorVO connectorVO = BeanUtils.toBean(connectorDO, FlowConnectorVO.class);
//        if (StringUtils.isBlank(connectorDO.getDescription())) {
//            connectorVO.setDescription("");
//        } else {
//            connectorVO.setDescription(connectorDO.getDescription());
//        }
        String config = connectorDO.getConfig();
        if (StringUtils.isNotBlank(config)) {
            connectorVO.setConfig(JsonUtils.parseTree(config));
        }
        connectorVO.setConnectorVersion("1.0.0");
        return connectorVO;
    }

    @Override
    public FlowConnectorVO getConnectorDetail(Long connectorId) {
        FlowConnectorDO connectorDO = connectorRepository.getById(connectorId);
        if (connectorDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        return convertToVO(connectorDO);
    }

    @Override
    public Long createConnector(CreateFlowConnectorReqVO createVO) {
        FlowConnectorDO connectorDO = BeanUtils.toBean(createVO, FlowConnectorDO.class);
        connectorDO.setConnectorUuid(UuidUtils.getUuid());
        connectorDO.setConfig(jsonNodeToString(createVO.getConfig()));
        connectorRepository.save(connectorDO);
        return connectorDO.getId();
    }

    @Override
    public void updateConnector(UpdateFlowConnectorReqVO updateVO) {
        Long connectorId = updateVO.getConnectorId();
        FlowConnectorDO oldDO = connectorRepository.getById(connectorId);
        if (oldDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        oldDO.setConnectorName(updateVO.getConnectorName());
        oldDO.setDescription(updateVO.getDescription());
        oldDO.setConfig(updateVO.getConfigAsStr());
        connectorRepository.updateById(oldDO);
    }

    @Override
    public void deleteById(Long connectorId) {
        connectorRepository.removeById(connectorId);
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
