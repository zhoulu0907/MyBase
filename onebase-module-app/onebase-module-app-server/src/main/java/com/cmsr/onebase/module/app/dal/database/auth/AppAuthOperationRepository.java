package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionReqVO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthOperationDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用权限操作数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthOperationRepository extends DataRepositoryNew<AuthOperationDO> {

    public AppAuthOperationRepository() {
        super(AuthOperationDO.class);
    }

    public List<AuthOperationDO> findByQuery(AuthPermissionReqVO reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        return this.findAllByConfig(configs);
    }

}