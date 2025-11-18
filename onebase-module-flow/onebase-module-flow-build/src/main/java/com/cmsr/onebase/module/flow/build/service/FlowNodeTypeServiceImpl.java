package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.build.vo.NodeTypeVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeTypeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeTypeDO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeTypeReqVO;
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
public class FlowNodeTypeServiceImpl implements FlowNodeTypeService {

    @Autowired
    private FlowNodeTypeRepository flowNodeTypeRepository;

    @Override
    public PageResult<NodeTypeVO> pageNodeType(PageNodeTypeReqVO reqVO) {
        PageResult<FlowNodeTypeDO> dos = flowNodeTypeRepository.pageNodeTypeByCode(reqVO);
        List<NodeTypeVO> vos = BeanUtils.toBean(dos.getList(), NodeTypeVO.class);
        return new PageResult(vos, dos.getTotal());
    }

}
