package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigActionVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigConnVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        BeanUtils.copyProperties(nodeConfigDO, nodeConfigConnVO);
        return nodeConfigConnVO;
    }

    @Override
    public NodeConfigActionVO findActionConfig(String nodeCode) {
        FlowNodeConfigDO nodeConfigDO = flowNodeConfigRepository.findByNodeCode(nodeCode);
        if (nodeConfigDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXIST);
        }
        NodeConfigActionVO nodeConfigActionVO = new NodeConfigActionVO();
        BeanUtils.copyProperties(nodeConfigDO, nodeConfigActionVO);
        return nodeConfigActionVO;
    }

}
