package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.mail.MailAccountPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailAccountDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;

/**
 * 邮箱账号数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class MailAccountDataRepository extends DataRepository<MailAccountDO> {

    public MailAccountDataRepository() {
        super(MailAccountDO.class);
    }

    /**
     * 分页查询邮箱账号
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<MailAccountDO> findPage(MailAccountPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        if (StringUtils.isNotBlank(pageReqVO.getMail())) {
            configStore.and(Compare.EQUAL, "mail", pageReqVO.getMail());
        }
        if (StringUtils.isNotBlank(pageReqVO.getUsername())) {
            configStore.and(Compare.EQUAL, "username", pageReqVO.getUsername());
        }
        
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
