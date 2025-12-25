package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigActionVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigConnVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;

/**
 * @Author：huangjie
 * @Date：2025/11/17 17:25
 */
public interface FlowNodeConfigService {

    PageResult<NodeConfigVO> pageNodeType(PageNodeConfigReqVO reqVO);

    NodeConfigConnVO findConnConfig(String nodeCode);

    NodeConfigActionVO findActionConfig(String nodeCode);
}
