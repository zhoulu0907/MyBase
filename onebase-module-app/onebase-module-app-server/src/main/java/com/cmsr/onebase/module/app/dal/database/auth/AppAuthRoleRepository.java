package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthRoleDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用角色数据访问类
 *
 * @author huangjie
 * @date 2025-08-05
 */
@Repository
public class AppAuthRoleRepository extends DataRepositoryNew<AuthRoleDO> {

    public AppAuthRoleRepository() {
        super(AuthRoleDO.class);
    }

    public List<AuthRoleDO> findByApplicationCode(String applicationCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", applicationCode);
        configs.order("role_type", Order.TYPE.ASC);
        configs.order("role_name", Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    public AuthRoleDO findByAppCodeAndRoleName(String applicationCode, String roleName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", applicationCode);
        configs.eq("role_name", roleName);
        return findOne(configs);
    }

    public AuthRoleDO findByAppCodeAndRoleNameAndRoleIdNot(String applicationCode, String roleName, Long roleId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", applicationCode);
        configs.eq("role_name", roleName);
        configs.ne("id", roleId);
        return findOne(configs);
    }

    public void deleteByRoleCode(String roleCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("role_code", roleCode);
        deleteByConfig(configs);
    }

    public AuthRoleDO findByRoleCode(String roleCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("role_code", roleCode);
        return findOne(configs);
    }


}