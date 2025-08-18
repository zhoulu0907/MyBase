package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyTemplateDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;

/**
 * 站内信模版数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class NotifyTemplateDataRepository extends DataRepository<NotifyTemplateDO> {

    public NotifyTemplateDataRepository() {
        super(NotifyTemplateDO.class);
    }

    /**
     * 根据编码查找站内信模版
     *
     * @param code 模版编码
     * @return 站内信模版
     */
    public NotifyTemplateDO findOneByCode(String code) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, "code", code));
    }

    /**
     * 分页查询站内信模版
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<NotifyTemplateDO> findPage(NotifyTemplatePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (StringUtils.isNotBlank(pageReqVO.getCode())) {
            configStore.and(Compare.EQUAL, "code", pageReqVO.getCode());
        }
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            configStore.and(Compare.EQUAL, "name", pageReqVO.getName());
        }
        if (null != pageReqVO.getStatus()) {
            configStore.and(Compare.EQUAL, "status", pageReqVO.getStatus());
        }
        if (null != pageReqVO.getCreateTime()) {
            configStore.and(Compare.EQUAL, "create_time", pageReqVO.getCreateTime());
        }

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
