package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthFieldDO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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

    public List<AuthFieldDO> findByQuery(AuthPermissionReq reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        return this.findAllByConfig(configs);
    }

    public AuthFieldDO findByQuery(AuthPermissionReq reqVO, Long fieldId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        configs.eq("field_id", fieldId);
        return this.findOne(configs);
    }

    public void deleteByQuery(AuthPermissionReq reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        this.deleteByConfig(configs);
    }

    public List<AuthFieldDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.in("role_id", roleIds);
        configs.eq("menu_id", menuId);
        return this.findAllByConfig(configs);
    }
}