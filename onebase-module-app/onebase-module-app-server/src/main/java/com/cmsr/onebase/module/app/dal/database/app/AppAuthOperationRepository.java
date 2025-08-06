package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.AuthOperationDO;
import org.springframework.stereotype.Repository;

/**
 * 应用权限操作数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthOperationRepository extends DataRepository {

    public AppAuthOperationRepository() {
        super(AuthOperationDO.class);
    }
}