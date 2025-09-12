package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailTemplateDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * 邮箱模版数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class MailTemplateDataRepository extends DataRepository<MailTemplateDO> {

    public MailTemplateDataRepository() {
        super(MailTemplateDO.class);
    }

    /**
     * 根据编码查找邮箱模版
     *
     * @param code 模版编码
     * @return 邮箱模版
     */
    public MailTemplateDO findOneByCode(String code) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, "code", code));
    }

    /**
     * 分页查询邮箱模版
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<MailTemplateDO> findPage(MailTemplatePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (null != pageReqVO.getStatus()) {
            configStore.and(Compare.EQUAL, "status", pageReqVO.getStatus());
        }
        if (StringUtils.isNotBlank(pageReqVO.getCode())) {
            configStore.and(Compare.LIKE, "code", pageReqVO.getCode());
        }
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            configStore.and(Compare.LIKE, "name", pageReqVO.getName());
        }
        if (null != pageReqVO.getAccountId()) {
            configStore.and(Compare.EQUAL, "account_id", pageReqVO.getAccountId());
        }
        if (null != pageReqVO.getCreateTime()) {
            configStore.and(Compare.EQUAL, "create_time", pageReqVO.getCreateTime());
        }

        configStore.order("id", "DESC");

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 根据账号ID统计模版数量
     *
     * @param accountId 账号ID
     * @return 模版数量
     */
    public long countByAccountId(Long accountId) {
        List<MailTemplateDO> list = findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, "account_id", accountId));
        return list.size();
    }
}
