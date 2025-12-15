package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Repository
public class SystemGeneralConfigDataRepository  extends DataRepository<SystemGeneralConfigDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public SystemGeneralConfigDataRepository() {
        super(SystemGeneralConfigDO.class);
    }


    public SystemGeneralConfigDO getConfigByKey(String key) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(SystemGeneralConfigDO.CONFIG_KEY, key);
        return findOne(configStore);

    }
}
