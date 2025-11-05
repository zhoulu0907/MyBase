package com.cmsr.onebase.module.infra.service.security;

import com.cmsr.onebase.module.infra.convert.security.SecurityConfigCategoryConvert;
import com.cmsr.onebase.module.infra.dal.database.SecurityConfigCategoryDataRepository;
import com.cmsr.onebase.module.infra.dal.database.SecurityConfigDataRepository;
import com.cmsr.onebase.module.infra.dal.database.SecurityConfigTemplateDataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigCategoryDO;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigDO;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigTemplateDO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigCategoryRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigUpdateReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置服务实现类
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Service
@Slf4j
@Validated
public class SecurityConfigServiceImpl implements SecurityConfigService {

    @Resource
    private SecurityConfigCategoryDataRepository categoryDataRepository;

    @Resource
    private SecurityConfigTemplateDataRepository templateDataRepository;

    @Resource
    private SecurityConfigDataRepository securityConfigDataRepository;

    private static final String cacheNames = "infra:security:tenant-config#30m";

    @Override
    public List<SecurityConfigCategoryRespVO> getAllCategories() {
        // 接口1：从infra_security_config_category中拉取所有deleted=0的数据
        List<SecurityConfigCategoryDO> categories = categoryDataRepository.findAllActive();
        return SecurityConfigCategoryConvert.INSTANCE.convertList(categories);
    }

    @Override
    public List<SecurityConfigItemRespVO> getTenantConfigItems(Long tenantId, Long categoryId) {
        // 接口2：根据租户id和category_id获取该租户相关安全配置项（不使用缓存）
        return loadTenantConfigItems(tenantId, categoryId);
    }

    @Override
    @Cacheable(cacheNames = cacheNames, key = "#tenantId")
    public List<SecurityConfigItemRespVO> getSecurityConfigsByTenant(Long tenantId) {
        // 获取租户所有安全配置项，用于安全逻辑判断（使用Redis分布式缓存，TTL=30分钟）
        log.info("从数据库加载租户安全配置，tenantId: {}", tenantId);
        List<SecurityConfigTemplateDO> templates = templateDataRepository.findByTenantId(tenantId);
        if (templates.isEmpty()) {
            return new ArrayList<>();
        }

        // 组装返回数据：defaultValue=模板默认值；configValue=租户有效值（租户配置或默认值）
        List<SecurityConfigItemRespVO> configItems = new ArrayList<>();
        for (SecurityConfigTemplateDO template : templates) {
            SecurityConfigItemRespVO itemVO = new SecurityConfigItemRespVO();
            itemVO.setConfigKey(template.getConfigKey());
            itemVO.setConfigName(template.getConfigName());
            itemVO.setDataType(template.getDataType());
            itemVO.setConfigValue(template.getConfigValue());
            itemVO.setDescription(template.getDescription());
            itemVO.setSortOrder(template.getSortOrder());
            itemVO.setId(template.getId());
            itemVO.setCategoryId(template.getCategoryId());
            itemVO.setOptions(template.getOptions());
            itemVO.setMaxvalue(template.getMaxValue());
            itemVO.setMinvalue(template.getMinValue());

            configItems.add(itemVO);
        }

        log.info("租户安全配置加载完成，tenantId: {}, 配置项数量: {}", tenantId, configItems.size());
        return configItems;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = cacheNames, key = "#tenantId")
    public void batchUpdateConfig(Long tenantId, List<SecurityConfigUpdateReqVO> updateReqVOList) {
        // 接口4：批量更新租户安全配置
        if (updateReqVOList == null || updateReqVOList.isEmpty()) {
            return;
        }

        log.info("开始批量更新租户安全配置，tenantId: {}, 配置项数量: {}", tenantId, updateReqVOList.size());

        for (SecurityConfigUpdateReqVO updateReqVO : updateReqVOList) {
            updateSingleConfig(tenantId, updateReqVO);
        }

        log.info("批量更新租户安全配置完成，tenantId: {}, 配置项数量: {}，已自动清除缓存", tenantId, updateReqVOList.size());
    }

    /**
     * 更新单个配置项
     *
     * @param tenantId    租户ID
     * @param updateReqVO 更新请求
     */
    private void updateSingleConfig(Long tenantId, SecurityConfigUpdateReqVO updateReqVO) {
        SecurityConfigDO config = securityConfigDataRepository.findByTenantIdAndKey(tenantId, updateReqVO.getConfigKey());

        if (config == null) {
            // 如果不存在，创建新配置
            config = SecurityConfigDO.builder()
                    .tenantId(tenantId)
                    .configKey(updateReqVO.getConfigKey())
                    .configValue(updateReqVO.getConfigValue())
                    .build();
            securityConfigDataRepository.insert(config);
        } else {
            // 如果存在，更新配置
            config.setConfigValue(updateReqVO.getConfigValue());
            securityConfigDataRepository.update(config);
        }
    }

    /**
     * 加载租户配置项（带兜底策略）
     *
     * @param tenantId   租户ID
     * @param categoryId 分类ID
     * @return 配置项列表
     */
    private List<SecurityConfigItemRespVO> loadTenantConfigItems(Long tenantId, Long categoryId) {
        // 使用新的带兜底策略的SQL查询方法
        // 该方法会自动处理：如果租户配置存在则使用租户配置值，否则使用模板默认值
        List<SecurityConfigTemplateDO> templates = templateDataRepository.findByTenantIdAndCategoryId(tenantId, categoryId);
        if (templates.isEmpty()) {
            return new ArrayList<>();
        }

        // 组装返回数据：defaultValue=模板默认值；configValue=租户有效值（租户配置或默认值）
        List<SecurityConfigItemRespVO> configItems = new ArrayList<>();
        for (SecurityConfigTemplateDO template : templates) {
            SecurityConfigItemRespVO itemVO = new SecurityConfigItemRespVO();
            itemVO.setConfigKey(template.getConfigKey());
            itemVO.setConfigName(template.getConfigName());
            itemVO.setDataType(template.getDataType());
            itemVO.setConfigValue(template.getConfigValue());
            itemVO.setDescription(template.getDescription());
            itemVO.setSortOrder(template.getSortOrder());
            itemVO.setId(template.getId());
            itemVO.setCategoryId(template.getCategoryId());
            itemVO.setOptions(template.getOptions());
            itemVO.setMaxvalue(template.getMaxValue());
            itemVO.setMinvalue(template.getMinValue());

            configItems.add(itemVO);
        }

        return configItems;
    }

}