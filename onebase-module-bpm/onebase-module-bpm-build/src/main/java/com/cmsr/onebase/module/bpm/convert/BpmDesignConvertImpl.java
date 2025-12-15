package com.cmsr.onebase.module.bpm.convert;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignRespVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignSaveReqVO;
import com.cmsr.onebase.module.bpm.build.vo.design.strategy.NodeVOStrategyManager;
import com.cmsr.onebase.module.bpm.core.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmGlobalConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.edge.EdgeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.VersionStatusEnum;
import com.cmsr.onebase.module.bpm.core.vo.design.BpmDefJsonVO;
import com.cmsr.onebase.module.bpm.core.vo.design.edge.base.BaseEdgeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.dto.SkipJson;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
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
    public BpmDesignRespVO toDesignRespVO(DefJson defJson) {
        if (defJson == null) {
            return null;
        }

        BpmDesignRespVO respVO = new BpmDesignRespVO();
        respVO.setFlowCode(defJson.getFlowCode());
        respVO.setFlowName(defJson.getFlowName());
        respVO.setId(defJson.getId());

        respVO.setBusinessUuid(defJson.getFormPath());
        respVO.setBpmVersion("V" + defJson.getVersion());

        Integer isPublish = defJson.getIsPublish();

        if (isPublish.equals(PublishStatus.PUBLISHED.getKey())) {
            respVO.setBpmVersionStatus(VersionStatusEnum.PUBLISHED.getCode());
        } else if (isPublish.equals(PublishStatus.UNPUBLISHED.getKey())) {
            respVO.setBpmVersionStatus(VersionStatusEnum.DESIGNING.getCode());
        } else if (isPublish.equals(PublishStatus.EXPIRED.getKey())) {
            respVO.setBpmVersionStatus(VersionStatusEnum.PREVIOUS.getCode());
        }

        String ext = defJson.getExt();

        // 转版本
        String defaultVersionAlias = "流程版本V" + defJson.getVersion();

        if (StringUtils.isNotBlank(ext)) {
            BpmDefinitionExtDTO extDto = JsonUtils.parseObject(ext, BpmDefinitionExtDTO.class);

            if (extDto != null) {
                if (extDto.getGlobalConfig() != null) {
                    respVO.setGlobalConfig(extDto.getGlobalConfig());
                } else {
                    // 返回默认值
                    respVO.setGlobalConfig(new BpmGlobalConfigDTO());
                }
            }
        }

        // 设置版本备注
        if (StringUtils.isBlank(defJson.getVersionAlias())) {
            respVO.setBpmVersionAlias(defaultVersionAlias);
        } else {
            respVO.setBpmVersionAlias(defJson.getVersionAlias());
        }

        // 转换节点配置
        respVO.setBpmDefJson(nodeJsonListToBpmDefJson(defJson.getNodeList()));

        return respVO;
    }

    /**
     * BpmDesignVO转换为DefJson
     *
     * @param flowDesignVO BpmDesignVO
     *
     * @return DefJson
     */
    @Override
    public DefJson toDefJson(BpmDesignSaveReqVO flowDesignVO) {
        if (flowDesignVO == null) {
           return null;
        }

        DefJson defJson = new DefJson();
        defJson.setId(flowDesignVO.getId());
        defJson.setFlowCode(flowDesignVO.getFlowCode());
        defJson.setFlowName(flowDesignVO.getFlowName());
        defJson.setFormPath(flowDesignVO.getBusinessUuid());
        defJson.setApplicationId(flowDesignVO.getAppId());

        // 构建ext
        BpmDefinitionExtDTO extDto = new BpmDefinitionExtDTO();
        extDto.setGlobalConfig(flowDesignVO.getGlobalConfig());

        defJson.setVersionAlias(flowDesignVO.getBpmVersionAlias());
        defJson.setExt(JsonUtils.toJsonString(extDto));

        // 固定值
        defJson.setModelValue("CLASSICS");
        defJson.setFormCustom("Y");

        // 转节点和边
        defJson.setNodeList(toNodeJsonList(flowDesignVO));

        return defJson;
    }

    /**
     * 复制通用字段数据，保持version不变
     *
     * @param destDefJson 目标流程定义JSON
     * @param sourceDefJson 源流程定义JSON
     */
    @Override
    public void copyCommonField(DefJson destDefJson, Definition definition) {
        if (destDefJson == null || definition == null) {
            return;
        }

        String sourceVersion = definition.getVersion();

        // 保持version不变
        destDefJson.setVersion(sourceVersion);

        // flowcode保持不变
        destDefJson.setFlowCode(definition.getFlowCode());
        destDefJson.setDefinitionUuid(definition.getDefinitionUuid());

        // 节点也使用相同的version
        for (NodeJson nodeJson : destDefJson.getNodeList()) {
            nodeJson.setVersion(sourceVersion);
        }

        // todo：确认是否还有其他通用字段
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
                BaseEdgeVO edgeVO = new BaseEdgeVO();

                if (StringUtils.isNotBlank(skipJson.getExt())) {
                    edgeVO.setData(JsonUtils.parseObject(skipJson.getExt(), EdgeExtDTO.class));
                }

                edgeVO.setSourceNodeId(skipJson.getNowNodeCode());
                edgeVO.setTargetNodeId(skipJson.getNextNodeCode());
                edgeVO.setName(skipJson.getSkipName());

                // 添加边视图到列表
                if (bpmDefJsonVO.getEdges() == null) {
                    bpmDefJsonVO.setEdges(new ArrayList<>());
                }

                bpmDefJsonVO.getEdges().add(edgeVO);
            }
        }

        return JsonUtils.toJsonString(bpmDefJsonVO);
    }

    private List<NodeJson> toNodeJsonList(BpmDesignSaveReqVO flowDesignVO) {
        if (flowDesignVO == null) {
            return null;
        }

        String bpmDefJson = flowDesignVO.getBpmDefJson();
        Long appId = flowDesignVO.getAppId();

        if (StringUtils.isBlank(bpmDefJson)) {
            return null;
        }

        BpmDefJsonVO bpmDefJsonVO = flowDesignVO.getBpmDefJsonVO();
        if (bpmDefJsonVO == null) {
            return null;
        }

        Map<String, NodeJson> nodeJsonMap = new HashMap<>();
        List<NodeJson> nodeJsonList = new ArrayList<>();

        for (BaseNodeVO nodeVO : bpmDefJsonVO.getNodes()) {
            NodeJson nodeJson = new NodeJson();
            nodeJson.setNodeCode(nodeVO.getId());
            nodeJson.setNodeName(nodeVO.getName());
            nodeJson.setFormPath(flowDesignVO.getBusinessUuid());

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
            strategyManager.fillNodeExtData(nodeJson, nodeVO, appId);

            // 添加到映射
            nodeJsonMap.put(nodeJson.getNodeCode(), nodeJson);
            nodeJsonList.add(nodeJson);
        }

        // 处理边
        for (BaseEdgeVO edgeVO : bpmDefJsonVO.getEdges()) {
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

            // 目前场景只有PASS
            skipJson.setSkipType(SkipType.PASS.getKey());

            // 处理条件分支
            if (edgeVO.getData() != null) {
                EdgeExtDTO edgeExtDTO = edgeVO.getData();
                skipJson.setExt(JsonUtils.toJsonString(edgeExtDTO));
                skipJson.setPriority(edgeExtDTO.getPriority());

                if (!edgeExtDTO.getIsDefault()) {
                    skipJson.setSkipCondition(JsonUtils.toJsonString(edgeExtDTO.getCondition()));
                }
            }

            skipJson.setCoordinate("0,0;0,0;");

            // 添加到源节点的跳过列表
            sourceNodeJson.getSkipList().add(skipJson);
        }

        return nodeJsonList;
    }
}