package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.controller.admin.auth.dto.AuthPermissionDTO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthPermissionDO;
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
public class AppAuthPermissionRepository extends DataRepository<AuthPermissionDO> {

    public AppAuthPermissionRepository() {
        super(AuthPermissionDO.class);
    }

    public AuthPermissionDO findByQuery(AuthPermissionDTO dto) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", dto.getApplicationId());
        configs.eq("role_id", dto.getRoleId());
        configs.eq("menu_id", dto.getMenuId());
        return this.findOne(configs);
    }
}