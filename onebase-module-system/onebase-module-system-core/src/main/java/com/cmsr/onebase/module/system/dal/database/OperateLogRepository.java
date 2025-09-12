package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.cmsr.onebase.module.system.vo.operatelog.OperateLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.logger.OperateLogDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * 操作日志数据访问层
 *
 * 负责操作日志相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class OperateLogRepository extends DataRepository<OperateLogDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public OperateLogRepository() {
        super(OperateLogDO.class);
    }

    /**
     * 分页查询操作日志（管理端）
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<OperateLogDO> findPage(OperateLogPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getUserId() != null) {
            configs.and(Compare.EQUAL, OperateLogDO.USER_ID, reqVO.getUserId());
        }
        if (reqVO.getBizId() != null) {
            configs.and(Compare.EQUAL, OperateLogDO.BIZ_ID, reqVO.getBizId());
        }
        if (reqVO.getType() != null && !reqVO.getType().trim().isEmpty()) {
            configs.and(Compare.LIKE, OperateLogDO.TYPE, reqVO.getType());
        }
        if (reqVO.getSubType() != null && !reqVO.getSubType().trim().isEmpty()) {
            configs.and(Compare.LIKE, OperateLogDO.SUB_TYPE, reqVO.getSubType());
        }
        if (reqVO.getAction() != null && !reqVO.getAction().trim().isEmpty()) {
            configs.and(Compare.LIKE, OperateLogDO.ACTION, reqVO.getAction());
        }
        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            if (reqVO.getCreateTime()[0] != null) {
                configs.and(Compare.GREAT_EQUAL, OperateLogDO.CREATE_TIME, reqVO.getCreateTime()[0]);
            }
            if (reqVO.getCreateTime()[1] != null) {
                configs.and(Compare.LESS_EQUAL, OperateLogDO.CREATE_TIME, reqVO.getCreateTime()[1]);
            }
        }

        // 添加排序条件，按ID降序排列
        configs.order(OperateLogDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

    /**
     * 分页查询操作日志（API端）
     *
     * @param reqDTO 分页查询条件
     * @return 分页结果
     */
    public PageResult<OperateLogDO> findPage(OperateLogPageReqDTO reqDTO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqDTO.getType() != null && !reqDTO.getType().trim().isEmpty()) {
            configs.and(Compare.EQUAL, OperateLogDO.TYPE, reqDTO.getType());
        }
        if (reqDTO.getBizId() != null) {
            configs.and(Compare.EQUAL, OperateLogDO.BIZ_ID, reqDTO.getBizId());
        }
        if (reqDTO.getUserId() != null) {
            configs.and(Compare.EQUAL, OperateLogDO.USER_ID, reqDTO.getUserId());
        }

        // 添加排序条件，按ID降序排列
        configs.order(OperateLogDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findPageWithConditions(configs, reqDTO.getPageNo(), reqDTO.getPageSize());
    }
}
