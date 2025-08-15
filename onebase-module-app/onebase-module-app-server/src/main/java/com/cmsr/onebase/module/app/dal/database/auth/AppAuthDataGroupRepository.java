package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.controller.admin.auth.dto.AuthPermissionDTO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthDataGroupDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用权限数据组数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthDataGroupRepository extends DataRepositoryNew<AuthDataGroupDO> {

    public AppAuthDataGroupRepository() {
        super(AuthDataGroupDO.class);
    }

    public List<AuthDataGroupDO> findByQuery(AuthPermissionDTO dto) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", dto.getApplicationCode());
        configs.eq("role_code", dto.getRoleCode());
        configs.eq("menu_code", dto.getMenuCode());
        configs.order("group_order", Order.TYPE.ASC);
        return this.findAllByConfig(configs);
    }

}