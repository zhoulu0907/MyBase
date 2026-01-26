package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.api.vo.NodeInfoVO;
import com.cmsr.onebase.module.flow.api.vo.NodeTypeInfoVO;
import com.cmsr.onebase.module.flow.build.vo.ConnectorTypeListVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;

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
}
