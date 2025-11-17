package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
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

    public long countByAppIdAndRoleCode(Long applicationId, String roleCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("role_code", roleCode);
        return countByConfig(configs);
    }

    public AuthRoleDO findByApplicationIdAndRoleNameAndIdNot(Long applicationId, String roleName, Long roleId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("role_name", roleName);
        configs.ne("id", roleId);
        return findOne(configs);
    }


    public List<AuthRoleDO> findByUserIdAndApplicationId(Long userId, Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.param("userId", userId);
        configs.param("applicationId", applicationId);
        String sql = """
                select
                	aar.id, aar.role_code, aar.role_type, aar.role_name
                from
                	app_auth_role_user aaru ,
                	app_auth_role aar
                where
                	aaru.role_id = aar.id
                    and aaru.deleted = 0
                    and aar.deleted = 0
                	and aaru.user_id = #{userId}
                	and aar.application_id = #{applicationId}
                """;
        return this.querys(sql, configs).stream().map(row -> {
            AuthRoleDO authRoleDO = new AuthRoleDO();
            authRoleDO.setId(row.getLong("id"));
            authRoleDO.setRoleCode(row.getString("role_code"));
            authRoleDO.setRoleType(row.getInt("role_type"));
            authRoleDO.setRoleName(row.getString("role_name"));
            return authRoleDO;
        }).toList();
    }
}