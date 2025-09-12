package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.sms.SmsTemplatePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsTemplateDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 短信模板数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class SmsTemplateDataRepository extends DataRepository<SmsTemplateDO> {

    public SmsTemplateDataRepository() {
        super(SmsTemplateDO.class);
    }

    /**
     * 根据编码查找短信模板
     *
     * @param code 模板编码
     * @return 短信模板
     */
    public SmsTemplateDO findOneByCode(String code) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, SmsTemplateDO.CODE, code));
    }

    /**
     * 分页查询短信模板
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SmsTemplateDO> findPage(SmsTemplatePageReqVO pageReqVO) {
        return findPageWithConditions(new DefaultConfigStore()
                .order("id", Order.TYPE.DESC), pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 根据渠道ID统计模板数量
     *
     * @param channelId 渠道ID
     * @return 模板数量
     */
    public Long countByChannelId(Long channelId) {
        List<SmsTemplateDO> list = findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, "channel_id", channelId));
        return (long) list.size();
    }
}
