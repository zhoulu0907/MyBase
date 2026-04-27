package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsLogDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemSmsLogMapper;
import com.cmsr.onebase.module.system.vo.sms.SmsLogPageReqVO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.stereotype.Repository;

/**
 * 短信日志数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class SmsLogDataRepository extends BaseDataRepository<SystemSmsLogMapper, SmsLogDO> {

    /**
     * 分页查询短信日志
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SmsLogDO> findPage(SmsLogPageReqVO pageReqVO) {
        Page<SmsLogDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), query());
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
