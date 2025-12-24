package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.cmsr.onebase.module.system.dal.dataobject.logger.OperateLogDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOperateLogMapper;
import com.cmsr.onebase.module.system.vo.log.OperateLogPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.framework.data.base.BaseDO.CREATE_TIME;
import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 操作日志数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class OperateLogRepository extends BaseDataRepository<SystemOperateLogMapper, OperateLogDO> {

    /**
     * 分页查询操作日志（管理端）
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<OperateLogDO> findPage(OperateLogPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .eq(OperateLogDO.USER_ID, reqVO.getUserId(), reqVO.getUserId() != null)
                .eq(OperateLogDO.BIZ_ID, reqVO.getBizId(), reqVO.getBizId() != null)
                .like(OperateLogDO.TYPE, reqVO.getType(), StringUtils.isNotBlank(reqVO.getType()))
                .like(OperateLogDO.SUB_TYPE, reqVO.getSubType(), StringUtils.isNotBlank(reqVO.getSubType()))
                .like(OperateLogDO.ACTION, reqVO.getAction(), StringUtils.isNotBlank(reqVO.getAction()))
                .orderBy(ID, false);

        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            queryWrapper.ge(CREATE_TIME, reqVO.getCreateTime()[0], reqVO.getCreateTime()[0] != null);
            queryWrapper.le(CREATE_TIME, reqVO.getCreateTime()[1], reqVO.getCreateTime()[1] != null);
        }

        Page<OperateLogDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 分页查询操作日志（API端）
     *
     * @param reqDTO 分页查询条件
     * @return 分页结果
     */
    public PageResult<OperateLogDO> findPage(OperateLogPageReqDTO reqDTO) {
        QueryWrapper queryWrapper = query()
                .eq(OperateLogDO.TYPE, reqDTO.getType(), StringUtils.isNotBlank(reqDTO.getType()))
                .eq(OperateLogDO.BIZ_ID, reqDTO.getBizId(), reqDTO.getBizId() != null)
                .eq(OperateLogDO.USER_ID, reqDTO.getUserId(), reqDTO.getUserId() != null)
                .orderBy(ID, false);

        Page<OperateLogDO> pageResult = page(Page.of(reqDTO.getPageNo(), reqDTO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
