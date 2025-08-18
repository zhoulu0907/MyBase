package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
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
public class AppAuthRoleRepository extends DataRepository<AuthRoleDO> {

    public AppAuthRoleRepository() {
        super(AuthRoleDO.class);
    }

    public List<AuthRoleDO> findByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.order("role_type", Order.TYPE.ASC);
        configs.order("role_name", Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    public AuthRoleDO findByApplicationIdAndRoleName(Long applicationId, String roleName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("role_name", roleName);
        return findOne(configs);
    }

    public AuthRoleDO findByApplicationIdAndRoleNameAndIdNot(Long applicationId, String roleName, Long roleId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("role_name", roleName);
        configs.ne("id", roleId);
        return findOne(configs);
    }


}