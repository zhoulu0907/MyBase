package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import org.springframework.stereotype.Repository;

/**
 * 管理员认证数据访问层
 *
 * 负责管理员用户相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class AdminAuthRepository extends DataRepositoryNew<AdminUserDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public AdminAuthRepository() {
        super(AdminUserDO.class);
    }
    // 如有特殊查询可在此补充
}

