package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsChannelDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 短信渠道数据访问层
 *
 * 负责短信渠道相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class SmsChannelRepository extends DataRepositoryNew<SmsChannelDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public SmsChannelRepository() {
        super(SmsChannelDO.class);
    }

    /**
     * 分页查询短信渠道
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<SmsChannelDO> findPage(SmsChannelPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getSignature() != null && !reqVO.getSignature().trim().isEmpty()) {
            configs.and(Compare.LIKE, SmsChannelDO.SIGNATURE, reqVO.getSignature());
        }
        if (reqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, SmsChannelDO.STATUS, reqVO.getStatus());
        }
        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            if (reqVO.getCreateTime()[0] != null) {
                configs.and(Compare.GREAT_EQUAL, SmsChannelDO.CREATE_TIME, reqVO.getCreateTime()[0]);
            }
            if (reqVO.getCreateTime()[1] != null) {
                configs.and(Compare.LESS_EQUAL, SmsChannelDO.CREATE_TIME, reqVO.getCreateTime()[1]);
            }
        }

        // 添加排序条件，按ID降序排列
        configs.order(SmsChannelDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

    /**
     * 查询所有短信渠道列表
     *
     * @return 短信渠道列表
     */
    public List<SmsChannelDO> findAllList() {
        return findAll();
    }
}
