package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import com.cmsr.onebase.module.infra.dal.vo.logger.apiaccesslog.ApiAccessLogPageReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * API 访问日志数据访问层
 *
 * 负责API访问日志相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class ApiAccessLogDataRepositoryOld extends DataRepository<ApiAccessLogDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public ApiAccessLogDataRepositoryOld() {
        super(ApiAccessLogDO.class);
    }

    /**
     * 分页查询API访问日志
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<ApiAccessLogDO> findPage(ApiAccessLogPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(ApiAccessLogDO.COLUMN_USER_ID, pageReqVO.getUserId())
                .eq(ApiAccessLogDO.COLUMN_USER_TYPE, pageReqVO.getUserType())
                .eq(ApiAccessLogDO.COLUMN_APPLICATION_NAME, pageReqVO.getApplicationName())
                .like(ApiAccessLogDO.COLUMN_REQUEST_URL, pageReqVO.getRequestUrl())
                .eq(ApiAccessLogDO.COLUMN_DURATION, pageReqVO.getDuration())
                .eq(ApiAccessLogDO.COLUMN_RESULT_CODE, pageReqVO.getResultCode());

        if (pageReqVO.getBeginTime() != null && pageReqVO.getBeginTime().length == 2) {
            configStore.ge(ApiAccessLogDO.CREATE_TIME, pageReqVO.getBeginTime()[0]);
            configStore.le(ApiAccessLogDO.CREATE_TIME, pageReqVO.getBeginTime()[1]);
        }

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 清理过期的访问日志
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
                    .le(ApiAccessLogDO.CREATE_TIME, expireDate).limit(deleteLimit));
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
