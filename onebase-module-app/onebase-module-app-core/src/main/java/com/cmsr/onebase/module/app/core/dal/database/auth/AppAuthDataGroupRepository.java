package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthDataGroupDO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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

    public List<AuthDataGroupDO> findByQuery(AuthPermissionReq reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", reqVO.getApplicationId());
        configs.eq("role_id", reqVO.getRoleId());
        configs.eq("menu_id", reqVO.getMenuId());
        configs.order("group_order", Order.TYPE.ASC);
        return this.findAllByConfig(configs);
    }

    public void updateAuthDataGroup(AuthDataGroupDO authDataGroupDO) {
        DataRow row = new DataRow();
        row.put("group_name", authDataGroupDO.getGroupName());
        row.put("group_order", authDataGroupDO.getGroupOrder());
        row.put("description", authDataGroupDO.getDescription());
        row.put("scope_tags", authDataGroupDO.getScopeTags());
        row.put("scope_field_id", authDataGroupDO.getScopeFieldId());
        row.put("scope_level", authDataGroupDO.getScopeLevel());
        row.put("scope_value", authDataGroupDO.getScopeValue());
        row.put("data_filter", authDataGroupDO.getDataFilter());
        row.put("operation_tags", authDataGroupDO.getOperationTags());
        row.addAllUpdateColumns(true);

        ConfigStore configs = new DefaultConfigStore();
        configs.eq("id", authDataGroupDO.getId());
        anylineService.update("app_auth_data_group", row, configs);
        this.update(authDataGroupDO);
    }

    public List<AuthDataGroupDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
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
                left join app_auth_data_group g
                on
                	r.application_id = g.application_id
                	and r.id = g.role_id
                where
                	r.deleted = 0
                	and (g.deleted = 0 or g.deleted is null)
                	and r.application_id = #{applicationId}
                	and r.id in (#{roleIds})
                	and (g.menu_id = #{menuId}
                		or g.menu_id is null)
                """;
        DataSet dataSet = this.querys(sql, configs);
        return dataSet.stream().map(dataRow -> {
            AuthDataGroupDO authDataGroupDO = new AuthDataGroupDO();
            authDataGroupDO.setId(dataRow.getLong("id"));
            authDataGroupDO.setApplicationId(dataRow.getLong("application_id"));
            authDataGroupDO.setRoleId(dataRow.getLong("role_id"));
            authDataGroupDO.setMenuId(dataRow.getLong("menu_id"));
            authDataGroupDO.setGroupName(dataRow.getString("group_name"));
            authDataGroupDO.setGroupOrder(dataRow.getInt("group_order"));
            authDataGroupDO.setDescription(dataRow.getString("description"));
            authDataGroupDO.setScopeTags(dataRow.getString("scope_tags"));
            authDataGroupDO.setScopeFieldId(dataRow.getLong("scope_field_id"));
            authDataGroupDO.setScopeLevel(dataRow.getString("scope_level"));
            authDataGroupDO.setScopeValue(dataRow.getString("scope_value"));
            authDataGroupDO.setDataFilter(dataRow.getString("data_filter"));
            authDataGroupDO.setOperationTags(dataRow.getString("operation_tags"));
            return authDataGroupDO;
        }).toList();
    }


}