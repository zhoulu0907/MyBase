package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataflexdo.logger.ApiErrorLogDO;
import com.cmsr.onebase.module.infra.dal.mapper.logger.ApiErrorLogMapper;
import com.cmsr.onebase.module.infra.dal.vo.logger.apierrorlog.ApiErrorLogPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * API 错误日志数据访问层
 *
 * 负责API错误日志相关的数据操作，基于MyBatis-Flex实现
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class ApiErrorLogDataRepository extends ServiceImpl<ApiErrorLogMapper, ApiErrorLogDO> {

    /**
     * 分页查询API错误日志
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<ApiErrorLogDO> findPage(ApiErrorLogPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        queryWrapper.eq(ApiErrorLogDO.COLUMN_USER_ID, pageReqVO.getUserId())
                .eq(ApiErrorLogDO.COLUMN_USER_TYPE, pageReqVO.getUserType())
                .eq(ApiErrorLogDO.COLUMN_APPLICATION_NAME, pageReqVO.getApplicationName())
                .eq(ApiErrorLogDO.COLUMN_REQUEST_URL, pageReqVO.getRequestUrl())
                .eq(ApiErrorLogDO.COLUMN_PROCESS_STATUS, pageReqVO.getProcessStatus());

        if (pageReqVO.getExceptionTime() != null && pageReqVO.getExceptionTime().length == 2) {
            queryWrapper.ge(ApiErrorLogDO.COLUMN_EXCEPTION_TIME, pageReqVO.getExceptionTime()[0]);
            queryWrapper.le(ApiErrorLogDO.COLUMN_EXCEPTION_TIME, pageReqVO.getExceptionTime()[1]);
        }

        queryWrapper.orderBy(ApiErrorLogDO.COLUMN_EXCEPTION_TIME, false); // 按异常时间降序排列

        Page<ApiErrorLogDO> page = this.page(new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    /**
     * 更新错误日志处理状态
     *
     * @param id 日志ID
     * @param processStatus 处理状态
     * @param processUserId 处理用户ID
     * @param processTime 处理时间
     */
    public void updateProcessStatus(Long id, Integer processStatus, Long processUserId, LocalDateTime processTime) {
        ApiErrorLogDO logDO = new ApiErrorLogDO();
        logDO.setProcessStatus(processStatus);
        logDO.setProcessUserId(processUserId);
        logDO.setProcessTime(processTime);
        logDO.setId(id);
        this.updateById(logDO);
    }

    /**
     * 清理过期的错误日志
     *
     * @param expireDate 过期时间
     * @param deleteLimit 每次删除限制数量
     * @return 删除的数量
     */
    public int cleanExpiredLogs(LocalDateTime expireDate, Integer deleteLimit) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .le(ApiErrorLogDO.COLUMN_EXCEPTION_TIME, expireDate);

        return this.mapper.deleteByQuery(queryWrapper);
    }
}