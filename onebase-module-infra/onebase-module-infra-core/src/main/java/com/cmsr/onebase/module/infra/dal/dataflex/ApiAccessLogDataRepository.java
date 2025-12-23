package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataflexdo.logger.ApiAccessLogDO;
import com.cmsr.onebase.module.infra.dal.mapper.logger.ApiAccessLogMapper;
import com.cmsr.onebase.module.infra.dal.vo.logger.apiaccesslog.ApiAccessLogPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * API 访问日志数据访问层
 *
 * 负责API访问日志相关的数据操作，基于MyBatis-Flex实现
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class ApiAccessLogDataRepository extends ServiceImpl<ApiAccessLogMapper, ApiAccessLogDO> {

    /**
     * 分页查询API访问日志
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<ApiAccessLogDO> findPage(ApiAccessLogPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        queryWrapper.eq(ApiAccessLogDO.COLUMN_USER_ID, pageReqVO.getUserId())
                .eq(ApiAccessLogDO.COLUMN_USER_TYPE, pageReqVO.getUserType())
                .eq(ApiAccessLogDO.COLUMN_APPLICATION_NAME, pageReqVO.getApplicationName())
                .like(ApiAccessLogDO.COLUMN_REQUEST_URL, pageReqVO.getRequestUrl())
                .eq(ApiAccessLogDO.COLUMN_DURATION, pageReqVO.getDuration())
                .eq(ApiAccessLogDO.COLUMN_RESULT_CODE, pageReqVO.getResultCode());

        if (pageReqVO.getBeginTime() != null && pageReqVO.getBeginTime().length == 2) {
            queryWrapper.ge(ApiAccessLogDO.COLUMN_BEGIN_TIME, pageReqVO.getBeginTime()[0]);
            queryWrapper.le(ApiAccessLogDO.COLUMN_END_TIME, pageReqVO.getBeginTime()[1]);
        }

        queryWrapper.orderBy(ApiAccessLogDO.COLUMN_BEGIN_TIME, false); // 按开始时间降序排列

        Page<ApiAccessLogDO> page = this.page(new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    /**
     * 清理过期的访问日志
     *
     * @param expireDate 过期时间
     * @param deleteLimit 每次删除限制数量
     * @return 删除的数量
     */
    public int cleanExpiredLogs(LocalDateTime expireDate, Integer deleteLimit) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .le(ApiAccessLogDO.COLUMN_BEGIN_TIME, expireDate);

        return this.mapper.deleteByQuery(queryWrapper);
    }
}
