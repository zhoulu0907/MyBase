package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeCategoryDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowNodeCategoryMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowNodeCategoryTableDef.FLOW_NODE_CATEGORY;

@Slf4j
@Repository
public class FlowNodeCategoryRepository extends ServiceImpl<FlowNodeCategoryMapper, FlowNodeCategoryDO> {

    public List<FlowNodeCategoryDO> findAllCategories() {
        QueryWrapper queryWrapper = this.query().orderBy(FLOW_NODE_CATEGORY.SORT_ORDER, true);
        return list(queryWrapper);
    }

}
