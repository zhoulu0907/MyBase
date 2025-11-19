package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeTypeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FlowConnectorServiceImpl implements FlowConnectorService {

    @Resource
    private FlowConnectorRepository connectorRepository;

    @Resource
    private FlowNodeTypeRepository nodeTypeRepository;

    @Override
    public PageResult<FlowConnectorVO> getConnectorPage(PageConnectorReqVO pageReqVO) {
        PageResult<FlowConnectorDO> connectorPage = connectorRepository.getConnectorPage(pageReqVO);
        List<FlowConnectorVO> voList = new ArrayList<>();
        for (FlowConnectorDO connectorDO : connectorPage.getList()) {
            FlowConnectorVO connectorVO = convertToVO(connectorDO);
            voList.add(connectorVO);
        }
        return new PageResult<>(voList, connectorPage.getTotal());
    }

    private FlowConnectorVO convertToVO(FlowConnectorDO connectorDO) {
        FlowConnectorVO connectorVO = new FlowConnectorVO();
        connectorVO.setApplicationId(connectorDO.getApplicationId());
        connectorVO.setConnectorId(connectorDO.getId());
        connectorVO.setTypeCode(connectorDO.getTypeCode());
        connectorVO.setConnectorName(connectorVO.getConnectorName());
        connectorVO.setDescription(connectorDO.getDescription());
        connectorVO.setConfigStr(connectorDO.getConfig());
        connectorVO.setCreateTime(connectorDO.getCreateTime());
        return connectorVO;
    }

    @Override
    public FlowConnectorVO getConnectorDetail(Long connectorId) {
        FlowConnectorDO connectorDO = connectorRepository.findById(connectorId);
        if (connectorDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        return convertToVO(connectorDO);
    }

    @Override
    public Long createConnector(CreateFlowConnectorReqVO createVO) {
        FlowConnectorDO connectorDO = new FlowConnectorDO();
        connectorDO.setApplicationId(createVO.getApplicationId());
        connectorDO.setConnectorName(createVO.getConnecotrName());
        connectorDO.setDescription(createVO.getDescription());
        connectorDO.setTypeCode(createVO.getTypeCode());
        connectorDO.setConfig(createVO.getConfigAsStr());

        connectorDO = connectorRepository.insert(connectorDO);
        return connectorDO.getId();
    }

    @Override
    public void updateConnector(UpdateFlowConnectorReqVO updateVO) {
        Long connectorId = updateVO.getConnectorId();
        FlowConnectorDO oldDO = connectorRepository.findById(connectorId);
        if (oldDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        oldDO.setConnectorName(updateVO.getConnecotrName());
        oldDO.setDescription(updateVO.getDescription());
        oldDO.setConfig(updateVO.getConfigAsStr());

        connectorRepository.update(oldDO);
    }

    @Override
    public void deleteById(Long connectorId) {
        connectorRepository.deleteById(connectorId);
    }
}
