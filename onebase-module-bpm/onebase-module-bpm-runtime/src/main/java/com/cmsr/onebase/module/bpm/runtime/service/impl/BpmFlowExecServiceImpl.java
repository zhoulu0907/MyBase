package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowExecService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowChartVO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class BpmFlowExecServiceImpl implements BpmFlowExecService {
    @Resource
    private NodeService nodeService;
    @Resource
    FlowInstanceRepository flowInstanceRepository;
    @Override
    public List<BpmFlowChartVO> getFlowChart(String flowCode, String appId) {
        // 获取流程节点
        List<Node> nodes = nodeService.getPublishByFlowCode(flowCode);
        if(CollectionUtils.isNotEmpty(nodes)){
            for (Node node : nodes){
                List<String>  permissionList = StringUtils.str2List(node.getPermissionFlag(), FlowCons.SPLIT_AT);

            }
        }

        log.info("流程节点:{}", nodes);

        return List.of();
    }
}
