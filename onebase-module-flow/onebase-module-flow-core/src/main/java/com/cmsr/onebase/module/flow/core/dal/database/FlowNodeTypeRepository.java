package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeTypeDO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeTypeReqVO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FlowNodeTypeRepository extends DataRepository<FlowNodeTypeDO> {

    public FlowNodeTypeRepository() {
        super(FlowNodeTypeDO.class);
    }

    public PageResult<FlowNodeTypeDO> pageNodeTypeByCode(PageNodeTypeReqVO reqVO) {
        ConfigStore configStore = new DefaultConfigStore();
        if (reqVO.getLevel1Code() != null) {
            configStore.like("level1_code", reqVO.getLevel1Code());
        }
        if (reqVO.getLevel2Code() != null) {
            configStore.like("level2_code", reqVO.getLevel2Code());
        }
        if (reqVO.getLevel3Code() != null) {
            configStore.like("level3_code", reqVO.getLevel3Code());
        }
        if (reqVO.getTypeName() != null) {
            configStore.like("type_name", reqVO.getTypeName());
        }
        configStore.eq("active_status", 1);
        configStore.order("sort_order", Order.TYPE.ASC);
        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }

}
