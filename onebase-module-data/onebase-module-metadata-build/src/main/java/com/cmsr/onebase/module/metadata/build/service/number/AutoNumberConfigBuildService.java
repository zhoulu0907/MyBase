package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.AutoNumberConfigReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.AutoNumberConfigRespVO;
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

    /**
     * 保存自动编号配置（统一规则列表方式）
     * <p>
     * 将统一规则列表中的SEQUENCE类型存入Config表，其他类型存入RuleItem表
     *
     * @param fieldUuid 字段UUID
     * @param reqVO     配置请求VO（包含统一规则列表）
     * @return 配置ID
     */
    Long saveConfigWithUnifiedRules(String fieldUuid, AutoNumberConfigReqVO reqVO);

    /**
     * 获取自动编号配置（统一规则列表方式）
     * <p>
     * 将Config中的SEQUENCE配置与RuleItem合并为统一规则列表返回
     *
     * @param fieldUuid 字段UUID
     * @return 配置响应VO（包含统一规则列表），如果配置不存在则返回null
     */
    AutoNumberConfigRespVO getConfigWithUnifiedRules(String fieldUuid);
}