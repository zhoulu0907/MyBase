package com.cmsr.onebase.module.metadata.core.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataDatasourceRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

/**
 * Metadata模块 ID与UUID转换工具类
 * <p>
 * 支持前端传入Long类型ID自动转换为UUID，实现向后兼容。
 * 转换逻辑优先级：UUID非空 → 直接返回；UUID空但ID非空 → 查询转换；两者都空 → 抛出参数校验异常
 *
 * @author matianyu
 * @date 2025-12-03
 */
@Component
@Slf4j
public class MetadataIdUuidConverter {

    @Resource
    private MetadataDatasourceRepository datasourceRepository;

    @Resource
    private MetadataBusinessEntityRepository entityRepository;

    @Resource
    private MetadataEntityFieldRepository fieldRepository;

    @Resource
    private MetadataEntityRelationshipRepository relationshipRepository;

    @Resource
    private MetadataValidationRuleGroupRepository ruleGroupRepository;

    /**
     * 纯数字正则模式，用于判断标识符是ID还是UUID
     */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");

    // ====================== 单参数自动识别方法（Controller层使用） ======================

    /**
     * 自动解析数据源标识符为Long ID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为Long ID。
     * 判断逻辑：纯数字 → 直接解析为Long ID；非纯数字 → 视为UUID，查库转换
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 数据源Long ID
     * @throws ServiceException 当标识符为空或对应的数据源不存在时抛出异常
     */
    public Long resolveDatasourceId(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("数据源标识符为空");
            throw new ServiceException(DATASOURCE_ID_OR_UUID_REQUIRED);
        }

        // 纯数字 -> 直接作为ID
        if (isNumeric(identifier)) {
            return Long.parseLong(identifier);
        }

