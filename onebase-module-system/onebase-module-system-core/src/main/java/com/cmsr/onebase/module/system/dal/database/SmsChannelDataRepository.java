package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsChannelDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemSmsChannelMapper;
import com.cmsr.onebase.module.system.vo.sms.SmsChannelPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 短信渠道数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class SmsChannelDataRepository extends BaseDataServiceImpl<SystemSmsChannelMapper, SmsChannelDO> {

    /**
     * 根据编码查找短信渠道
     *
     * @param code 渠道编码
     * @return 短信渠道
     */
    public SmsChannelDO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getOne(query().eq(SmsChannelDO.CODE, code));
    }

    /**
     * 根据状态查询短信渠道列表
     *
     * @param status 状态
     * @return 短信渠道列表
     */
    public List<SmsChannelDO> findListByStatus(Integer status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return list(query().eq(SmsChannelDO.STATUS, status));
    }

    /**
     * 分页查询短信渠道
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SmsChannelDO> findPage(SmsChannelPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query()
                .eq(SmsChannelDO.STATUS, pageReqVO.getStatus(), pageReqVO.getStatus() != null)
                .like(SmsChannelDO.SIGNATURE, pageReqVO.getSignature(), pageReqVO.getSignature() != null);

        Page<SmsChannelDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
