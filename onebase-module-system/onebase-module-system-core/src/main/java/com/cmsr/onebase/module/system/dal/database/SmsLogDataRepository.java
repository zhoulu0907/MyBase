package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.sms.SmsLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsLogDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * 短信日志数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class SmsLogDataRepository extends DataRepository<SmsLogDO> {

    public SmsLogDataRepository() {
        super(SmsLogDO.class);
    }

    /**
     * 分页查询短信日志
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SmsLogDO> findPage(SmsLogPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
