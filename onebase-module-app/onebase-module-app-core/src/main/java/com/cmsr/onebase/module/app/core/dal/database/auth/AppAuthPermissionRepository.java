package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
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
        configs.param("applicationId", applicationId);
        configs.param("roleIds", roleIds);
        configs.param("menuId", menuId);
        String sql = """
                select
                	r.role_code,
                	r.role_type,
                	g.*
                from
                	app_auth_role r
                left join app_auth_permission g
                on
                	r.application_id = g.application_id
                	and r.id = g.role_id
                where
                	r.deleted = 0
                	and (g.deleted = 0 or g.deleted is null)
                	and r.application_id = #{applicationId}
                	and r.id in (#{roleIds})
                	and (g.menu_id = #{menuId} or g.menu_id is null)
                """;
        DataSet dataSet = this.querys(sql, configs);
        return dataSet.stream().map(dataRow -> {
            AuthPermissionDO permissionDO = new AuthPermissionDO();
            permissionDO.setId(dataRow.getLong("id"));
            permissionDO.setApplicationId(dataRow.getLong("application_id"));
            permissionDO.setRoleId(dataRow.getLong("role_id"));
            permissionDO.setMenuId(dataRow.getLong("menu_id"));
            permissionDO.setIsPageAllowed(dataRow.getInt("is_page_allowed"));
            permissionDO.setIsAllViewsAllowed(dataRow.getInt("is_all_views_allowed"));
            permissionDO.setIsAllFieldsAllowed(dataRow.getInt("is_all_fields_allowed"));
            permissionDO.setOperationTags(dataRow.getString("operation_tags"));
            return permissionDO;
        }).toList();
    }


    public List<AuthPermissionDO> findByAppIdAndRoleIds(Long applicationId, Set<Long> roleIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.param("applicationId", applicationId);
        configs.param("roleIds", roleIds);
        String sql = """
                select
                	r.role_code,
                	r.role_type,
                	g.*
                from
                	app_auth_role r
                left join app_auth_permission g
                on
                	r.application_id = g.application_id
                	and r.id = g.role_id
                where
                	r.deleted = 0
                	and (g.deleted = 0 or g.deleted is null)
                	and r.application_id = #{applicationId}
                	and r.id in (#{roleIds})
                """;
        DataSet dataSet = this.querys(sql, configs);
        return dataSet.stream().map(dataRow -> {
            AuthPermissionDO permissionDO = new AuthPermissionDO();
            permissionDO.setId(dataRow.getLong("id"));
            permissionDO.setApplicationId(dataRow.getLong("application_id"));
            permissionDO.setRoleId(dataRow.getLong("role_id"));
            permissionDO.setMenuId(dataRow.getLong("menu_id"));
            permissionDO.setIsPageAllowed(dataRow.getInt("is_page_allowed"));
            permissionDO.setIsAllViewsAllowed(dataRow.getInt("is_all_views_allowed"));
            permissionDO.setIsAllFieldsAllowed(dataRow.getInt("is_all_fields_allowed"));
            permissionDO.setOperationTags(dataRow.getString("operation_tags"));
            return permissionDO;
        }).toList();
    }
}