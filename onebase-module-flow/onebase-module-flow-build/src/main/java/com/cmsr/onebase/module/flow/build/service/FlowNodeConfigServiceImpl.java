package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.build.vo.ConnectorTypeListVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigActionVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigConnVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/11/17 17:31
 */
@Slf4j
@Setter
@Service
public class FlowNodeConfigServiceImpl implements FlowNodeConfigService {

    @Autowired
    private FlowNodeConfigRepository flowNodeConfigRepository;

    @Override
    public PageResult<NodeConfigVO> pageNodeType(PageNodeConfigReqVO reqVO) {
        PageResult<FlowNodeConfigDO> dos = flowNodeConfigRepository.pageNodeConfigByCode(reqVO);
        List<NodeConfigVO> vos = BeanUtils.toBean(dos.getList(), NodeConfigVO.class);
        return new PageResult(vos, dos.getTotal());
    }

    @Override
    public NodeConfigConnVO findConnConfig(String nodeCode) {
        FlowNodeConfigDO nodeConfigDO = flowNodeConfigRepository.findByNodeCode(nodeCode);
        if (nodeConfigDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXIST);
        }
        NodeConfigConnVO nodeConfigConnVO = new NodeConfigConnVO();
        nodeConfigConnVO.setConnConfigType(nodeConfigDO.getConnConfigType());
        nodeConfigConnVO.setConnConfig(JsonUtils.parseTree(nodeConfigDO.getConnConfig()));
        return nodeConfigConnVO;
    }

    @Override
    public NodeConfigActionVO findActionConfig(String nodeCode) {
        FlowNodeConfigDO nodeConfigDO = flowNodeConfigRepository.findByNodeCode(nodeCode);
        if (nodeConfigDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXIST);
        }
        NodeConfigActionVO nodeConfigActionVO = new NodeConfigActionVO();
        nodeConfigActionVO.setActionConfigType(nodeConfigDO.getActionConfigType());
        nodeConfigActionVO.setActionConfig(JsonUtils.parseTree(nodeConfigDO.getActionConfig()));
        return nodeConfigActionVO;
    }

    @Override
    public List<ConnectorTypeListVO> getAllConnectorTypes() {
        List<FlowNodeConfigDO> dos = flowNodeConfigRepository.listAllConnectorTypes();
        return dos.stream()
                .filter(doObj -> {
                    // level1 cannot be empty
                    if (StringUtils.isBlank(doObj.getLevel1Code())) {
                        log.warn("[ConnectorTypeList] Connector skipped due to empty level1, nodeCode: {}", doObj.getNodeCode());
                        return false;
                    }
                    return true;
                })
                .map(doObj -> {
                    ConnectorTypeListVO vo = new ConnectorTypeListVO();
                    // Build category: level1-level2-level3
                    StringBuilder category = new StringBuilder(doObj.getLevel1Code());
                    if (StringUtils.isNotBlank(doObj.getLevel2Code())) {
                        category.append("-").append(doObj.getLevel2Code());
                        if (StringUtils.isNotBlank(doObj.getLevel3Code())) {
                            category.append("-").append(doObj.getLevel3Code());
                        }
                    }
                    vo.setCategory(category.toString());
                    vo.setNodeName(doObj.getNodeName());
                    vo.setNodeCode(doObj.getNodeCode());
                    return vo;
                })
                .collect(Collectors.toList());
    }

}
