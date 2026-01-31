package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.api.vo.NodeInfoVO;
import com.cmsr.onebase.module.flow.api.vo.NodeTypeInfoVO;
import com.cmsr.onebase.module.flow.build.vo.ConnectorTypeListVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private FlowConnectorRepository flowConnectorRepository;

    @Override
    public PageResult<NodeConfigVO> pageNodeType(PageNodeConfigReqVO reqVO) {
        PageResult<FlowNodeConfigDO> dos = flowNodeConfigRepository.pageNodeConfigByCode(reqVO);
        List<NodeConfigVO> vos = BeanUtils.toBean(dos.getList(), NodeConfigVO.class);
        return new PageResult(vos, dos.getTotal());
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

    @Override
    public List<NodeInfoVO> getAllNodeTypes() {
        log.info("getAllNodeTypes start");

        // 1. Query all active node types (already sorted)
        List<FlowNodeConfigDO> nodeConfigs = flowNodeConfigRepository.listActiveNodeTypes();
        if (nodeConfigs == null || nodeConfigs.isEmpty()) {
            log.info("No active node types found");
            return Collections.emptyList();
        }

        // 2. Count connector instances for each type
        List<String> typeCodes = nodeConfigs.stream()
                .map(FlowNodeConfigDO::getNodeName)
                .collect(Collectors.toList());
        Map<String, Integer> instanceCountMap = flowConnectorRepository.countByTypeCodes(typeCodes);

        // 3. Build VO list
        List<NodeInfoVO> result = nodeConfigs.stream()
                .map(this::convertToNodeInfoVO)
                .peek(vo -> vo.setInstanceCount(instanceCountMap.getOrDefault(vo.getNodeName(), 0)))
                .collect(Collectors.toList());

        log.info("getAllNodeTypes success, count: {}", result.size());
        return result;
    }

    /**
     * Convert FlowNodeConfigDO to NodeInfoVO
     */
    private NodeInfoVO convertToNodeInfoVO(FlowNodeConfigDO config) {
        NodeInfoVO vo = new NodeInfoVO();
        vo.setNodeName(config.getNodeName());
        vo.setNodeCode(config.getNodeCode());
        vo.setLevel1Code(config.getLevel1Code());
        vo.setLevel2Code(config.getLevel2Code());
        vo.setLevel3Code(config.getLevel3Code());

        // Parse conn_config JSON with default values
        try {
            JsonNode connConfigJson = JsonUtils.parseTree(config.getConnConfig());
            vo.setVersion(getJsonValue(connConfigJson, "version", "1.0.0"));
            vo.setAuthType(getJsonValue(connConfigJson, "authType", "NONE"));
        } catch (Exception e) {
            log.warn("Failed to parse conn_config for nodeCode: {}, using defaults",
                    config.getNodeCode(), e);
            vo.setVersion("1.0.0");
            vo.setAuthType("NONE");
        }

        return vo;
    }

    /**
     * Get JSON field value with default
     */
    private String getJsonValue(JsonNode jsonNode, String fieldName, String defaultValue) {
        if (jsonNode == null || jsonNode.isNull()) {
            return defaultValue;
        }
        JsonNode field = jsonNode.get(fieldName);
        if (field == null || field.isNull()) {
            return defaultValue;
        }
        return field.asText();
    }

    @Override
    public NodeTypeInfoVO getNodeTypeInfo(String nodeCode) {
        log.info("getNodeTypeInfo start, nodeCode: {}", nodeCode);

        FlowNodeConfigDO nodeConfig = flowNodeConfigRepository.findByNodeCode(nodeCode);
        if (nodeConfig == null) {
            log.warn("Node config not found, nodeCode: {}", nodeCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXIST);
        }

        NodeTypeInfoVO result = convertToNodeTypeInfoVO(nodeConfig);
        log.info("getNodeTypeInfo success, nodeCode: {}, nodeName: {}, version: {}",
                nodeCode, result.getNodeName(), result.getVersion());
        return result;
    }

    /**
     * Convert FlowNodeConfigDO to NodeTypeInfoVO (with full Schema)
     */
    private NodeTypeInfoVO convertToNodeTypeInfoVO(FlowNodeConfigDO config) {
        NodeTypeInfoVO vo = new NodeTypeInfoVO();
        vo.setNodeCode(config.getNodeCode());
        vo.setNodeName(config.getNodeName());

        // Parse conn_config JSON with default values
        try {
            JsonNode connConfigJson = JsonUtils.parseTree(config.getConnConfig());
            vo.setVersion(getJsonValue(connConfigJson, "version", "1.0.0"));
            vo.setAuthType(getJsonValue(connConfigJson, "authType", "NONE"));
            vo.setConnConfig(connConfigJson);
        } catch (Exception e) {
            log.warn("Failed to parse conn_config for nodeCode: {}, using defaults",
                    config.getNodeCode(), e);
            vo.setVersion("1.0.0");
            vo.setAuthType("NONE");
            vo.setConnConfig(null);
        }

        // Parse action_config
        try {
            JsonNode actionConfigJson = JsonUtils.parseTree(config.getActionConfig());
            vo.setActionConfig(actionConfigJson);
        } catch (Exception e) {
            log.warn("Failed to parse action_config for nodeCode: {}",
                    config.getNodeCode(), e);
            vo.setActionConfig(null);
        }

        return vo;
    }

    @Override
    public JsonNode getActionSchemaTemplate(String typeCode) {
        log.info("getActionSchemaTemplate start, typeCode: {}", typeCode);

        // 1. 根据 typeCode 查询 flow_node_config
        FlowNodeConfigDO nodeConfig = flowNodeConfigRepository.findByNodeCode(typeCode);
        if (nodeConfig == null) {
            log.warn("Node config not found, typeCode: {}", typeCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXIST);
        }

        // 2. 校验 action_config 是否为空
        if (StringUtils.isBlank(nodeConfig.getActionConfig())) {
            log.warn("Action config is empty, typeCode: {}", typeCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_CONFIG_EMPTY);
        }

        // 3. 解析并返回 action_config JSON
        try {
            JsonNode template = JsonUtils.parseTree(nodeConfig.getActionConfig());
            log.info("getActionSchemaTemplate success, typeCode: {}", typeCode);
            return template;
        } catch (Exception e) {
            log.error("Failed to parse action_config JSON, typeCode: {}", typeCode, e);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
        }
    }

}
