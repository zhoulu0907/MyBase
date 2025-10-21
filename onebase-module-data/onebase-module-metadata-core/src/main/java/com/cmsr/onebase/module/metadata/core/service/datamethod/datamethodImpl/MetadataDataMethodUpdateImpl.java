package com.cmsr.onebase.module.metadata.core.service.datamethod.datamethodImpl;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.AbstractMetadataDataMethodCoreService;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_NOT_EXISTS;
@Slf4j
@Component
public class MetadataDataMethodUpdateImpl extends AbstractMetadataDataMethodCoreService {


    /**
     * 校验更新数据
     */
    protected void validateDataIntegrity(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 更新时不校验必填，只校验数据类型等
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            if (field == null) {
                throw exception(ENTITY_FIELD_NOT_EXISTS, fieldName);
            }

            // 不允许更新主键字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsPrimaryKey())) {
                throw invalidParamException("不允许更新主键字段");
            }
        }
    }

    /**
     * 处理更新数据
     */
    protected Map<String, Object> processDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> processedData = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            // 只处理非主键和非系统字段 - 使用新的枚举值：1-是，0-否
            if (field != null &&
                    !BooleanStatusEnum.isYes(field.getIsPrimaryKey()) &&
                    !BooleanStatusEnum.isYes(field.getIsSystemField())) {
                processedData.put(fieldName, entry.getValue());
            }
        }

        return processedData;
    }

    /**
     * 根据id校验待更新的数据记录是否存在
     * @param context
     */
    @Override
    protected void checkDataExistence(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();

        // 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        TenantUtils.executeIgnore(() -> {
            // 校验数据存在
            Object id = context.getId();
            validateDataExistsWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);
        });

    }

    /**
     * 针对修改后的数据进行存储
     * @param context
     */
    protected void storeData(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        Map<String, Object> processedData = context.getProcessedData();

        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        // 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
        TenantUtils.executeIgnore(() -> {

            // 获取主键字段名
            String primaryKeyField = getPrimaryKeyFieldName(fields);

            // 构建更新条件
            DefaultConfigStore configStore = new DefaultConfigStore();
            Object id = context.getId();
            configStore.and(primaryKeyField, id);

            // 执行更新
            DataRow dataRow = new DataRow(processedData);
            long updateCount = temporaryService.update(quoteTableName(entity.getTableName()), dataRow, configStore);
            log.info("更新数据成功，实体ID: {}, 表名: {}, 更新记录数: {}", entityId, entity.getTableName(), updateCount);

            // 查询更新后的完整数据
            Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);

            // 构建响应（移除多表写入逻辑，直接返回结果）
            return buildDataResponse(entity, resultData, fields);

        });


//        TenantUtils.executeIgnore(() -> {
//            // 8. 获取主键字段名
//            String primaryKeyField = getPrimaryKeyFieldName(fields);
//
//            // 9. 构建更新条件
//            DefaultConfigStore configStore = new DefaultConfigStore();
//            configStore.and(primaryKeyField, id);
//
//            // 10. 执行更新
//            DataRow dataRow = new DataRow(processedData);
//            long updateCount = temporaryService.update(quoteTableName(entity.getTableName()), dataRow, configStore);
//            log.info("更新数据成功，实体ID: {}, 表名: {}, 更新记录数: {}", entityId, entity.getTableName(), updateCount);
//
//    });
    }

    /**
     * 结果进行格式化
     * @param context
     * @return
     */
    @Override
    protected Map<String, Object> formatResult(ProcessContext context) {
        Map<String, Object> processedData = context.getProcessedData();

        MetadataBusinessEntityDO entity = context.getEntity();
        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();

        return TenantUtils.executeIgnore(() -> {
            Object primaryKeyValue = context.getId();
            // 确保主键值不为null
            if (primaryKeyValue == null) {
                log.warn("无法获取主键值，跳过查询插入后的数据，实体ID: {}, 表名: {}", entityId, entity.getTableName());
                // 返回插入的数据
                return buildDataResponse(entity, processedData, fields);
            }

            Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), primaryKeyValue, fields);
            // 构建响应（移除多表写入逻辑，直接返回结果）
            return buildDataResponse(entity, resultData, fields);

        }); // TenantUtils.executeIgnore 闭合
    }
}