        // 非纯数字 -> 视为UUID，查库转换
        MetadataDatasourceDO datasource = datasourceRepository.getDatasourceByUuid(identifier);
        if (datasource == null) {
            log.warn("通过UUID查询数据源失败，数据源不存在: uuid={}", identifier);
            throw new ServiceException(DATASOURCE_NOT_EXISTS);
        }
        log.debug("数据源UUID转ID成功: uuid={} -> id={}", identifier, datasource.getId());
        return datasource.getId();
    }

    /**
     * 自动解析业务实体标识符为Long ID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为Long ID。
     * 判断逻辑：纯数字 → 直接解析为Long ID；非纯数字 → 视为UUID，查库转换
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 实体Long ID
     * @throws ServiceException 当标识符为空或对应的实体不存在时抛出异常
     */
    public Long resolveEntityId(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("业务实体标识符为空");
            throw new ServiceException(ENTITY_ID_OR_UUID_REQUIRED);
        }

        // 纯数字 -> 直接作为ID
        if (isNumeric(identifier)) {
            return Long.parseLong(identifier);
        }

        // 非纯数字 -> 视为UUID，查库转换
        MetadataBusinessEntityDO entity = entityRepository.getByEntityUuid(identifier);
        if (entity == null) {
            log.warn("通过UUID查询业务实体失败，实体不存在: uuid={}", identifier);
            throw new ServiceException(BUSINESS_ENTITY_NOT_EXISTS);
        }
        log.debug("业务实体UUID转ID成功: uuid={} -> id={}", identifier, entity.getId());
        return entity.getId();
    }

    /**
     * 自动解析实体字段标识符为Long ID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为Long ID。
     * 判断逻辑：纯数字 → 直接解析为Long ID；非纯数字 → 视为UUID，查库转换
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 字段Long ID
     * @throws ServiceException 当标识符为空或对应的字段不存在时抛出异常
     */
    public Long resolveFieldId(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("实体字段标识符为空");
            throw new ServiceException(FIELD_ID_OR_UUID_REQUIRED);
        }

        // 纯数字 -> 直接作为ID
        if (isNumeric(identifier)) {
            return Long.parseLong(identifier);
        }

        // 非纯数字 -> 视为UUID，查库转换
        MetadataEntityFieldDO field = fieldRepository.getByFieldUuid(identifier);
        if (field == null) {
            log.warn("通过UUID查询实体字段失败，字段不存在: uuid={}", identifier);
            throw new ServiceException(ENTITY_FIELD_NOT_EXISTS);
        }
        log.debug("实体字段UUID转ID成功: uuid={} -> id={}", identifier, field.getId());
        return field.getId();
    }

    /**
     * 自动解析实体关系标识符为Long ID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为Long ID。
     * 判断逻辑：纯数字 → 直接解析为Long ID；非纯数字 → 视为UUID，查库转换
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 关系Long ID
     * @throws ServiceException 当标识符为空或对应的关系不存在时抛出异常
     */
    public Long resolveRelationshipId(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("实体关系标识符为空");
            throw new ServiceException(ENTITY_RELATIONSHIP_NOT_EXISTS);
        }

        // 纯数字 -> 直接作为ID
        if (isNumeric(identifier)) {
            return Long.parseLong(identifier);
        }

        // 非纯数字 -> 视为UUID，查库转换
        MetadataEntityRelationshipDO relationship = relationshipRepository.findByRelationshipUuid(identifier);
        if (relationship == null) {
            log.warn("通过UUID查询实体关系失败，关系不存在: uuid={}", identifier);
            throw new ServiceException(ENTITY_RELATIONSHIP_NOT_EXISTS);
        }
        log.debug("实体关系UUID转ID成功: uuid={} -> id={}", identifier, relationship.getId());
        return relationship.getId();
    }

    /**
     * 自动解析校验规则组标识符为Long ID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为Long ID。
     * 判断逻辑：纯数字 → 直接解析为Long ID；非纯数字 → 视为UUID，查库转换
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 规则组Long ID
     * @throws ServiceException 当标识符为空或对应的规则组不存在时抛出异常
     */
    public Long resolveRuleGroupId(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("校验规则组标识符为空");
            throw new ServiceException(VALIDATION_RULE_GROUP_NOT_EXISTS);
        }

        // 纯数字 -> 直接作为ID
        if (isNumeric(identifier)) {
            return Long.parseLong(identifier);
        }

        // 非纯数字 -> 视为UUID，查库转换
        MetadataValidationRuleGroupDO ruleGroup = ruleGroupRepository.getByGroupUuid(identifier);
        if (ruleGroup == null) {
            log.warn("通过UUID查询校验规则组失败，规则组不存在: uuid={}", identifier);
            throw new ServiceException(VALIDATION_RULE_GROUP_NOT_EXISTS);
        }
        log.debug("校验规则组UUID转ID成功: uuid={} -> id={}", identifier, ruleGroup.getId());
        return ruleGroup.getId();
    }

    /**
     * 判断字符串是否为纯数字
     *
     * @param str 待判断的字符串
     * @return true-纯数字，false-包含非数字字符
     */
    private boolean isNumeric(String str) {
        return NUMERIC_PATTERN.matcher(str).matches();
    }

    // ====================== 单参数自动识别转UUID方法（Controller层使用） ======================

    /**
     * 自动解析数据源标识符为UUID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为UUID。
     * 判断逻辑：纯数字 → 视为ID，查库转换；非纯数字 → 直接作为UUID返回
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 数据源UUID
     * @throws ServiceException 当标识符为空或对应的数据源不存在时抛出异常
     */
    public String toDatasourceUuid(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("数据源标识符为空");
            throw new ServiceException(DATASOURCE_ID_OR_UUID_REQUIRED);
        }

        // 非纯数字 -> 直接作为UUID
        if (!isNumeric(identifier)) {
            return identifier;
        }

        // 纯数字 -> 视为ID，查库转换
        MetadataDatasourceDO datasource = datasourceRepository.getDatasourceById(identifier);
        if (datasource == null) {
            log.warn("通过ID查询数据源失败，数据源不存在: id={}", identifier);
            throw new ServiceException(DATASOURCE_NOT_EXISTS);
        }
        log.debug("数据源ID转UUID成功: id={} -> uuid={}", identifier, datasource.getDatasourceUuid());
        return datasource.getDatasourceUuid();
    }

    /**
     * 自动解析业务实体标识符为UUID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为UUID。
     * 判断逻辑：纯数字 → 视为ID，查库转换；非纯数字 → 直接作为UUID返回
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 实体UUID
     * @throws ServiceException 当标识符为空或对应的实体不存在时抛出异常
     */
    public String toEntityUuid(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("业务实体标识符为空");
            throw new ServiceException(ENTITY_ID_OR_UUID_REQUIRED);
        }

        // 非纯数字 -> 直接作为UUID
        if (!isNumeric(identifier)) {
            return identifier;
        }

        // 纯数字 -> 视为ID，查库转换
        MetadataBusinessEntityDO entity = entityRepository.getBusinessEntityById(identifier);
        if (entity == null) {
            log.warn("通过ID查询业务实体失败，实体不存在: id={}", identifier);
            throw new ServiceException(BUSINESS_ENTITY_NOT_EXISTS);
        }
        log.debug("业务实体ID转UUID成功: id={} -> uuid={}", identifier, entity.getEntityUuid());
        return entity.getEntityUuid();
    }

    /**
     * 自动解析实体字段标识符为UUID
     * <p>
     * 支持传入Long类型ID的字符串形式或UUID，自动识别并转换为UUID。
     * 判断逻辑：纯数字 → 视为ID，查库转换；非纯数字 → 直接作为UUID返回
     *
     * @param identifier 标识符（可以是Long ID的字符串形式或UUID）
     * @return 字段UUID
     * @throws ServiceException 当标识符为空或对应的字段不存在时抛出异常
     */
    public String toFieldUuid(String identifier) {
        if (CharSequenceUtil.isBlank(identifier)) {
            log.warn("实体字段标识符为空");
            throw new ServiceException(FIELD_ID_OR_UUID_REQUIRED);
        }

        // 非纯数字 -> 直接作为UUID
        if (!isNumeric(identifier)) {
            return identifier;
        }

        // 纯数字 -> 视为ID，查库转换
        MetadataEntityFieldDO field = fieldRepository.findById(Long.valueOf(identifier));
        if (field == null) {
            log.warn("通过ID查询实体字段失败，字段不存在: id={}", identifier);
            throw new ServiceException(ENTITY_FIELD_NOT_EXISTS);
        }
        log.debug("实体字段ID转UUID成功: id={} -> uuid={}", identifier, field.getFieldUuid());
        return field.getFieldUuid();
    }

    // ====================== 双参数方法（VO层使用） ======================

    /**
     * 解析数据源UUID
     * <p>
     * 优先使用uuid参数，若为空则通过id查询转换
     *
     * @param uuid 数据源UUID（优先使用）
     * @param id   数据源ID（兼容旧版，当uuid为空时使用）
     * @return 数据源UUID
     * @throws ServiceException 当uuid和id都为空，或id对应的数据源不存在时抛出异常
     */
    public String resolveDatasourceUuid(String uuid, String id) {
        // 1. UUID非空，直接返回
        if (CharSequenceUtil.isNotBlank(uuid)) {
            return uuid;
        }

        // 2. UUID为空，尝试通过ID查询
        if (CharSequenceUtil.isNotBlank(id)) {
            MetadataDatasourceDO datasource = datasourceRepository.getDatasourceById(id);
            if (datasource == null) {
                log.warn("通过ID查询数据源失败，数据源不存在: id={}", id);
                throw new ServiceException(DATASOURCE_NOT_EXISTS);
            }
            String resolvedUuid = datasource.getDatasourceUuid();
            log.debug("数据源ID转UUID成功: id={} -> uuid={}", id, resolvedUuid);
            return resolvedUuid;
        }

        // 3. 两者都为空，抛出异常
        log.warn("数据源标识为空，datasourceUuid和datasourceId至少需要提供一个");
        throw new ServiceException(DATASOURCE_ID_OR_UUID_REQUIRED);
    }

    /**
     * 解析业务实体UUID
     * <p>
     * 优先使用uuid参数，若为空则通过id查询转换
     *
     * @param uuid 实体UUID（优先使用）
     * @param id   实体ID（兼容旧版，当uuid为空时使用）
     * @return 实体UUID
     * @throws ServiceException 当uuid和id都为空，或id对应的实体不存在时抛出异常
     */
    public String resolveEntityUuid(String uuid, String id) {
        // 1. UUID非空，直接返回
        if (CharSequenceUtil.isNotBlank(uuid)) {
            return uuid;
        }

        // 2. UUID为空，尝试通过ID查询
        if (CharSequenceUtil.isNotBlank(id)) {
            MetadataBusinessEntityDO entity = entityRepository.getBusinessEntityById(id);
            if (entity == null) {
                log.warn("通过ID查询业务实体失败，实体不存在: id={}", id);
                throw new ServiceException(BUSINESS_ENTITY_NOT_EXISTS);
            }
            String resolvedUuid = entity.getEntityUuid();
            log.debug("业务实体ID转UUID成功: id={} -> uuid={}", id, resolvedUuid);
            return resolvedUuid;
        }

        // 3. 两者都为空，抛出异常
        log.warn("业务实体标识为空，entityUuid和entityId至少需要提供一个");
        throw new ServiceException(ENTITY_ID_OR_UUID_REQUIRED);
    }

    /**
     * 解析实体字段UUID
     * <p>
     * 优先使用uuid参数，若为空则通过id查询转换
     *
     * @param uuid 字段UUID（优先使用）
     * @param id   字段ID（兼容旧版，当uuid为空时使用）
     * @return 字段UUID
     * @throws ServiceException 当uuid和id都为空，或id对应的字段不存在时抛出异常
     */
    public String resolveFieldUuid(String uuid, String id) {
        // 1. UUID非空，直接返回
        if (CharSequenceUtil.isNotBlank(uuid)) {
            return uuid;
        }

        // 2. UUID为空，尝试通过ID查询
        if (CharSequenceUtil.isNotBlank(id)) {
            MetadataEntityFieldDO field = fieldRepository.findById(Long.valueOf(id));
            if (field == null) {
                log.warn("通过ID查询实体字段失败，字段不存在: id={}", id);
                throw new ServiceException(ENTITY_FIELD_NOT_EXISTS);
            }
            String resolvedUuid = field.getFieldUuid();
            log.debug("实体字段ID转UUID成功: id={} -> uuid={}", id, resolvedUuid);
            return resolvedUuid;
        }

        // 3. 两者都为空，抛出异常
        log.warn("实体字段标识为空，fieldUuid和fieldId至少需要提供一个");
        throw new ServiceException(FIELD_ID_OR_UUID_REQUIRED);
    }

    /**
     * 解析实体字段UUID（可选字段版本）
     * <p>
     * 与resolveFieldUuid不同，当uuid和id都为空时返回null而不是抛出异常，
     * 适用于selectFieldUuid等可选字段的转换
     *
     * @param uuid 字段UUID（优先使用）
     * @param id   字段ID（兼容旧版，当uuid为空时使用）
     * @return 字段UUID，若都为空则返回null
     * @throws ServiceException 当id非空但对应的字段不存在时抛出异常
     */
    public String resolveFieldUuidOptional(String uuid, String id) {
        // 1. UUID非空，直接返回
        if (CharSequenceUtil.isNotBlank(uuid)) {
            return uuid;
        }

        // 2. UUID为空，尝试通过ID查询
        if (CharSequenceUtil.isNotBlank(id)) {
            MetadataEntityFieldDO field = fieldRepository.findById(Long.valueOf(id));
            if (field == null) {
                log.warn("通过ID查询实体字段失败，字段不存在: id={}", id);
                throw new ServiceException(ENTITY_FIELD_NOT_EXISTS);
            }
            String resolvedUuid = field.getFieldUuid();
            log.debug("实体字段ID转UUID成功: id={} -> uuid={}", id, resolvedUuid);
            return resolvedUuid;
        }

        // 3. 两者都为空，返回null（可选字段允许为空）
        return null;
    }

    /**
     * 解析数据源UUID（可选字段版本）
     * <p>
     * 当uuid和id都为空时返回null而不是抛出异常
     *
     * @param uuid 数据源UUID（优先使用）
     * @param id   数据源ID（兼容旧版，当uuid为空时使用）
     * @return 数据源UUID，若都为空则返回null
     * @throws ServiceException 当id非空但对应的数据源不存在时抛出异常
     */
    public String resolveDatasourceUuidOptional(String uuid, String id) {
        // 1. UUID非空，直接返回
        if (CharSequenceUtil.isNotBlank(uuid)) {
            return uuid;
        }

        // 2. UUID为空，尝试通过ID查询
        if (CharSequenceUtil.isNotBlank(id)) {
            MetadataDatasourceDO datasource = datasourceRepository.getDatasourceById(id);
            if (datasource == null) {
                log.warn("通过ID查询数据源失败，数据源不存在: id={}", id);
                throw new ServiceException(DATASOURCE_NOT_EXISTS);
            }
            String resolvedUuid = datasource.getDatasourceUuid();
            log.debug("数据源ID转UUID成功: id={} -> uuid={}", id, resolvedUuid);
            return resolvedUuid;
        }

        // 3. 两者都为空，返回null（可选字段允许为空）
        return null;
    }

    /**
     * 解析业务实体UUID（可选字段版本）
     * <p>
     * 当uuid和id都为空时返回null而不是抛出异常
     *
     * @param uuid 实体UUID（优先使用）
     * @param id   实体ID（兼容旧版，当uuid为空时使用）
     * @return 实体UUID，若都为空则返回null
     * @throws ServiceException 当id非空但对应的实体不存在时抛出异常
     */
    public String resolveEntityUuidOptional(String uuid, String id) {
        // 1. UUID非空，直接返回
        if (CharSequenceUtil.isNotBlank(uuid)) {
            return uuid;
        }

        // 2. UUID为空，尝试通过ID查询
        if (CharSequenceUtil.isNotBlank(id)) {
            MetadataBusinessEntityDO entity = entityRepository.getBusinessEntityById(id);
            if (entity == null) {
                log.warn("通过ID查询业务实体失败，实体不存在: id={}", id);
                throw new ServiceException(BUSINESS_ENTITY_NOT_EXISTS);
            }
            String resolvedUuid = entity.getEntityUuid();
            log.debug("业务实体ID转UUID成功: id={} -> uuid={}", id, resolvedUuid);
            return resolvedUuid;
        }

        // 3. 两者都为空，返回null（可选字段允许为空）
        return null;
    }
}
