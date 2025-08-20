package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionReqVO;
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
public class AppAuthFieldRepository extends DataRepository<AuthFieldDO> {

    public AppAuthFieldRepository() {
        super(AuthFieldDO.class);
    }

    public List<AuthFieldDO> findByQuery(AuthPermissionReqVO reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        return this.findAllByConfig(configs);
    }

}