package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthDataFilterDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据权限配置-数据过滤条件数据访问类
 *
 * @author lingma
 * @date 2025-07-25
 */
@Repository
public class AppAuthDataFilterRepository extends DataRepository {

    public AppAuthDataFilterRepository() {
        super(AuthDataFilterDO.class);
    }

    public List<AuthDataFilterDO> findByGroupId(Long groupId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("group_id", groupId);
        configs.order("condition_group", Order.TYPE.ASC);
        configs.order("condition_order", Order.TYPE.ASC);
        return this.findAllByConfig(AuthDataFilterDO.class, configs);
    }

    public void deleteByGroupId(Long groupId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("group_id", groupId);
        this.deleteByConfig(AuthDataFilterDO.class, configs);
    }

}