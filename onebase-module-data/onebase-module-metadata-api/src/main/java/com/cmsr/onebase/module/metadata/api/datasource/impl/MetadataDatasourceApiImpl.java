package com.cmsr.onebase.module.metadata.api.datasource.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据源管理 API 本地内存实现（无 server 模块时的兜底 Stub）。
 * 仅用于避免 Bean 缺失启动失败；不做真实持久化。
 * 若后续引入正式实现，请替换或移除此 Stub。
 */
@Slf4j
@Service
@Primary
public class MetadataDatasourceApiImpl implements MetadataDatasourceApi {

    private final AtomicLong idGen = new AtomicLong(1000);
    private final Map<Long, SimpleDs> store = new ConcurrentHashMap<>();

    @Override
    public CommonResult<Long> createDefaultDatasource(DatasourceCreateDefaultReqDTO reqDTO) {
        try {
            SimpleDs ds = new SimpleDs();
            ds.setId(idGen.incrementAndGet());
            ds.setAppId(reqDTO.getAppId());
            ds.setAppUid(reqDTO.getAppUid());
            ds.setName("默认数据源");
            ds.setCode("default");
            ds.setDatasourceType("mysql");
            ds.setConfig("{}");
            store.put(ds.getId(), ds);
            return CommonResult.success(ds.getId());
        } catch (Exception e) {
            log.error("createDefaultDatasource stub error", e);
            return CommonResult.error(500, e.getMessage());
        }
    }

    @Override
    public CommonResult<Long> createDatasource(DatasourceSaveReqDTO reqDTO) {
        try {
            SimpleDs ds = new SimpleDs();
            ds.setId(idGen.incrementAndGet());
            ds.setAppId(reqDTO.getAppId());
            ds.setAppUid(reqDTO.getAppUid());
            ds.setName(reqDTO.getName());
            ds.setCode(reqDTO.getCode());
            ds.setDatasourceType(reqDTO.getDatasourceType());
            ds.setConfig(reqDTO.getConfig());
            ds.setRemark(reqDTO.getRemark());
            store.put(ds.getId(), ds);
            return CommonResult.success(ds.getId());
        } catch (Exception e) {
            log.error("createDatasource stub error", e);
            return CommonResult.error(500, e.getMessage());
        }
    }

    @Override
    public CommonResult<Object> getDatasource(Long id) {
        SimpleDs ds = store.get(id);
        if (ds == null) {
            return CommonResult.error(404, "datasource not found");
        }
        return CommonResult.success(ds);
    }

    @Data
    private static class SimpleDs {
        private Long id;
        private Long appId;
        private String appUid;
        private String name;
        private String code;
        private String datasourceType;
        private String config;
        private String remark;
    }
}
