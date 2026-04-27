package com.cmsr.onebase.module.metadata.core.service.datamethod.engine;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataSystemMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.dto.QueryPlanDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

/**
 * 多表查询引擎 - 使用基础数据类型，不依赖VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Component
@Slf4j
public class MultiTableQueryEngine {

    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    @Resource
    private MetadataDataSystemMethodCoreService metadataDataSystemMethodService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 执行单条查询
     *
     * @param methodCode 方法编码
     * @param planJson 查询计划JSON
     * @param entityId 实体ID
     * @param id 数据ID
     * @param extraMethodCode 额外方法编码
     * @return 查询结果Map
     */
    public Map<String, Object> queryOne(String methodCode, String planJson, String entityId, Object id, String extraMethodCode) {
        try {
            // 解析查询计划
            QueryPlanDTO plan = objectMapper.readValue(planJson, QueryPlanDTO.class);

            // 构建查询参数Map
            Map<String, Object> params = new HashMap<>();
            params.put("entityId", entityId);
            params.put("id", id);
            params.put("methodCode", extraMethodCode);

            // 执行查询
            List<Map<String, Object>> results = executeQuery(plan, params, 1, 1);

            if (results.isEmpty()) {
                throw exception(BUSINESS_ENTITY_NOT_EXISTS);
            }

            return results.get(0);

        } catch (Exception e) {
            log.error("多表单条查询失败，方法编码: {}, 实体ID: {}, 数据ID: {}", methodCode, entityId, id, e);
            throw new RuntimeException("多表查询失败", e);
        }
    }

    /**
     * 执行分页查询
     *
     * @param methodCode 方法编码
     * @param planJson 查询计划JSON
     * @param entityId 实体ID
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param filters 过滤条件
     * @param extraMethodCode 额外方法编码
     * @return 分页查询结果
     */
    public PageResult<Map<String, Object>> queryPage(String methodCode, String planJson, String entityId,
                                                     Integer pageNo, Integer pageSize, String sortField,
                                                     String sortDirection, Map<String, Object> filters, String extraMethodCode) {
        try {
            // 解析查询计划
            QueryPlanDTO plan = objectMapper.readValue(planJson, QueryPlanDTO.class);

            // 构建查询参数Map
            Map<String, Object> params = new HashMap<>();
            params.put("entityId", entityId);
            params.put("pageNo", pageNo);
            params.put("pageSize", pageSize);
            params.put("sortField", sortField);
            params.put("sortDirection", sortDirection);
            params.put("filters", filters);
            params.put("methodCode", extraMethodCode);

            // 执行查询
            List<Map<String, Object>> results = executeQuery(plan, params, pageNo, pageSize);

            // TODO: 实现总数查询
            long total = results.size(); // 临时简化

            return new PageResult<>(results, total);

        } catch (Exception e) {
            log.error("多表分页查询失败，方法编码: {}, 实体ID: {}", methodCode, entityId, e);
            throw new RuntimeException("多表分页查询失败", e);
        }
    }

    /**
     * 执行查询的核心逻辑
     */
    private List<Map<String, Object>> executeQuery(QueryPlanDTO plan, Map<String, Object> params, Integer pageNo, Integer pageSize) {
        // TODO: 这里是简化实现，实际需要根据查询计划构建复杂的多表查询SQL

        // 获取主实体信息
        String entityId = (String) params.get("entityId");
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(Long.valueOf(entityId));
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 获取数据源
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasourceByUuid(entity.getDatasourceUuid());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);

        return TenantUtils.executeIgnore(() -> {
            // 简化查询，只查询主表数据
            ConfigStore configs = new DefaultConfigStore();

            // 添加ID条件（如果存在）
            Object id = params.get("id");
            if (id != null) {
                configs.and("id", id);
            }

            // 添加过滤条件
            @SuppressWarnings("unchecked")
            Map<String, Object> filters = (Map<String, Object>) params.get("filters");
            if (filters != null && !filters.isEmpty()) {
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    if (fieldValue != null && !"deleted".equalsIgnoreCase(fieldName) && !"tenant_id".equalsIgnoreCase(fieldName)) {
                        configs.and(Compare.EQUAL, fieldName, fieldValue);
                    }
                }
            }

            // 添加软删除条件
            configs.and(Compare.EQUAL, "deleted", 0);

            // 添加排序
            String sortField = (String) params.get("sortField");
            String sortDirection = (String) params.get("sortDirection");
            if (StringUtils.hasText(sortField)) {
                String orderClause = sortField;
                orderClause += "desc".equalsIgnoreCase(sortDirection) ? " DESC" : " ASC";
                configs.order(orderClause);
            } else {
                configs.order("id DESC");
            }

            // 添加分页
            if (pageNo != null && pageSize != null) {
                int offset = (pageNo - 1) * pageSize;
                configs.scope(offset, pageSize);
            }

            // 执行查询
            DataSet dataSet = temporaryService.querys(quoteTableName(entity.getTableName()), configs);

            // 转换结果
            List<Map<String, Object>> results = new ArrayList<>();
            for (int i = 0; i < dataSet.size(); i++) {
                DataRow row = dataSet.getRow(i);
                Map<String, Object> data = new HashMap<>();

                // 转换DataRow为Map
                for (String key : row.keys()) {
                    data.put(key, row.get(key));
                }

                // 构建响应格式
                Map<String, Object> response = new HashMap<>();
                response.put("entityId", entityId);
                response.put("entityName", entity.getDisplayName());
                response.put("data", data);
                response.put("fieldType", new HashMap<>());

                results.add(response);
            }

            return results;
        });
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
