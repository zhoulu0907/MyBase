package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityRecordDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全记录数据访问层
 *
 * @author matianyu
 * @date 2025-11-12
 */
@Repository
public class SecurityRecordDataRepository extends DataRepository<SecurityRecordDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public SecurityRecordDataRepository() {
        super(SecurityRecordDO.class);
    }

    /**
     * 根据租户ID、用户ID、记录类型查询历史记录（按创建时间倒序）
     *
     * @param userId     用户ID
     * @param recordType 记录类型
     * @param limit      查询数量限制
     * @return 历史记录列表
     */
    public List<SecurityRecordDO> findByTenantUserType(Long userId, String recordType, int limit) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(SecurityRecordDO.USER_ID, userId);
        configStore.eq(SecurityRecordDO.RECORD_TYPE, recordType);
        configStore.order(BaseDO.CREATE_TIME, "DESC");
        configStore.limit(limit);
        return findAllByConfig(configStore);
    }

}
