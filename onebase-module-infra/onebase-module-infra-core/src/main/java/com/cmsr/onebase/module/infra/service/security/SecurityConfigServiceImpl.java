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
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.cmsr.onebase.framework.common.util.cache.CacheUtils.buildAsyncReloadingCache;

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

    /**
     * 租户配置缓存，提升查询性能
     * Key: tenantId_categoryId, Value: 配置项列表
     */
    @SuppressWarnings("deprecation")
    private final LoadingCache<String, List<SecurityConfigItemRespVO>> configCache = buildAsyncReloadingCache(
            Duration.ofMinutes(10L),
            new CacheLoader<>() {
                @Override
                public List<SecurityConfigItemRespVO> load(String key) {
                    String[] parts = key.split("_", 2);
                    if (parts.length != 2) {
                        return new ArrayList<>();
                    }
                    Long tenantId = Long.parseLong(parts[0]);
                    Long categoryId = Long.parseLong(parts[1]);
                    return loadTenantConfigItems(tenantId, categoryId);
                }
            });

    @Override
    public List<SecurityConfigCategoryRespVO> getAllCategories() {
        // 接口1：从infra_security_config_category中拉取所有deleted=0的数据
        List<SecurityConfigCategoryDO> categories = categoryDataRepository.findAllActive();
        return SecurityConfigCategoryConvert.INSTANCE.convertList(categories);
    }

    @Override
    public List<SecurityConfigItemRespVO> getTenantConfigItems(Long tenantId, Long categoryId) {
        // 接口2：根据租户id和category_id获取该租户相关安全配置项
        // 使用缓存提升性能
        try {
            String cacheKey = buildCacheKey(tenantId, categoryId);
            return configCache.get(cacheKey);
        } catch (Exception e) {
            log.error("获取租户安全配置失败，租户ID: {}, 分类ID: {}", tenantId, categoryId, e);
            return loadTenantConfigItems(tenantId, categoryId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Long tenantId, SecurityConfigUpdateReqVO updateReqVO) {
        // 接口3：根据租户id、config_key更新infra_security_config数据
        updateSingleConfig(tenantId, updateReqVO);

        // 清除相关缓存
        clearRelatedCache(tenantId);

        log.info("更新租户安全配置，租户ID: {}, 配置键: {}, 配置值: {}", 
                tenantId, updateReqVO.getConfigKey(), updateReqVO.getConfigValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateConfig(Long tenantId, List<SecurityConfigUpdateReqVO> updateReqVOList) {
        // 接口4：批量更新租户安全配置
        if (updateReqVOList == null || updateReqVOList.isEmpty()) {
            return;
        }

        for (SecurityConfigUpdateReqVO updateReqVO : updateReqVOList) {
            updateSingleConfig(tenantId, updateReqVO);
        }

        // 清除相关缓存
        clearRelatedCache(tenantId);

        log.info("批量更新租户安全配置，租户ID: {}, 配置项数量: {}", tenantId, updateReqVOList.size());
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

        // 组装返回数据（default_value字段已通过SQL的COALESCE处理了兜底逻辑）
        List<SecurityConfigItemRespVO> configItems = new ArrayList<>();
        for (SecurityConfigTemplateDO template : templates) {
            SecurityConfigItemRespVO itemVO = new SecurityConfigItemRespVO();
            itemVO.setConfigKey(template.getConfigKey());
            itemVO.setConfigName(template.getConfigName());
            itemVO.setDataType(template.getDataType());
            itemVO.setDefaultValue(template.getDefaultValue()); // 已包含兜底逻辑
            itemVO.setConfigValue(template.getDefaultValue()); // 已包含兜底逻辑
            itemVO.setDescription(template.getDescription());
            itemVO.setSortOrder(template.getSortOrder());
            itemVO.setId(template.getId());

            configItems.add(itemVO);
        }

        return configItems;
    }

    /**
     * 构建缓存键
     *
     * @param tenantId   租户ID
     * @param categoryId 分类ID
     * @return 缓存键
     */
    private String buildCacheKey(Long tenantId, Long categoryId) {
        return tenantId + "_" + categoryId;
    }

    /**
     * 清除租户相关缓存
     *
     * @param tenantId 租户ID
     */
    private void clearRelatedCache(Long tenantId) {
        // 清除该租户所有分类的缓存
        configCache.asMap().keySet().removeIf(key -> key.startsWith(tenantId + "_"));
    }

}