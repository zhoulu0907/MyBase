package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.PageUtils;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowNodeConfigMapper;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowNodeConfigTableDef.FLOW_NODE_CONFIG;

@Slf4j
@Repository
public class FlowNodeConfigRepository extends ServiceImpl<FlowNodeConfigMapper, FlowNodeConfigDO> {

    public PageResult<FlowNodeConfigDO> pageNodeConfigByCode(PageNodeConfigReqVO reqVO) {
        String level1Code = reqVO.getLevel1Code();
        String level2Code = reqVO.getLevel2Code();
        String level3Code = reqVO.getLevel3Code();
        String nodeName = reqVO.getNodeName();
        QueryWrapper queryWrapper = this.query()
                .where(FLOW_NODE_CONFIG.LEVEL1_CODE.eq(level1Code).when(StringUtils.isNotBlank(level1Code) && !StringUtils.equals("all", level1Code)))
                .where(FLOW_NODE_CONFIG.LEVEL2_CODE.eq(level2Code).when(StringUtils.isNotBlank(level2Code) && !StringUtils.equals("all", level2Code)))
                .where(FLOW_NODE_CONFIG.LEVEL3_CODE.eq(level3Code).when(StringUtils.isNotBlank(level3Code) && !StringUtils.equals("all", level3Code)))
                .where(FLOW_NODE_CONFIG.NODE_NAME.like(nodeName).when(StringUtils.isNotBlank(nodeName)))
                .where(FLOW_NODE_CONFIG.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_NODE_CONFIG.SORT_ORDER, true)
                .orderBy(FLOW_NODE_CONFIG.NODE_NAME, true);
        Page<FlowNodeConfigDO> pageData = this.page(PageUtils.toFlexPage(reqVO), queryWrapper);
        return PageUtils.toPageResult(pageData);
    }

    public FlowNodeConfigDO findByNodeCode(String nodeCode) {
        QueryWrapper queryWrapper = this.query()
                .where(FLOW_NODE_CONFIG.NODE_CODE.eq(nodeCode))
                .where(FLOW_NODE_CONFIG.ACTIVE_STATUS.eq(1));
        return this.getOne(queryWrapper);
    }
}
