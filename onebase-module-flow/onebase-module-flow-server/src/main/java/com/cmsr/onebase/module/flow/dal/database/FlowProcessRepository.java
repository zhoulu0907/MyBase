package com.cmsr.onebase.module.flow.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.ListFlowProcessReqVO;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:35
 */
public class FlowProcessRepository extends DataRepository<FlowProcessDO> {

    public FlowProcessRepository() {
        super(FlowProcessDO.class);
    }

    public PageResult<FlowProcessDO> findPageByQuery(ListFlowProcessReqVO reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        if (reqVO.getApplicationId() != null) {
            configs.eq("application_id", reqVO.getApplicationId());
        }
        if (reqVO.getProcessName() != null && !reqVO.getProcessName().isEmpty()) {
            configs.like("process_name", "%" + reqVO.getProcessName() + "%");
        }
        if (reqVO.getProcessStatus() != null) {
            configs.eq("process_status", reqVO.getProcessStatus());
        }
        if (reqVO.getTriggerType() != null && !reqVO.getTriggerType().isEmpty()) {
            configs.eq("trigger_type", reqVO.getTriggerType());
        }

        return this.findPageWithConditions(configs, reqVO.getPageNum(), reqVO.getPageSize());
    }
}
