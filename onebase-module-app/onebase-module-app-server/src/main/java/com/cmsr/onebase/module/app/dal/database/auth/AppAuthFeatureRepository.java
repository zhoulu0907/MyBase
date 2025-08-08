package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionReqVO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthFeatureDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * 应用权限功能数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthFeatureRepository extends DataRepository {

    public AppAuthFeatureRepository() {
        super(AuthFeatureDO.class);
    }

    public AuthFeatureDO findByQuery(AuthPermissionReqVO reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        return this.findOne(AuthFeatureDO.class, configs);
    }
}