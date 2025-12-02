package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.build.controller.admin.number.vo.AutoNumberConfigWithRulesRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

/**
 * 自动编号配置 Build Service 接口
 *
 * @author bty418
 * @date 2025-09-17
 */
public interface AutoNumberConfigBuildService {

    /**
     * 新增或更新自动编号配置
     *
     * @param config 配置信息
     * @return 配置ID
     */
    Long upsert(MetadataAutoNumberConfigDO config);

    /**
     * 根据字段UUID获取自动编号配置
     *
     * @param fieldUuid 字段UUID
     * @return 配置信息
     */
    MetadataAutoNumberConfigDO getByFieldId(String fieldUuid);

    /**
     * 根据字段UUID删除自动编号配置
     *
     * @param fieldUuid 字段UUID
     */
    void deleteByFieldId(String fieldUuid);

    /**
     * 根据配置ID获取规则项列表
     *
     * @param configId 配置ID
     * @return 规则项列表
     */
    List<MetadataAutoNumberRuleItemDO> listRules(Long configId);

    /**
     * 根据配置ID删除所有规则项
     *
     * @param configId 配置ID
     */
    void deleteRulesByConfigId(Long configId);

    /**
     * 获取自动编号配置及其规则项（Controller使用）
     *
     * @param fieldUuid 字段UUID
     * @return 配置和规则项的响应VO，如果配置不存在则返回null
     */
    AutoNumberConfigWithRulesRespVO getAutoNumberConfigWithRules(String fieldUuid);

    /**
     * 保存自动编号配置（Controller使用）
     *
     * @param config 配置信息
     * @return 配置ID
     */
    Long saveAutoNumberConfig(MetadataAutoNumberConfigDO config);
}