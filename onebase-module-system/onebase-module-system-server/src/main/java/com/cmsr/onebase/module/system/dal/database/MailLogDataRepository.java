package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.mail.vo.log.MailLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailLogDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;

/**
 * 邮件日志数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class MailLogDataRepository extends DataRepositoryNew<MailLogDO> {

    public MailLogDataRepository() {
        super(MailLogDO.class);
    }

    /**
     * 分页查询邮件日志
     *
     * @param pageVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<MailLogDO> findPage(MailLogPageReqVO pageVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        if (null != pageVO.getUserId()) {
            configStore.and(Compare.EQUAL, MailLogDO.USER_ID, pageVO.getUserId());
        }
        if (null != pageVO.getUserType()) {
            configStore.and(Compare.EQUAL, MailLogDO.USER_TYPE, pageVO.getUserType());
        }
        if (StringUtils.isNotBlank(pageVO.getToMail())) {
            configStore.and(Compare.LIKE, MailLogDO.TO_MAIL, pageVO.getToMail());
        }
        if (null != pageVO.getAccountId()) {
            configStore.and(Compare.EQUAL, MailLogDO.ACCOUNT_ID, pageVO.getAccountId());
        }
        if (null != pageVO.getTemplateId()) {
            configStore.and(Compare.EQUAL, MailLogDO.TEMPLATE_ID, pageVO.getTemplateId());
        }
        if (null != pageVO.getSendStatus()) {
            configStore.and(Compare.EQUAL, MailLogDO.SEND_STATUS, pageVO.getSendStatus());
        }
        if (null != pageVO.getSendTime()) {
            configStore.and(Compare.EQUAL, MailLogDO.SEND_TIME, pageVO.getSendTime());
        }

        return findPageWithConditions(configStore, pageVO.getPageNo(), pageVO.getPageSize());
    }
}
