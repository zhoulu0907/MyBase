package com.cmsr.onebase.module.bpm.convert;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.VersionStatusEnum;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDefJsonVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.strategy.NodeVOStrategyManager;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.dto.SkipJson;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程设计转换类
 *
 * @author liyang
 * @date 2025-10-21
 */
@Slf4j
@Component
public class BpmDesignConvertImpl implements BpmDesignConvert {
    @Resource
    private NodeVOStrategyManager strategyManager;

    /**
     * DefJson转换为BpmDesignVO
     *
     * @param defJson WarmFlow的DefJson
     * @return BpmDesignVO
     */
    public BpmDesignVO toFlowDesignVO(DefJson defJson) {
        if (defJson == null) {
            return null;
        }

        BpmDesignVO flowDesignVO = new BpmDesignVO();
        flowDesignVO.setBusinessId(Long.valueOf(defJson.getFormPath()));
        flowDesignVO.setFlowCode(defJson.getFlowCode());
        flowDesignVO.setFlowName(defJson.getFlowName());
        flowDesignVO.setId(defJson.getId());
        flowDesignVO.setVersion("V" + defJson.getVersion());

        Integer isPublish = defJson.getIsPublish();

        if (isPublish.equals(PublishStatus.PUBLISHED.getKey())) {
             flowDesignVO.setVersionStatus(VersionStatusEnum.PUBLISHED.getCode());
        } else if (isPublish.equals(PublishStatus.UNPUBLISHED.getKey())) {
             flowDesignVO.setVersionStatus(VersionStatusEnum.DESIGNING.getCode());
        } else if (isPublish.equals(PublishStatus.EXPIRED.getKey())) {
             flowDesignVO.setVersionStatus(VersionStatusEnum.PREVIOUS.getCode());
        }

        String ext = defJson.getExt();

        // 转版本
        String defaultVersionAlias = "流程版本V" + defJson.getVersion();

        if (StringUtils.isNotBlank(ext)) {
            BpmDefinitionExtDTO extDto = JsonUtils.parseObject(ext, BpmDefinitionExtDTO.class);

            if (extDto != null) {
                flowDesignVO.setVersionAlias(extDto.getVersionAlias());
            }
        }

        // 设置默认版本备注
        if (StringUtils.isBlank(flowDesignVO.getVersionAlias())) {
            flowDesignVO.setVersionAlias(defaultVersionAlias);
        }

        // 转换节点配置
        flowDesignVO.setBpmDefJson(nodeJsonListToBpmDefJson(defJson.getNodeList()));

        return flowDesignVO;
    }

    /**
     * BpmDesignVO转换为DefJson
     *
     * @param flowDesignVO BpmDesignVO
     * @return DefJson
     */
    public DefJson toDefJson(BpmDesignVO flowDesignVO) {
        if (flowDesignVO == null) {
           return null;
        }

        DefJson defJson = new DefJson();
        defJson.setId(flowDesignVO.getId());
        defJson.setFlowCode(flowDesignVO.getFlowCode());
        defJson.setFlowName(flowDesignVO.getFlowName());
        defJson.setFormPath(String.valueOf(flowDesignVO.getBusinessId()));

        // 构建ext
        BpmDefinitionExtDTO extDto = new BpmDefinitionExtDTO();

        if (flowDesignVO.getVersionAlias() != null) {
            extDto.setVersionAlias(flowDesignVO.getVersionAlias());
        }

        defJson.setExt(JsonUtils.toJsonString(extDto));

        // 固定值
        defJson.setModelValue("CLASSICS");
        defJson.setFormCustom("Y");

        // 转节点和边
        defJson.setNodeList(toNodeJsonList(flowDesignVO));

        return defJson;
    }

