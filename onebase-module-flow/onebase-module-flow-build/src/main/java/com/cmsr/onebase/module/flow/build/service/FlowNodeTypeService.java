package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.NodeTypeVO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeTypeReqVO;

/**
 * @Author：huangjie
 * @Date：2025/11/17 17:25
 */
public interface FlowNodeTypeService {

    PageResult<NodeTypeVO> pageNodeType(PageNodeTypeReqVO reqVO);

}
