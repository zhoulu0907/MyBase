package com.cmsr.onebase.module.metadata.service.datamethod.engine;

import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataCompensationLogDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataMethodExecutionLogDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataOutboxDO;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.service.datamethod.CompensationLogService;
import com.cmsr.onebase.module.metadata.service.datamethod.OutboxService;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataMethodExecutionLogService;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataSystemMethodService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 多表写入引擎（最小实现）：
 * - 约定主表写由现有 Service 执行；本引擎在主表成功后处理子表写入/更新/删除。
 * - 支持跨数据源：按步骤创建临时 AnylineService 写入。
 * - 软删除：按计划中的 softDelete 与 deletedColumn 控制，否则执行物理删除。
 *
 * @author bty418
 * @date 2025-08-22
 */
@Component
@Slf4j
public class MultiTableWriteEngine {

    @Resource
    private MetadataDatasourceService metadataDatasourceService;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    private MetadataBusinessEntityService metadataBusinessEntityService;
    @Resource
    private MetadataEntityFieldService metadataEntityFieldService;
    @Resource
    private MetadataDataMethodExecutionLogService execLogService;
    @Resource
    private OutboxService outboxService;
    @Resource
    private CompensationLogService compensationLogService;
    @Resource
    private MetadataDataSystemMethodService metadataDataSystemMethodService;

    private final ObjectMapper mapper = new ObjectMapper();

    public void afterPrimaryCreate(String methodCode, String planJson,
                                   MetadataBusinessEntityDO entity,
                                   Object primaryPk,
                                   Map<String, Object> requestData) {
        long begin = System.currentTimeMillis();
        boolean success = true;
        String error = null;
        Set<String> dsSet = new LinkedHashSet<>();
        try {
            WritePlan plan = mapper.readValue(planJson, WritePlan.class);
            if (plan.getPrimary() != null && plan.getPrimary().getDatasource() != null) {
                dsSet.add(plan.getPrimary().getDatasource());
            }
            if (plan.getChildren() == null || plan.getChildren().isEmpty()) return;
            for (Child c : plan.getChildren()) {
                if (c.getDatasource() != null) dsSet.add(c.getDatasource());
                Object childData = requestData.get(c.getAlias());
                if (childData == null) continue;
                if (Boolean.TRUE.equals(c.getMany()) && childData instanceof List) {
                    @SuppressWarnings("unchecked") List<Map<String, Object>> list = (List<Map<String, Object>>) childData;
                    batchInsertWithOutbox(methodCode, plan, c, list, primaryPk);
                } else if (childData instanceof Map) {
                    @SuppressWarnings("unchecked") Map<String, Object> one = (Map<String, Object>) childData;
                    batchInsertWithOutbox(methodCode, plan, c, Collections.singletonList(one), primaryPk);
                } else {
                    log.warn("write-engine afterCreate alias={} unexpected data type: {}", c.getAlias(), childData.getClass());
                }
            }
        } catch (Exception e) {
            success = false;
            error = e.getMessage();
            log.error("afterPrimaryCreate write-engine failed", e);
            throw new RuntimeException(e);
        } finally {
            try { recordLog(methodCode, planJson, "WRITE_CREATE", System.currentTimeMillis() - begin, 1L, success, error, dsSet); } catch (Exception ignore) {}
        }
    }

    public void afterPrimaryUpdate(String methodCode, String planJson,
                                   MetadataBusinessEntityDO entity,
                                   Object primaryPk,
                                   Map<String, Object> requestData) {
        long begin = System.currentTimeMillis();
        boolean success = true;
        String error = null;
        Set<String> dsSet = new LinkedHashSet<>();
        try {
            WritePlan plan = mapper.readValue(planJson, WritePlan.class);
            if (plan.getPrimary() != null && plan.getPrimary().getDatasource() != null) {
                dsSet.add(plan.getPrimary().getDatasource());
            }
            if (plan.getChildren() == null || plan.getChildren().isEmpty()) return;
            for (Child c : plan.getChildren()) {
                if (c.getDatasource() != null) dsSet.add(c.getDatasource());
                if (!Boolean.TRUE.equals(c.getReplaceOnUpdate())) continue;
                deleteByFkWithOutbox(methodCode, plan, c, primaryPk);
                Object childData = requestData.get(c.getAlias());
                if (childData == null) continue;
                if (Boolean.TRUE.equals(c.getMany()) && childData instanceof List) {
                    @SuppressWarnings("unchecked") List<Map<String, Object>> list = (List<Map<String, Object>>) childData;
                    batchInsertWithOutbox(methodCode, plan, c, list, primaryPk);
                } else if (childData instanceof Map) {
                    @SuppressWarnings("unchecked") Map<String, Object> one = (Map<String, Object>) childData;
                    batchInsertWithOutbox(methodCode, plan, c, Collections.singletonList(one), primaryPk);
                }
            }
        } catch (Exception e) {
            success = false;
            error = e.getMessage();
            log.error("afterPrimaryUpdate write-engine failed", e);
            throw new RuntimeException(e);
        } finally {
            try { recordLog(methodCode, planJson, "WRITE_UPDATE", System.currentTimeMillis() - begin, 1L, success, error, dsSet); } catch (Exception ignore) {}
        }
    }

