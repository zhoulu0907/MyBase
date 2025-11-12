package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.vo.PageFlowProcessReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        if (reqVO.getEnableStatus() != null) {
            configs.eq("enable_status", reqVO.getEnableStatus());
        }
        if (StringUtils.isNotEmpty(reqVO.getTriggerType())) {
            configs.eq("trigger_type", reqVO.getTriggerType());
        }
        configs.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return this.findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

    public List<FlowProcessDO> findAllByEnableStatus(Integer status) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.eq("enable_status", status);
        return findAllByConfig(configs);
    }

    public List<FlowProcessDO> findByApplicationId(Long applicationId) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        return findAllByConfig(configs);
    }

    public List<FlowProcessDO> findByApplicationIdAndEnableStatus(Long applicationId, Integer status) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("enable_status", status);
        return findAllByConfig(configs);
    }

    public String findProcessNameById(Long id) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.columns("process_name");
        configs.eq("id", id);
        FlowProcessDO process = findOne(configs);
        if (process != null) {
            return process.getProcessName();
        }
        return null;
    }

}
