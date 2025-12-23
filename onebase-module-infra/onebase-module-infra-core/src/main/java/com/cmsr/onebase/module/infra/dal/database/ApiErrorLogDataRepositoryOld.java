package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiErrorLogDO;
import com.cmsr.onebase.module.infra.dal.vo.logger.apierrorlog.ApiErrorLogPageReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * API 错误日志数据访问层
 *
 * 负责API错误日志相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class ApiErrorLogDataRepositoryOld extends DataRepository<ApiErrorLogDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public ApiErrorLogDataRepositoryOld() {
        super(ApiErrorLogDO.class);
    }

    /**
     * 分页查询API错误日志
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<ApiErrorLogDO> findPage(ApiErrorLogPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(ApiErrorLogDO.COLUMN_USER_ID, pageReqVO.getUserId())
                .eq(ApiErrorLogDO.COLUMN_USER_TYPE, pageReqVO.getUserType())
                .eq(ApiErrorLogDO.COLUMN_APPLICATION_NAME, pageReqVO.getApplicationName())
                .eq(ApiErrorLogDO.COLUMN_REQUEST_URL, pageReqVO.getRequestUrl())
                .eq(ApiErrorLogDO.COLUMN_PROCESS_STATUS, pageReqVO.getProcessStatus())
                .eq(ApiErrorLogDO.COLUMN_EXCEPTION_TIME, pageReqVO.getExceptionTime());

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
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
        ApiErrorLogDO logDO = ApiErrorLogDO.builder()
                .processStatus(processStatus)
                .processUserId(processUserId)
                .processTime(processTime)
                .build();
        logDO.setId(id);
        update(logDO);
    }

    /**
     * 清理过期的错误日志
     *
     * @param expireDate 过期时间
     * @param deleteLimit 每次删除限制数量
     * @return 删除的数量
     */
    public int cleanExpiredLogs(LocalDateTime expireDate, Integer deleteLimit) {
        int count = 0;
        // 循环删除，直到没有满足条件的数据
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = (int) deleteByConfigReturn(new DefaultConfigStore()
                    .le(ApiErrorLogDO.CREATE_TIME, expireDate).limit(deleteLimit));
            count += deleteCount;
            // 达到删除预期条数，说明到底了
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

    /**
     * 根据条件删除并返回删除数量
     *
     * @param configs 删除条件
     * @return 删除的记录数
     */
    private long deleteByConfigReturn(ConfigStore configs) {
        return deleteByConfig(configs);
    }
}
