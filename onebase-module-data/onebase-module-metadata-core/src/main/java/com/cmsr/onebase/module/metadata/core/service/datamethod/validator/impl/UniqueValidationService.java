package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

/**
 * 唯一性校验服务
 * 
 * 校验字段值是否唯一
 *
 */
@Component
public class UniqueValidationService implements ValidationService {

    private static final Logger log = Logger.getLogger(UniqueValidationService.class.getName());

    private final MetadataValidationUniqueRepository uniqueRepository;

    @Resource
    protected MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Resource
    protected MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Resource
    protected TemporaryDatasourceService temporaryDatasourceService;

    @Resource
    protected MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    public UniqueValidationService(MetadataValidationUniqueRepository uniqueRepository) {
        this.uniqueRepository = uniqueRepository;
    }

    @Override
    public void validate(String entityUuid, String fieldUuid, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {
        if (value == null) {
            return; // 空值不校验唯一性
        }

        // 查询唯一性规则（使用字段UUID）
        List<MetadataValidationUniqueDO> rules = uniqueRepository.findByFieldUuid(fieldUuid);
        
        if (rules.isEmpty()) {
            return; // 没有唯一性规则，跳过校验
        }

        // 检查是否有启用的唯一性规则
        boolean hasEnabledRule = rules.stream()
                .anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);

        if (!hasEnabledRule) {
            return; // 没有启用的唯一性规则
        }

        // 执行唯一性校验
        for (MetadataValidationUniqueDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) {
                continue; // 跳过未启用的规则
            }

            // TODO: 实现数据库唯一性检查
            // 这里需要根据 rule.getUniqueScope() 来确定检查范围
            // 可能需要调用数据库查询来检查是否已存在相同的值

            log.fine("执行唯一性校验：entityUuid=" + entityUuid + ", fieldUuid=" + fieldUuid + ", value=" + value);
            boolean isExist = validateDateExistByField(entityUuid, field, value, data);// 查询数据库检查唯一性
            if (isExist) {
                throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]值已存在");// 如果发现重复值，抛出异常
            }

        }
    }

    @Override
    public String getValidationType() {
        return "UNIQUE";
    }

    @Override
    public boolean supports(String fieldType) {
        // 唯一性校验支持所有字段类型
        return true;
    }

    /**
     *根据字段值在业务表中查询是否已经存在相同的值
     */
    private boolean validateDateExistByField(String entityUuid, MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(entityUuid);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasourceByUuid(entity.getDatasourceUuid());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：" + datasource.getCode());
        ConfigStore configs = new DefaultConfigStore();
        configs.and(field.getFieldName(), value);

        List<MetadataEntityFieldDO> entityFields = metadataEntityFieldCoreService.getEntityFieldListByEntityUuid(entityUuid);

        // 仅校验未被逻辑删除的数据
        boolean hasDeletedColumn = entityFields.stream()
                .anyMatch(item -> "deleted".equalsIgnoreCase(item.getFieldName()));
        if (hasDeletedColumn) {
            configs.and("deleted", 0);
        }

        // 排除当前记录（用于更新场景）
        Optional<String> primaryKeyField = entityFields.stream()
                .filter(item -> item.getIsPrimaryKey() != null && item.getIsPrimaryKey() == 1)
                .map(MetadataEntityFieldDO::getFieldName)
                .findFirst();
        Object currentId = null;
        if (data != null) {
            if (primaryKeyField.isPresent()) {
                currentId = data.get(primaryKeyField.get());
            }
            if (currentId == null) {
                currentId = data.get("id");
            }
        }
        if (currentId != null) {
            configs.and(Compare.NOT_EQUAL, primaryKeyField.orElse("id"), currentId);
        }

        DataSet dataSet = temporaryService.querys(quoteTableName(entity.getTableName()), configs);
        return !dataSet.isEmpty();
    }

    /**
     * 为表名添加引号以支持PostgreSQL中大小写混合的表名
     */
    private String quoteTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return tableName;
        }
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
            return tableName;
        }
        return "\"" + tableName + "\"";
    }
}
