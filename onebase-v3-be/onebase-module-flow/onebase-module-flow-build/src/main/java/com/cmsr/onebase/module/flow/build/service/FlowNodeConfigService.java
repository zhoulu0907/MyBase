package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.api.vo.NodeInfoVO;
import com.cmsr.onebase.module.flow.api.vo.NodeTypeInfoVO;
import com.cmsr.onebase.module.flow.build.vo.ConnectorTypeListVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/17 17:25
 */
public interface FlowNodeConfigService {

    PageResult<NodeConfigVO> pageNodeType(PageNodeConfigReqVO reqVO);

    /**
     * Get all active connector types
     */
    List<ConnectorTypeListVO> getAllConnectorTypes();

    /**
     * Get all node types with instance count
     */
    List<NodeInfoVO> getAllNodeTypes();

    /**
     * Get connector type info by node code (with full Schema)
     */
    NodeTypeInfoVO getNodeTypeInfo(String nodeCode);

    /**
     * 查询连接器类型动作配置模板
     * <p>
     * 从 flow_node_config.action_config 获取 Formily Schema 模板
     *
     * @param typeCode 连接器类型编码（对应 nodeCode）
     * @return 动作配置模板 JSON
     */
    JsonNode getActionSchemaTemplate(String typeCode);
}
