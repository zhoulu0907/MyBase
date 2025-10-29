package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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

    public AuthPermissionDO findByQuery(AuthPermissionReq reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        return this.findOne(configs);
    }

    public List<AuthPermissionDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.in("role_id", roleIds);
        configs.eq("menu_id", menuId);
        return this.findAllByConfig(configs);
    }

    public List<AuthPermissionDO> findByAppIdAndRoleId(Long applicationId, Long roleId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("role_id", roleId);
        return this.findAllByConfig(configs);
    }
}