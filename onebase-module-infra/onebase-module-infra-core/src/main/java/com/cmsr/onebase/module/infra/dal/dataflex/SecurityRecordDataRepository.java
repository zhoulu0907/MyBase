package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity.SecurityRecordDO;
import com.cmsr.onebase.module.infra.dal.mapper.ssecurity.SecurityRecordMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全记录数据访问层
 *
 * @author matianyu
 * @date 2025-11-12
 */
@Repository
public class SecurityRecordDataRepository extends ServiceImpl<SecurityRecordMapper, SecurityRecordDO> {

    /**
     * 根据租户ID、用户ID、记录类型查询历史记录（按创建时间倒序）
     *
     * @param userId     用户ID
     * @param recordType 记录类型
     * @param limit      查询数量限制
     * @return 历史记录列表
     */
    public List<SecurityRecordDO> findByTenantUserType(Long userId, String recordType, int limit) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(SecurityRecordDO.USER_ID, userId);
        queryWrapper.eq(SecurityRecordDO.RECORD_TYPE, recordType);
        queryWrapper.orderBy(BaseDO.CREATE_TIME, false);
        queryWrapper.limit(limit);
        return list(queryWrapper);
    }

    /**
     * 查询用户最近一次密码记录
     *
     * @param userId     用户ID
     * @param recordType 记录类型
     * @return 最近一次密码记录，如果不存在则返回null
     */
    public SecurityRecordDO findLatestByUserIdAndType(Long userId, String recordType) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(SecurityRecordDO.USER_ID, userId);
        queryWrapper.eq(SecurityRecordDO.RECORD_TYPE, recordType);
        queryWrapper.orderBy(BaseDO.CREATE_TIME, false);
        queryWrapper.limit(1);
        return getOne(queryWrapper);
    }

}