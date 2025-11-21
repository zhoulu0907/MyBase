package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeTypeDO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeTypeReqVO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FlowNodeTypeRepository extends DataRepository<FlowNodeTypeDO> {

    public FlowNodeTypeRepository() {
        super(FlowNodeTypeDO.class);
    }

    public PageResult<FlowNodeTypeDO> pageNodeTypeByCode(PageNodeTypeReqVO reqVO) {
        ConfigStore configStore = new DefaultConfigStore();
        String level1Code = reqVO.getLevel1Code();
        if (StringUtils.isNotBlank(level1Code) && !StringUtils.equals("all", level1Code)) {
            configStore.like("level1_code", level1Code);
        }
        String level2Code = reqVO.getLevel2Code();
        if (StringUtils.isNotBlank(level2Code) && !StringUtils.equals("all", level2Code)) {
            configStore.like("level2_code", level2Code);
        }
        String level3Code = reqVO.getLevel3Code();
        if (StringUtils.isNotBlank(level3Code) && !StringUtils.equals("all", level3Code)) {
            configStore.like("level3_code", level3Code);
        }
        if (reqVO.getTypeName() != null) {
            configStore.like("type_name", reqVO.getTypeName());
        }
        configStore.eq("active_status", 1);
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("type_code", Order.TYPE.ASC);
        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }

}