    public void afterPrimaryDelete(String methodCode, String planJson,
                                   MetadataBusinessEntityDO entity,
                                   Object primaryPk) {
        long begin = System.currentTimeMillis();
        boolean success = true;
        String error = null;
        Set<String> dsSet = new LinkedHashSet<>();
        try {
            WritePlan plan = mapper.readValue(planJson, WritePlan.class);
            if (plan.getPrimary() != null && plan.getPrimary().getDatasource() != null) {
                dsSet.add(plan.getPrimary().getDatasource());
            }
            if (plan.getChildren() == null || plan.getChildren().isEmpty()) return;
            for (Child c : plan.getChildren()) {
                if (c.getDatasource() != null) dsSet.add(c.getDatasource());
                deleteByFkWithOutbox(methodCode, plan, c, primaryPk);
            }
        } catch (Exception e) {
            success = false;
            error = e.getMessage();
            log.error("afterPrimaryDelete write-engine failed", e);
            throw new RuntimeException(e);
        } finally {
            try { recordLog(methodCode, planJson, "WRITE_DELETE", System.currentTimeMillis() - begin, 1L, success, error, dsSet); } catch (Exception ignore) {}
        }
    }

    private void batchInsertWithOutbox(String methodCode, WritePlan plan, Child c, List<Map<String, Object>> list, Object primaryPk) {
        AnylineService<?> svc = getServiceByCode(c.getDatasource());
        for (Map<String, Object> item : list) {
            item.put(c.getFk(), primaryPk);
            DataRow row = new DataRow(item);
            boolean ok = true;
            String err = null;
            try {
                svc.insert(c.getTable(), row);
            } catch (Exception ex) {
                ok = false;
                err = ex.getMessage();
                log.warn("child insert failed alias={} table={}", c.getAlias(), c.getTable(), ex);
            }
            // outbox
            try {
                MetadataOutboxDO outbox = new MetadataOutboxDO();
                outbox.setAggregateType("DATA_METHOD");
                try {
                    com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO method =
                            metadataDataSystemMethodService.getDataMethodByCode(methodCode);
                    if (method != null) {
                        outbox.setAggregateId(String.valueOf(method.getId()));
                    } else {
                        outbox.setAggregateId(methodCode);
                    }
                } catch (Exception ignore) { outbox.setAggregateId(methodCode); }
                outbox.setAction("CHILD_CREATE");
                outbox.setPayload(toJsonSafe(Map.of(
                        "child", c.getAlias(),
                        "table", c.getTable(),
                        "datasource", c.getDatasource(),
                        "fk", c.getFk(),
                        "primaryPk", String.valueOf(primaryPk),
                        "data", item,
                        "success", ok,
                        "error", err
                )));
                outbox.setState(ok ? "DONE" : "FAILED");
                outbox.setRetries(0);
                if (ok) {
                    outbox.setProcessedAt(java.time.LocalDateTime.now());
                } else {
                    outbox.setNextRetryAt(java.time.LocalDateTime.now().plusMinutes(5));
                }
                outboxService.append(outbox);
                if (!ok) {
                    MetadataCompensationLogDO comp = new MetadataCompensationLogDO();
                    comp.setCompensationAction("CHILD_CREATE_COMPENSATE");
                    comp.setPayload(outbox.getPayload());
                    comp.setStatus("PENDING");
                    comp.setErrorMsg(err);
                    compensationLogService.append(comp);
                }
            } catch (Exception ignore) {}
            if (!ok) {
                throw new RuntimeException(err);
            }
        }
    }

