package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigTemplateDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全配置模板数据访问层
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Repository
public class SecurityConfigTemplateDataRepository extends DataRepository<SecurityConfigTemplateDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public SecurityConfigTemplateDataRepository() {
        super(SecurityConfigTemplateDO.class);
    }

    /**
     * 根据分类ID查询模板，按排序号升序
     *
     * @param categoryId 分类ID
     * @return 模板列表
     */
    public List<SecurityConfigTemplateDO> findByCategoryId(Long categoryId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(SecurityConfigTemplateDO.CATEGORY_ID, categoryId);
        configStore.order(SecurityConfigTemplateDO.SORT_ORDER, "ASC");
        return findAllByConfig(configStore);
    }

    /**
     * 查询所有有效的模板
     *
     * @return 模板列表
     */
    public List<SecurityConfigTemplateDO> findAllActive() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order(SecurityConfigTemplateDO.CATEGORY_ID, "ASC");
        configStore.order(SecurityConfigTemplateDO.SORT_ORDER, "ASC");
        return findAllByConfig(configStore);
    }

}
