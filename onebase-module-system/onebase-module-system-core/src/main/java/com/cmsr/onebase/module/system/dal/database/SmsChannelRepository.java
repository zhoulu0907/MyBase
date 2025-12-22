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

import java.util.List;

import static com.cmsr.onebase.framework.data.base.BaseDO.CREATE_TIME;
import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 短信渠道数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class SmsChannelRepository extends BaseDataServiceImpl<SystemSmsChannelMapper, SmsChannelDO> {

    /**
     * 分页查询短信渠道
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<SmsChannelDO> findPage(SmsChannelPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .like(SmsChannelDO.SIGNATURE, reqVO.getSignature(), StringUtils.isNotBlank(reqVO.getSignature()))
                .eq(SmsChannelDO.STATUS, reqVO.getStatus(), reqVO.getStatus() != null)
                .orderBy(ID, false);

        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            queryWrapper.ge(CREATE_TIME, reqVO.getCreateTime()[0], reqVO.getCreateTime()[0] != null);
            queryWrapper.le(CREATE_TIME, reqVO.getCreateTime()[1], reqVO.getCreateTime()[1] != null);
        }

        Page<SmsChannelDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 查询所有短信渠道列表
     *
     * @return 短信渠道列表
     */
    public List<SmsChannelDO> findAllList() {
        return list();
    }
}
