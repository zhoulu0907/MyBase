package com.cmsr.onebase.module.metadata.service.datamethod.engine;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DynamicDataGetReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DynamicDataPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataSystemMethodService;
import com.cmsr.onebase.module.metadata.service.datamethod.dto.QueryPlanDTO;
import com.cmsr.onebase.module.metadata.service.datamethod.dto.JoinTableDTO;
import com.cmsr.onebase.module.metadata.service.datamethod.dto.JoinOnDTO;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 最小可用多表读引擎：两阶段分页 + 内存关联
 */
@Component
@Slf4j
public class MultiTableQueryEngine {

    @Resource
    private MetadataDatasourceService metadataDatasourceService;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    private MetadataBusinessEntityService metadataBusinessEntityService;
    @Resource
    private MetadataEntityFieldService metadataEntityFieldService;

    private final ObjectMapper mapper = new ObjectMapper();
    @Resource
    private com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataMethodExecutionLogService execLogService;
    @Resource
    private MetadataDataSystemMethodService metadataDataSystemMethodService;

    public DynamicDataRespVO queryOne(String methodCode, String planJson, DynamicDataGetReqVO reqVO) {
        return TenantUtils.executeIgnore(() -> doQueryOne(methodCode, planJson, reqVO));
    }

    public PageResult<DynamicDataRespVO> queryPage(String methodCode, String planJson, DynamicDataPageReqVO reqVO) {
        return TenantUtils.executeIgnore(() -> doQueryPage(methodCode, planJson, reqVO));
    }

