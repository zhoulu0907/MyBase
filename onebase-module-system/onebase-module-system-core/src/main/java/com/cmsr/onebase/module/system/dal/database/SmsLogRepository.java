package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsLogDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemSmsLogMapper;
import com.cmsr.onebase.module.system.vo.sms.SmsLogPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 短信日志数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class SmsLogRepository extends BaseDataServiceImpl<SystemSmsLogMapper, SmsLogDO> {

    /**
     * 分页查询短信日志
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<SmsLogDO> findPage(SmsLogPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .eq(SmsLogDO.CHANNEL_ID, reqVO.getChannelId(), reqVO.getChannelId() != null)
                .eq(SmsLogDO.TEMPLATE_ID, reqVO.getTemplateId(), reqVO.getTemplateId() != null)
                .like(SmsLogDO.MOBILE, reqVO.getMobile(), StringUtils.isNotBlank(reqVO.getMobile()))
                .eq(SmsLogDO.SEND_STATUS, reqVO.getSendStatus(), reqVO.getSendStatus() != null)
                .eq(SmsLogDO.RECEIVE_STATUS, reqVO.getReceiveStatus(), reqVO.getReceiveStatus() != null)
                .orderBy(ID, false);

        if (reqVO.getSendTime() != null && reqVO.getSendTime().length == 2) {
            queryWrapper.ge(SmsLogDO.SEND_TIME, reqVO.getSendTime()[0], reqVO.getSendTime()[0] != null);
            queryWrapper.le(SmsLogDO.SEND_TIME, reqVO.getSendTime()[1], reqVO.getSendTime()[1] != null);
        }

        Page<SmsLogDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