    private void deleteByFkWithOutbox(String methodCode, WritePlan plan, Child c, Object primaryPk) {
        AnylineService<?> svc = getServiceByCode(c.getDatasource());
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(Compare.EQUAL, c.getFk(), primaryPk);
        boolean ok = true;
        String err = null;
        try {
            if (Boolean.TRUE.equals(c.getSoftDelete()) && c.getDeletedColumn() != null && !c.getDeletedColumn().isEmpty()) {
                DataRow update = new DataRow();
                update.put(c.getDeletedColumn(), String.valueOf(System.currentTimeMillis()));
                svc.update(c.getTable(), update, cs);
            } else {
                svc.delete(c.getTable(), cs);
            }
        } catch (Exception ex) {
            ok = false;
            err = ex.getMessage();
            log.warn("child delete failed alias={} table={}", c.getAlias(), c.getTable(), ex);
        }
        try {
            MetadataOutboxDO outbox = new MetadataOutboxDO();
            outbox.setAggregateType("DATA_METHOD");
            try {
                com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO method =
                        metadataDataSystemMethodService.getDataMethodByCode(methodCode);
                if (method != null) {
                    outbox.setAggregateId(String.valueOf(method.getId()));
                } else {
                    outbox.setAggregateId(methodCode);
                }
            } catch (Exception ignore) { outbox.setAggregateId(methodCode); }
            outbox.setAction("CHILD_DELETE");
            outbox.setPayload(toJsonSafe(Map.of(
                    "child", c.getAlias(),
                    "table", c.getTable(),
                    "datasource", c.getDatasource(),
                    "fk", c.getFk(),
                    "primaryPk", String.valueOf(primaryPk),
                    "softDelete", Boolean.TRUE.equals(c.getSoftDelete()),
                    "deletedColumn", c.getDeletedColumn(),
                    "success", ok,
                    "error", err
            )));
            outbox.setState(ok ? "DONE" : "FAILED");
            outbox.setRetries(0);
            if (ok) {
                outbox.setProcessedAt(java.time.LocalDateTime.now());
            } else {
                outbox.setNextRetryAt(java.time.LocalDateTime.now().plusMinutes(5));
            }
            outboxService.append(outbox);
            if (!ok) {
                MetadataCompensationLogDO comp = new MetadataCompensationLogDO();
                comp.setCompensationAction("CHILD_DELETE_COMPENSATE");
                comp.setPayload(outbox.getPayload());
                comp.setStatus("PENDING");
                comp.setErrorMsg(err);
                compensationLogService.append(comp);
            }
        } catch (Exception ignore) {}
        if (!ok) {
            throw new RuntimeException(err);
        }
    }

    private AnylineService<?> getServiceByCode(String code) {
        MetadataDatasourceDO ds = metadataDatasourceService.getDatasourceByCode(code);
        return temporaryDatasourceService.createTemporaryService(ds);
    }

    private void recordLog(String methodCode, String planJson, String op,
                           long costMs, Long rows, boolean success, String error, Set<String> dataSources) {
        try {
            MetadataDataMethodExecutionLogDO logDO = new MetadataDataMethodExecutionLogDO();
            try {
                com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO method =
                        metadataDataSystemMethodService.getDataMethodByCode(methodCode);
                if (method != null) {
                    logDO.setMethodId(method.getId());
                }
            } catch (Exception ignore) {}
            logDO.setRequestParams("{\"op\":\"" + op + "\",\"plan\":" + quote(planJson) + ",\"rows\":" + rows + "}");
            logDO.setDurationMs((int) Math.min(costMs, Integer.MAX_VALUE));
            logDO.setStatus(success ? "SUCCESS" : "FAILED");
            logDO.setErrorMsg(error);
            if (dataSources != null && !dataSources.isEmpty()) {
                logDO.setDataSources(toJsonSafe(dataSources));
            }
            execLogService.record(logDO);
        } catch (Exception ignore) {}
    }

    private String toJsonSafe(Object o) {
        try { return mapper.writeValueAsString(o); } catch (Exception e) { return "null"; }
    }

    private String quote(String s) {
        if (s == null) return null;
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WritePlan {
        private Primary primary;
        private List<Child> children;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Primary {
        private String datasource;
        private String table;
        private String alias;
        private String pk;
        private Boolean softDelete;
        private String deletedColumn;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Child {
        private String datasource;
        private String table;
        private String alias;
        private Boolean many;
        private String fk;
        private Boolean replaceOnUpdate;
        private Boolean softDelete;
        private String deletedColumn;
    }
}
