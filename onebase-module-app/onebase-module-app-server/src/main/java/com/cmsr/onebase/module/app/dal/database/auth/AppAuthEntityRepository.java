package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthEntityDO;
import org.springframework.stereotype.Repository;

/**
 * 应用权限实体数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthEntityRepository extends DataRepository {

    public AppAuthEntityRepository() {
        super(AuthEntityDO.class);
    }
}