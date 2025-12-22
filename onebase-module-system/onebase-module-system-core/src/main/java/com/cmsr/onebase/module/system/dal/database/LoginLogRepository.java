package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.logger.LoginLogDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemLoginLogMapper;
import com.cmsr.onebase.module.system.vo.log.LoginLogPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.framework.data.base.BaseDO.CREATE_TIME;
import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 登录日志数据访问层
 *
 * 负责登录日志相关的数据操作。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class LoginLogRepository extends BaseDataServiceImpl<SystemLoginLogMapper, LoginLogDO> {

    /**
     * 分页查询登录日志
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<LoginLogDO> findPage(LoginLogPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .like(LoginLogDO.USER_IP, reqVO.getUserIp(), StringUtils.isNotBlank(reqVO.getUserIp()))
                .like(LoginLogDO.USERNAME, reqVO.getUsername(), StringUtils.isNotBlank(reqVO.getUsername()))
                .eq(LoginLogDO.RESULT, reqVO.getStatus(), reqVO.getStatus() != null)
                .orderBy(ID, false);

        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            queryWrapper.ge(CREATE_TIME, reqVO.getCreateTime()[0], reqVO.getCreateTime()[0] != null);
            queryWrapper.le(CREATE_TIME, reqVO.getCreateTime()[1], reqVO.getCreateTime()[1] != null);
        }

        Page<LoginLogDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
