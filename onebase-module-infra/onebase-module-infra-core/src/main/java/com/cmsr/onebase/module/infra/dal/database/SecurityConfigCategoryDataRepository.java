package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigCategoryDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全配置分类数据访问层
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Repository
public class SecurityConfigCategoryDataRepository extends DataRepository<SecurityConfigCategoryDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public SecurityConfigCategoryDataRepository() {
        super(SecurityConfigCategoryDO.class);
    }

    /**
     * 查询所有有效的分类，按排序号升序
     *
     * @return 分类列表
     */
    @TenantIgnore
    public List<SecurityConfigCategoryDO> findAllActive() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order(SecurityConfigCategoryDO.SORT_ORDER, "ASC");
        return findAllByConfig(configStore);
    }

}
