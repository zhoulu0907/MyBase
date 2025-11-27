package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeTypeDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowNodeTypeMapper;
import com.cmsr.onebase.module.flow.core.vo.PageNodeTypeReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowNodeTypeTableDef.FLOW_NODE_TYPE;

@Slf4j
@Repository
public class FlowNodeTypeRepository extends ServiceImpl<FlowNodeTypeMapper, FlowNodeTypeDO> {

    public PageResult<FlowNodeTypeDO> pageNodeTypeByCode(PageNodeTypeReqVO reqVO) {
        String level1Code = reqVO.getLevel1Code();
        String level2Code = reqVO.getLevel2Code();
        String level3Code = reqVO.getLevel3Code();
        String typeName = reqVO.getTypeName();
        QueryWrapper queryWrapper = this.query()
                .where(FLOW_NODE_TYPE.LEVEL1_CODE.eq(level1Code).when(StringUtils.isNotBlank(level1Code) && !StringUtils.equals("all", level1Code)))
                .where(FLOW_NODE_TYPE.LEVEL2_CODE.eq(level2Code).when(StringUtils.isNotBlank(level2Code) && !StringUtils.equals("all", level2Code)))
                .where(FLOW_NODE_TYPE.LEVEL3_CODE.eq(level3Code).when(StringUtils.isNotBlank(level3Code) && !StringUtils.equals("all", level3Code)))
                .where(FLOW_NODE_TYPE.TYPE_NAME.like(typeName).when(StringUtils.isNotBlank(typeName)))
                .where(FLOW_NODE_TYPE.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_NODE_TYPE.SORT_ORDER, true)
                .orderBy(FLOW_NODE_TYPE.TYPE_CODE, true);
        Page<FlowNodeTypeDO> page = new Page(reqVO.getPageNo(), reqVO.getPageSize());
        Page<FlowNodeTypeDO> pageData = this.page(page, queryWrapper);
        return new PageResult<>(pageData.getRecords(), pageData.getTotalRow());
    }

}
