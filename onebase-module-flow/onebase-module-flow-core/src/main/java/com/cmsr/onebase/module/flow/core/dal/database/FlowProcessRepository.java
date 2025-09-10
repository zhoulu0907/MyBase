package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.vo.mgmt.PageFlowProcessReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:35
 */
@Repository
public class FlowProcessRepository extends DataRepository<FlowProcessDO> {

    public FlowProcessRepository() {
        super(FlowProcessDO.class);
    }

    public PageResult<FlowProcessDO> findPageByQuery(PageFlowProcessReqVO reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        if (reqVO.getApplicationId() != null) {
            configs.eq("application_id", reqVO.getApplicationId());
        }
        if (StringUtils.isNotEmpty(reqVO.getProcessName())) {
            configs.like("process_name", reqVO.getProcessName());
        }
        if (reqVO.getProcessStatus() != null) {
            configs.eq("process_status", reqVO.getProcessStatus());
        }
        if (StringUtils.isNotEmpty(reqVO.getTriggerType())) {
            configs.eq("trigger_type", reqVO.getTriggerType());
        }
        configs.order(BaseDO.UPDATE_TIME, Order.TYPE.DESC);
        return this.findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }
}
