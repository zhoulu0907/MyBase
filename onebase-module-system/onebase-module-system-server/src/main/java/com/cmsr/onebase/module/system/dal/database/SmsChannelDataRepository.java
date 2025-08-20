package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
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
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class SmsChannelDataRepository extends DataRepository<SmsChannelDO> {

    public SmsChannelDataRepository() {
        super(SmsChannelDO.class);
    }

    /**
     * 根据编码查找短信渠道
     *
     * @param code 渠道编码
     * @return 短信渠道
     */
    public SmsChannelDO findOneByCode(String code) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, "code", code));
    }

    /**
     * 根据状态查询短信渠道列表
     *
     * @param status 状态
     * @return 短信渠道列表
     */
    public List<SmsChannelDO> findListByStatus(Integer status) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, "status", status));
    }

    /**
     * 分页查询短信渠道
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SmsChannelDO> findPage(SmsChannelPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        if (pageReqVO.getStatus() != null) {
            configStore.and(Compare.EQUAL, "status", pageReqVO.getStatus());
        }
        if (pageReqVO.getSignature() != null) {
            configStore.and(Compare.LIKE, "signature", pageReqVO.getSignature());
        }
        
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
