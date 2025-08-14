package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.controller.admin.auth.dto.AuthPermissionDTO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthFieldDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用权限字段数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthFieldRepository extends DataRepositoryNew<AuthFieldDO> {

    public AppAuthFieldRepository() {
        super(AuthFieldDO.class);
    }

    public List<AuthFieldDO> findByQuery(AuthPermissionDTO dto) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", dto.getApplicationCode());
        configs.eq("role_code", dto.getRoleCode());
        configs.eq("menu_code", dto.getMenuCode());
        return this.findAllByConfig(configs);
    }

}