    /**
     * 节点JSON列表转换为流程定义JSON字符串
     *
     * @param nodeJsonList 节点JSON列表
     * @return 流程定义JSON
     */
    private String nodeJsonListToBpmDefJson(List<NodeJson> nodeJsonList) {
        if (nodeJsonList == null) {
            return null;
        }

        BpmDefJsonVO bpmDefJsonVO = new BpmDefJsonVO();

        // 转换节点
        for (NodeJson nodeJson : nodeJsonList) {
            // 从ext里获取扩展信息
            String ext = nodeJson.getExt();
            if (ext == null) {
                continue;
            }

            // 节点扩展信息/配置 - 现在返回的是BaseNodeVO
            BaseNodeVO nodeVO = strategyManager.createNodeVO(ext);

            // 设置节点基本信息
            nodeVO.setId(nodeJson.getNodeCode());
            nodeVO.setName(nodeJson.getNodeName());

            // 添加节点视图到列表
            if (bpmDefJsonVO.getNodes() == null) {
                bpmDefJsonVO.setNodes(new ArrayList<>());
            }

            bpmDefJsonVO.getNodes().add(nodeVO);
        }

        // 处理边
        for (NodeJson nodeJson : nodeJsonList) {
            for (SkipJson skipJson : nodeJson.getSkipList()) {
                BpmDefJsonVO.EdgeVO edgeVO = new BpmDefJsonVO.EdgeVO();
                edgeVO.setSourceNodeId(skipJson.getNowNodeCode());
                edgeVO.setTargetNodeId(skipJson.getNextNodeCode());
                edgeVO.setName(skipJson.getSkipName());
                edgeVO.setType(skipJson.getSkipType());
                edgeVO.setSkipCondition(skipJson.getSkipCondition());

                // 添加边视图到列表
                if (bpmDefJsonVO.getEdges() == null) {
                    bpmDefJsonVO.setEdges(new ArrayList<>());
                }

                bpmDefJsonVO.getEdges().add(edgeVO);
            }
        }

        return JsonUtils.toJsonString(bpmDefJsonVO);
    }

    private List<NodeJson> toNodeJsonList(BpmDesignVO flowDesignVO) {
        if (flowDesignVO == null) {
            return null;
        }

        String bpmDefJson = flowDesignVO.getBpmDefJson();
        if (StringUtils.isBlank(bpmDefJson)) {
            return null;
        }

        BpmDefJsonVO bpmDefJsonVO = JsonUtils.parseObject(bpmDefJson, BpmDefJsonVO.class);
        if (bpmDefJsonVO == null) {
            return null;
        }

        Map<String, NodeJson> nodeJsonMap = new HashMap<>();
        List<NodeJson> nodeJsonList = new ArrayList<>();

        for (BaseNodeVO nodeVO : bpmDefJsonVO.getNodes()) {
            NodeJson nodeJson = new NodeJson();
            nodeJson.setNodeCode(nodeVO.getId());
            nodeJson.setNodeName(nodeVO.getName());
            nodeJson.setFormPath(String.valueOf(flowDesignVO.getBusinessId()));

            // 设置节点类型
            BpmNodeTypeEnum bpmNodeType = BpmNodeTypeEnum.getByCode(nodeVO.getType());
            if (bpmNodeType != null) {
                NodeType nodeType = bpmNodeType.toWarmFlowNodeType();

                if (nodeType != null) {
                    nodeJson.setNodeType(nodeType.getKey());
                }
            }

            // 固定值
            nodeJson.setCoordinate("0,0|0,0|0,0");
            nodeJson.setFormCustom("Y");

            // 设置ext：使用策略管理器构建扩展信息
            nodeJson.setExt(strategyManager.buildNodeExtData(nodeVO));

            // 添加到映射
            nodeJsonMap.put(nodeJson.getNodeCode(), nodeJson);
            nodeJsonList.add(nodeJson);
        }

        // 处理边
        for (BpmDefJsonVO.EdgeVO edgeVO : bpmDefJsonVO.getEdges()) {
            String sourceNodeCode = edgeVO.getSourceNodeId();
            String targetNodeCode = edgeVO.getTargetNodeId();

            NodeJson sourceNodeJson = nodeJsonMap.get(sourceNodeCode);

            if (sourceNodeJson == null) {
                continue;
            }

            SkipJson skipJson = new SkipJson();
            skipJson.setNowNodeCode(sourceNodeCode);
            skipJson.setNextNodeCode(targetNodeCode);
            skipJson.setSkipName(edgeVO.getName());
            skipJson.setSkipType(edgeVO.getType());
            skipJson.setSkipCondition(edgeVO.getSkipCondition());
            skipJson.setCoordinate("0,0;0,0;");

            // 添加到源节点的跳过列表
            sourceNodeJson.getSkipList().add(skipJson);
        }

        return nodeJsonList;
    }
}