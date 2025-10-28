package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseEntity;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.springframework.stereotype.Repository;

/**
 * WarmFlow 流程实例 Repository
 *
 * @author liyang
 * @date 2025-10-10
 */
@Repository
public class FlowInstanceRepository extends DataRepository<FlowInstance> {

    public FlowInstanceRepository() {
        super(FlowInstance.class);
    }
    public PageResult<FlowInstance> findPage(BpmMyCreatedPageReqVO reqVO, Long userId) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "ext::json->>'appId'", reqVO.getAppId());//设置appId
        // 构建查询条件
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().trim().isEmpty()) {
            configs.and(Compare.LIKE, "ext::json->'processInfo'->>'processTitle'", reqVO.getProcessTitle());
        }
        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            configs.and(Compare.LIKE, "ext::json->'processInfo'->>'initiator'", reqVO.getInitiator());
        }
        if (reqVO.getFormSummary() != null && !reqVO.getFormSummary().isEmpty()) {
            configs.and(Compare.LIKE, "ext::json->'processInfo'->>'formSummary'", reqVO.getFormSummary());
        }
        configs.and(Compare.EQUAL, FlowInstance.CREATOR, userId);

        if (reqVO.getSubmitTime() != null && reqVO.getSubmitTime().length == 2){
            configs.and(Compare.BETWEEN, "(ext::json->'processInfo'->>'submitTime')::timestamp", reqVO.getSubmitTime()[0], reqVO.getSubmitTime()[1]);
        }
        if (reqVO.getFlowStatus() != null && !reqVO.getFlowStatus().isEmpty()){
            configs.and(Compare.EQUAL, "flowStatus", reqVO.getFlowStatus());
        }
        if("asc".equals(reqVO.getSortType())){
            configs.order(BaseEntity.CREATE_TIME, Order.TYPE.ASC);
        }else{
            configs.order(BaseEntity.CREATE_TIME, Order.TYPE.DESC);
        }
        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

}