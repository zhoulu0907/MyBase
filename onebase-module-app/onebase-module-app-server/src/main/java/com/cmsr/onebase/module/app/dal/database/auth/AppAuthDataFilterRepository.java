package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthDataFilterDO;
import org.springframework.stereotype.Repository;

/**
 * 数据权限配置-数据过滤条件数据访问类
 *
 * @author lingma
 * @date 2025-07-25
 */
@Repository
public class AppAuthDataFilterRepository extends DataRepository {

    public AppAuthDataFilterRepository() {
        super(AuthDataFilterDO.class);
    }

}