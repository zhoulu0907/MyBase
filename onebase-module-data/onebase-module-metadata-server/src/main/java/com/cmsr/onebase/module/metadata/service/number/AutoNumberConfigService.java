package com.cmsr.onebase.module.metadata.service.number;

import com.cmsr.onebase.module.metadata.controller.admin.number.vo.AutoNumberConfigWithRulesRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

/**
 * 自动编号-配置 Service
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface AutoNumberConfigService {
    MetadataAutoNumberConfigDO getByFieldId(Long fieldId);

    MetadataAutoNumberConfigDO getByConfigId(Long configId);

    Long upsert(MetadataAutoNumberConfigDO config);

    void deleteByFieldId(Long fieldId);

    List<MetadataAutoNumberRuleItemDO> listRules(Long configId);
    
    /**
     * 获取自动编号配置与规则
     *
     * @param fieldId 字段ID
     * @return 配置与规则响应VO
     */
    AutoNumberConfigWithRulesRespVO getAutoNumberConfigWithRules(Long fieldId);
    
    /**
     * 保存/更新自动编号配置
     *
     * @param config 配置对象
     * @return 配置ID
     */
    Long saveAutoNumberConfig(MetadataAutoNumberConfigDO config);
}


