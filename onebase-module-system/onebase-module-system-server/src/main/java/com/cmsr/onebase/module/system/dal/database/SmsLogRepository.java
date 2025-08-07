package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsLogDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * 短信日志数据访问层
 *
 * 负责短信日志相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class SmsLogRepository extends DataRepositoryNew<SmsLogDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public SmsLogRepository() {
        super(SmsLogDO.class);
    }

    /**
     * 分页查询短信日志
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<SmsLogDO> findPage(SmsLogPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getChannelId() != null) {
            configs.and(Compare.EQUAL, SmsLogDO.CHANNEL_ID, reqVO.getChannelId());
        }
        if (reqVO.getTemplateId() != null) {
            configs.and(Compare.EQUAL, SmsLogDO.TEMPLATE_ID, reqVO.getTemplateId());
        }
        if (reqVO.getMobile() != null && !reqVO.getMobile().trim().isEmpty()) {
            configs.and(Compare.LIKE, SmsLogDO.MOBILE, reqVO.getMobile());
        }
        if (reqVO.getSendStatus() != null) {
            configs.and(Compare.EQUAL, SmsLogDO.SEND_STATUS, reqVO.getSendStatus());
        }
        if (reqVO.getReceiveStatus() != null) {
            configs.and(Compare.EQUAL, SmsLogDO.RECEIVE_STATUS, reqVO.getReceiveStatus());
        }
        if (reqVO.getSendTime() != null && reqVO.getSendTime().length == 2) {
            if (reqVO.getSendTime()[0] != null) {
                configs.and(Compare.GREAT_EQUAL, SmsLogDO.SEND_TIME, reqVO.getSendTime()[0]);
            }
            if (reqVO.getSendTime()[1] != null) {
                configs.and(Compare.LESS_EQUAL, SmsLogDO.SEND_TIME, reqVO.getSendTime()[1]);
            }
        }

        // 添加排序条件，按ID降序排列
        configs.order(SmsLogDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }
}
