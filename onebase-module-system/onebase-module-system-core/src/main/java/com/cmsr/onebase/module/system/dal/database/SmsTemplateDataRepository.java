package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsTemplateDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemSmsTemplateMapper;
import com.cmsr.onebase.module.system.vo.sms.SmsTemplatePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 短信模板数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class SmsTemplateDataRepository extends BaseDataServiceImpl<SystemSmsTemplateMapper, SmsTemplateDO> {

    /**
     * 根据编码查找短信模板
     *
     * @param code 模板编码
     * @return 短信模板
     */
    public SmsTemplateDO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getOne(query().eq(SmsTemplateDO.CODE, code));
    }

    /**
     * 分页查询短信模板
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SmsTemplateDO> findPage(SmsTemplatePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query().orderBy(ID, false);
        Page<SmsTemplateDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 根据渠道ID统计模板数量
     *
     * @param channelId 渠道ID
     * @return 模板数量
     */
    public Long countByChannelId(Long channelId) {
        if (channelId == null) {
            return 0L;
        }
        return count(query().eq(SmsTemplateDO.CHANNEL_ID, channelId));
    }
}
