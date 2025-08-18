package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
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
public class AppAuthDataGroupRepository extends DataRepository<AuthDataGroupDO> {

    public AppAuthDataGroupRepository() {
        super(AuthDataGroupDO.class);
    }

    public List<AuthDataGroupDO> findByQuery(AuthPermissionDTO dto) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", dto.getApplicationId());
        configs.eq("role_id", dto.getRoleId());
        configs.eq("menu_id", dto.getMenuId());
        configs.order("group_order", Order.TYPE.ASC);
        return this.findAllByConfig(configs);
    }

}