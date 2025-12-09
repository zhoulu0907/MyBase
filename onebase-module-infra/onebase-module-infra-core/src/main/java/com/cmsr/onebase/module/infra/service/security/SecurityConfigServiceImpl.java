package com.cmsr.onebase.module.infra.service.security;

import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.convert.security.SecurityConfigCategoryConvert;
import com.cmsr.onebase.module.infra.dal.database.SecurityConfigCategoryDataRepository;
import com.cmsr.onebase.module.infra.dal.database.SecurityConfigDataRepository;
import com.cmsr.onebase.module.infra.dal.database.SecurityConfigTemplateDataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigCategoryDO;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigDO;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigTemplateDO;
import com.cmsr.onebase.module.infra.dal.vo.app.AppTenantVO;
import com.cmsr.onebase.module.infra.dal.vo.security.*;
import com.cmsr.onebase.module.infra.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.infra.dal.redis.RedisKeyConstants.SECURITY_TENANT_CONFIGS;

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

    public static final String DATATYPE_STRING       = "STRING";
    public static final String DATATYPE_INTEGER      = "INTEGER";
    public static final String DATATYPE_BOOLEAN      = "BOOLEAN";
    public static final String DATATYPE_JSON_STRING  = "JSON[STRING]";
    public static final String DATATYPE_JSON_INTEGER = "JSON[INTEGER]";
    public static final String DATATYPE_JSON_BOOLEAN = "JSON[BOOLEAN]";

    @Resource
    private SecurityConfigCategoryDataRepository categoryDataRepository;

    @Resource
    private SecurityConfigTemplateDataRepository templateDataRepository;

    @Resource
    private SecurityConfigDataRepository securityConfigDataRepository;

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
    @Cacheable(cacheNames = SECURITY_TENANT_CONFIGS, key = "#tenantId")
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
            SecurityConfigItemRespVO itemVO = SecurityConfigCategoryConvert.INSTANCE.convert(template);
            configItems.add(itemVO);
        }

        log.info("租户安全配置加载完成，tenantId: {}, 配置项数量: {}", tenantId, configItems.size());
        return configItems;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = SECURITY_TENANT_CONFIGS, key = "#tenantId")
    public void batchUpdateConfig(Long tenantId, List<SecurityConfigUpdateReqVO> updateReqVOList) {
        // 接口4：批量更新租户安全配置
        if (CollectionUtils.isEmpty(updateReqVOList)) {
            return;
        }

        log.info("开始批量更新租户安全配置，tenantId: {}, 配置项数量: {}", tenantId, updateReqVOList.size());

        // 校验配置项
        validateConfigUpdates(tenantId, updateReqVOList);

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
            SecurityConfigItemRespVO itemVO = SecurityConfigCategoryConvert.INSTANCE.convert(template);
            configItems.add(itemVO);
        }

        return configItems;
    }


    private List<SecurityConfigItemRespVO> findSecurityConfigItemsByTenantAndCatCodes(Long tenantId, List<String> codes) {
        // 使用新的带兜底策略的SQL查询方法
        // 该方法会自动处理：如果租户配置存在则使用租户配置值，否则使用模板默认值
        List<SecurityConfigCategoryDO> categoryDOS = categoryDataRepository.findActiveByCodes(codes);
        if (CollectionUtils.isEmpty(categoryDOS)) {
            return new ArrayList<>();
        }

        Map<Long, String> idCodeMap = categoryDOS.stream()
                .collect(Collectors.toMap(
                        SecurityConfigCategoryDO::getId,    // key mapper (id)
                        SecurityConfigCategoryDO::getCategoryCode   // value mapper (code)
                ));

        // 将categoryDOS转换为 categoryIds
        List<Long> categoryIds = categoryDOS.stream().map(SecurityConfigCategoryDO::getId).collect(Collectors.toList());

        // 查询所有具体配置项
        List<SecurityConfigTemplateDO> templates = templateDataRepository.findByTenantIdAndCategoryIdList(tenantId, categoryIds);
        if (CollectionUtils.isEmpty(templates)) {
            return new ArrayList<>();
        }

        // 组装返回数据：defaultValue=模板默认值；configValue=租户有效值（租户配置或默认值）
        List<SecurityConfigItemRespVO> configItems = new ArrayList<>();
        for (SecurityConfigTemplateDO template : templates) {
            SecurityConfigItemRespVO itemVO = SecurityConfigCategoryConvert.INSTANCE.convert(template);
            itemVO.setCategoryCode(idCodeMap.get(template.getCategoryId()));
            configItems.add(itemVO);
        }

        return configItems;
    }

    /**
     * 校验配置更新请求
     *
     * @param tenantId        租户ID
     * @param updateReqVOList 更新请求列表
     */
    private void validateConfigUpdates(Long tenantId, List<SecurityConfigUpdateReqVO> updateReqVOList) {
        // 获取租户所有安全配置模板
        List<SecurityConfigItemRespVO> templates = getSecurityConfigsByTenant(tenantId);

        // 转换为Map便于快速查找
        Map<String, SecurityConfigItemRespVO> templateMap = templates.stream()
                .collect(Collectors.toMap(SecurityConfigItemRespVO::getConfigKey, item -> item));

        // 校验每个配置项
        for (SecurityConfigUpdateReqVO updateReqVO : updateReqVOList) {
            String configKey = updateReqVO.getConfigKey();
            String configValue = updateReqVO.getConfigValue();

            // 1. Key匹配性校验
            SecurityConfigItemRespVO template = templateMap.get(configKey);
            if (template == null) {
                throw exception(SECURITY_CONFIG_NOT_EXIST, configKey);
            }

            // 2. 必填校验
            if ("true".equalsIgnoreCase(template.getRequired())) {
                if (configValue == null || configValue.trim().isEmpty()) {
                    throw exception(SECURITY_CONFIG_ITEM_REQUIRED, template.getConfigName());
                }
            }

            // 如果值为空且非必填，跳过后续校验
            if (configValue == null || configValue.trim().isEmpty()) {
                continue;
            }

            String dataType = template.getDataType().toUpperCase(Locale.ROOT);

            // 3. 数据类型校验
            validateDataType(template.getConfigName(), configValue, dataType);

            // 4. 数值边界校验（仅INTEGER类型）
            if (DATATYPE_INTEGER.equals(dataType)) {
                validateIntegerRange(template.getConfigName(), configValue, template.getMinValue(), template.getMaxValue());
            }
        }
    }

    /**
     * 校验数据类型
     *
     * @param configName  配置项名称
     * @param configValue 配置值
     * @param dataType    数据类型
     */
    private void validateDataType(String configName, String configValue, String dataType) {
        try {
            switch (dataType) {
                case DATATYPE_STRING:
                    // 字符串类型，任意值都可以
                    break;

                case DATATYPE_INTEGER:
                    // 整数类型
                    Long.parseLong(configValue.trim());
                    break;

                case DATATYPE_BOOLEAN:
                    // 布尔类型
                    String lowerValue = configValue.trim().toLowerCase();
                    if (!"true".equals(lowerValue) && !"false".equals(lowerValue)) {
                        throw new IllegalArgumentException();
                    }
                    break;

                case DATATYPE_JSON_STRING:
                    // 逗号分隔的字符串，不需要特殊校验
                    break;

                case DATATYPE_JSON_INTEGER:
                    // 逗号分隔的整数
                    String[] intValues = configValue.split(",");
                    for (String value : intValues) {
                        Long.parseLong(value.trim());
                    }
                    break;

                case DATATYPE_JSON_BOOLEAN:
                    // 逗号分隔的布尔值
                    String[] boolValues = configValue.split(",");
                    for (String value : boolValues) {
                        String lowerVal = value.trim().toLowerCase();
                        if (!"true".equals(lowerVal) && !"false".equals(lowerVal)) {
                            throw new IllegalArgumentException();
                        }
                    }
                    break;

                default:
                    throw exception(SECURITY_CONFIG_DATA_TYPE_NOT_SUPPORT, configName, dataType);
            }
        } catch (IllegalArgumentException e) {
            throw exception(SECURITY_CONFIG_DATA_TYPE_WRONG, configName, dataType);
        }
    }

    /**
     * 校验整数范围
     *
     * @param configName  配置项名称
     * @param configValue 配置值
     * @param minValue    最小值
     * @param maxValue    最大值
     */
    private void validateIntegerRange(String configName, String configValue, Long minValue, Long maxValue) {
        try {
            long value = Long.parseLong(configValue.trim());

            if (minValue != null && value < minValue) {
                throw exception(SECURITY_CONFIG_MIN_VALUE, configName, minValue);
            }

            if (maxValue != null && value > maxValue) {
                throw exception(SECURITY_CONFIG_MAX_VALUE, configName, maxValue);
            }
        } catch (NumberFormatException e) {
            throw exception(SECURITY_CONFIG_DATA_TYPE_WRONG, configName, DATATYPE_INTEGER);
        }
    }

    @Override
    public Integer getIntConfig(Long tenantId, String configKey) {
        List<SecurityConfigItemRespVO> configs = getSecurityConfigsByTenant(tenantId);
        if (configs == null || configs.isEmpty()) {
            return null;
        }

        for (SecurityConfigItemRespVO config : configs) {
            if (configKey.equals(config.getConfigKey())) {
                String configValue = config.getConfigValue();
                if (configValue == null || configValue.trim().isEmpty()) {
                    return null;
                }
                try {
                    return Integer.parseInt(configValue.trim());
                } catch (NumberFormatException e) {
                    log.warn("解析配置值失败, tenantId: {}, configKey: {}, value: {}", tenantId, configKey, configValue);
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    @TenantIgnore
    public List<SecurityConfigCategoryGroupRespVO> getTenantConfigItemsByCategoryCodes(SecurityConfigGetReqVO configReqVO) {
        Long tenantId = configReqVO.getTenantId();

        // 优先使用 appId 获取租户ID，覆盖 tenantId
        Long appID = configReqVO.getAppId();
        if (null != appID) {
            tenantId = checkAppAndGetTenantId(appID);
        }

        List<SecurityConfigItemRespVO> configItems = findSecurityConfigItemsByTenantAndCatCodes(tenantId, configReqVO.getCategoryCode());
        Map<String, List<SecurityConfigItemRespVO>> groupedByConfigKey = configItems.stream()
                .collect(Collectors.groupingBy(SecurityConfigItemRespVO::getCategoryCode));

        return groupedByConfigKey.entrySet().stream()
                .map(entry -> {
                    SecurityConfigCategoryGroupRespVO groupVO = new SecurityConfigCategoryGroupRespVO();
                    groupVO.setCategoryCode(entry.getKey());
                    groupVO.setSecurityConfigItemRespVO(entry.getValue());
                    return groupVO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 通过appid 获取租户id
     *
     * @param appId
     * @return
     */
    private Long checkAppAndGetTenantId(Long appId) {
        AppTenantVO app = templateDataRepository.findAppTenantIdById(appId);
        if (null != app) {
            return app.getTenantId();
        }
        throw exception(ErrorCodeConstants.APP_DELETE_OR_DISABLE, appId);
    }
}