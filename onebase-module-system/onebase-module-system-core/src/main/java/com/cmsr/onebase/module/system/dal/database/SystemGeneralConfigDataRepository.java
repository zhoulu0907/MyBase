package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.PostDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.enums.config.ConfigCategoryEnum;
import com.cmsr.onebase.module.system.vo.config.SystemConfigPageReqVO;
import com.cmsr.onebase.module.system.vo.config.SystemGeneralConfigSearchVO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SystemGeneralConfigDataRepository  extends DataRepository<SystemGeneralConfigDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public SystemGeneralConfigDataRepository() {
        super(SystemGeneralConfigDO.class);
    }



    public List<SystemGeneralConfigDO> findConfigList(SystemConfigPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        if (StringUtils.isNotBlank(reqVO.getName())) {
            configs.and(Compare.LIKE, SystemGeneralConfigDO.NAME, reqVO.getName());
        }
        if (reqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.STATUS, reqVO.getStatus());
        }
        // 添加排序条件，按ID降序排列
        configs.order(SystemGeneralConfigDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return  findAllByConfig(configs);
    }

    @TenantIgnore
    public SystemGeneralConfigDO getConfigByDiffCategory(SystemGeneralConfigSearchVO searchVO) {
        DefaultConfigStore configs = new DefaultConfigStore();
        if (StringUtils.isNotBlank(searchVO.getCategory())) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CATEGORY, searchVO.getCategory());
        }
        if (StringUtils.isNotBlank(searchVO.getConfigKey())) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_KEY, searchVO.getConfigKey());
        }
        if (null != searchVO.getTenantId()) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.TENANT_ID, searchVO.getTenantId());
        }
        if (null != searchVO.getCorpId()) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CORP_ID, searchVO.getCorpId());
        }
        return findOne(configs);
    }

    @TenantIgnore
    public List<SystemGeneralConfigDO> findGlobaConfigList() {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, SystemGeneralConfigDO.CATEGORY, ConfigCategoryEnum.GLOBAL.getCode());
        // 添加排序条件，按ID降序排列
        configs.order(SystemGeneralConfigDO.ID, org.anyline.entity.Order.TYPE.DESC);
        return  findAllByConfig(configs);
    }
}