    private DynamicDataRespVO doQueryOne(String methodCode, String planJson, DynamicDataGetReqVO reqVO) {
    long begin = System.currentTimeMillis();
    try {
            QueryPlanDTO plan = mapper.readValue(planJson, QueryPlanDTO.class);
            // 主表与字段
            MetadataBusinessEntityDO entity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(reqVO.getEntityId()));
            List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(reqVO.getEntityId());

            AnylineService<?> primarySvc = getServiceByCode(plan.getPrimary().getDatasource());
            String table = plan.getPrimary().getTable();
            // String pk = plan.getPrimary().getPk(); // 当前最小实现暂未使用

            DefaultConfigStore cs = new DefaultConfigStore();
            cs.and(Compare.EQUAL, plan.getPrimary().getPk(), reqVO.getId());
            DataSet ds = primarySvc.querys(quoteTableName(table), cs);
            if (ds.isEmpty()) {
                return buildResp(entity, Collections.emptyMap(), fields);
            }
            DataRow main = ds.getRow(0);
            Map<String, Object> mainMap = toMap(main);

            // Joins
            Map<String, Object> assembled = applyJoins(plan, mainMap, Collections.singletonList(mainMap));

            // 构建响应（仍使用实体名称等）
            DynamicDataRespVO resp = buildResp(entity, assembled, fields);
            // 执行日志
            try { recordLog(methodCode, planJson, "GET", System.currentTimeMillis() - begin, 1L, true, null); } catch (Exception ignore) {}
            return resp;
        } catch (Exception e) {
            log.error("queryOne multi-table failed", e);
            try { recordLog(methodCode, planJson, "GET", System.currentTimeMillis() - begin, 0L, false, e.getMessage()); } catch (Exception ignore) {}
            throw new RuntimeException(e);
        }
    }

    private PageResult<DynamicDataRespVO> doQueryPage(String methodCode, String planJson, DynamicDataPageReqVO reqVO) {
    long begin = System.currentTimeMillis();
    try {
            QueryPlanDTO plan = mapper.readValue(planJson, QueryPlanDTO.class);
            // 主表与字段
            MetadataBusinessEntityDO entity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(reqVO.getEntityId()));
            List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(reqVO.getEntityId());

            AnylineService<?> primarySvc = getServiceByCode(plan.getPrimary().getDatasource());
            String table = plan.getPrimary().getTable();
            // String pk = plan.getPrimary().getPk(); // 当前分页实现未直接使用主键变量

            // 统计
            ConfigStore countCs = buildPrimaryFilter(plan, reqVO.getFilters());
            long total = primarySvc.count(quoteTableName(table), countCs);

            // 分页
            DefaultConfigStore pageCs = (DefaultConfigStore) buildPrimaryFilter(plan, reqVO.getFilters());
            int offset = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
            pageCs.scope(offset, reqVO.getPageSize());
            DataSet pageDs = primarySvc.querys(quoteTableName(table), pageCs);
            List<Map<String, Object>> mains = new ArrayList<>();
            for (int i = 0; i < pageDs.size(); i++) {
                mains.add(toMap(pageDs.getRow(i)));
            }

            // Stage B/C: 关联与装配
            List<DynamicDataRespVO> rows = new ArrayList<>();
            if (!mains.isEmpty()) {
                for (Map<String, Object> main : mains) {
                    Map<String, Object> assembled = applyJoins(plan, main, mains);
                    rows.add(buildResp(entity, assembled, fields));
                }
            }
        PageResult<DynamicDataRespVO> page = new PageResult<>(rows, total);
        try { recordLog(methodCode, planJson, "PAGE", System.currentTimeMillis() - begin, (long) rows.size(), true, null); } catch (Exception ignore) {}
        return page;
        } catch (Exception e) {
            log.error("queryPage multi-table failed", e);
        try { recordLog(methodCode, planJson, "PAGE", System.currentTimeMillis() - begin, 0L, false, e.getMessage()); } catch (Exception ignore) {}
            throw new RuntimeException(e);
        }
    }

    private void recordLog(String methodCode, String planJson, String op,
                           long costMs, Long rows, boolean success, String error) {
    com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataMethodExecutionLogDO logDO =
        new com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataMethodExecutionLogDO();
    try {
        com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO method =
            metadataDataSystemMethodService.getDataMethodByCode(methodCode);
        if (method != null) {
            logDO.setMethodId(method.getId());
        }
    } catch (Exception ignore) {}
        // 将简要信息写入已有字段
        logDO.setRequestParams("{\"op\":\"" + op + "\",\"plan\":" + quote(planJson) + ",\"rows\":" + rows + "}");
        logDO.setDurationMs((int) Math.min(costMs, Integer.MAX_VALUE));
        logDO.setStatus(success ? "SUCCESS" : "FAILED");
        logDO.setErrorMsg(error);
    execLogService.record(logDO);
    }

    private String quote(String s) {
        if (s == null) return null;
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    private ConfigStore buildPrimaryFilter(QueryPlanDTO plan, Map<String, Object> filters) {
        DefaultConfigStore cs = new DefaultConfigStore();
        if (filters != null) {
            for (Map.Entry<String, Object> en : filters.entrySet()) {
                if (en.getValue() != null) {
                    cs.and(Compare.EQUAL, en.getKey(), en.getValue());
                }
            }
        }
        // 软删除约定
    // 简化：添加软删除条件；若表无此列，底层可能忽略或由上层计划规避
    cs.and(Compare.EQUAL, "deleted", "0");
        return cs;
    }

    private Map<String, Object> applyJoins(QueryPlanDTO plan, Map<String, Object> main, List<Map<String, Object>> pageMains) {
        Map<String, Object> result = new LinkedHashMap<>(main);
        if (plan.getJoins() == null || plan.getJoins().isEmpty()) {
            return result;
        }
        for (JoinTableDTO j : plan.getJoins()) {
            try {
                AnylineService<?> svc = getServiceByCode(j.getDatasource());
                String rightCol = rightColName(j.getOn());
                String leftCol = leftColName(j.getOn());
                // 收集当前页所有left值减少N+1
                Set<Object> keys = pageMains.stream()
                        .map(m -> m.get(simpleField(leftCol)))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                if (keys.isEmpty()) {
                    result.put(j.getAlias(), j.isMany() ? Collections.emptyList() : null);
                    continue;
                }
                DefaultConfigStore cs = new DefaultConfigStore();
                cs.in(simpleField(rightCol), keys);
                cs.and(Compare.EQUAL, "deleted", "0");
                DataSet ds = svc.querys(quoteTableName(j.getTable()), cs);
                // 构建右值 -> 行(列表)映射
                Map<Object, List<Map<String, Object>>> idx = new HashMap<>();
                for (int i = 0; i < ds.size(); i++) {
                    Map<String, Object> map = toMap(ds.getRow(i));
                    Object key = map.get(simpleField(rightCol));
                    idx.computeIfAbsent(key, k -> new ArrayList<>()).add(map);
                }
                Object leftVal = main.get(simpleField(leftCol));
                List<Map<String, Object>> matched = idx.getOrDefault(leftVal, Collections.emptyList());
                if (j.isMany()) {
                    result.put(j.getAlias(), matched);
                } else {
                    result.put(j.getAlias(), matched.isEmpty() ? null : matched.get(0));
                }
            } catch (Exception ex) {
                log.warn("join failed for alias={}", j.getAlias(), ex);
                result.put(j.getAlias(), j.isMany() ? Collections.emptyList() : null);
            }
        }
        return result;
    }

    private AnylineService<?> getServiceByCode(String code) {
        MetadataDatasourceDO ds = metadataDatasourceService.getDatasourceByCode(code);
        return temporaryDatasourceService.createTemporaryService(ds);
    }

    private Map<String, Object> toMap(DataRow row) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : row.keys()) {
            Object v = row.get(key);
            if (v != null) map.put(key, v);
        }
        return map;
    }

    private DynamicDataRespVO buildResp(MetadataBusinessEntityDO entity, Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        DynamicDataRespVO vo = new DynamicDataRespVO();
        vo.setEntityId(String.valueOf(entity.getId()));
        vo.setEntityName(entity.getDisplayName());
        vo.setData(data);
        Map<String, String> types = new HashMap<>();
        for (MetadataEntityFieldDO f : fields) {
            types.put(f.getFieldName(), f.getFieldType());
        }
        vo.setFieldType(types);
        return vo;
    }

    private String rightColName(JoinOnDTO on) { return on.getRight(); }
    private String leftColName(JoinOnDTO on) { return on.getLeft(); }

    private String simpleField(String qualified) {
        int idx = qualified.lastIndexOf('.') ;
        return idx >= 0 ? qualified.substring(idx + 1) : qualified;
    }

    /**
     * 为表名添加引号以支持PostgreSQL中大小写混合的表名
     *
     * @param tableName 原始表名
     * @return 添加引号后的表名
     */
    private String quoteTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return tableName;
        }
        // 如果表名已经有引号，直接返回
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
            return tableName;
        }
        // 为表名添加双引号
        return "\"" + tableName + "\"";
    }
}
