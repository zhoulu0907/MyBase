package com.cmsr.onebase.plugin.runtime.service;

import com.cmsr.onebase.plugin.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 数据服务实现
 * <p>
 * 桥接平台的数据访问服务，提供给插件使用。
 * TODO: 实际使用时需要注入平台的EntityService或DataRepository来实现真实的数据操作。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Service
public class DataServiceImpl implements DataService {

    private static final Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    // TODO: 注入平台的数据服务
    // @Resource
    // private EntityDataService entityDataService;

    @Override
    public Map<String, Object> getById(String entityCode, Long id) {
        log.debug("DataService.getById: entityCode={}, id={}", entityCode, id);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public Map<String, Object> getOne(String entityCode, Map<String, Object> query) {
        log.debug("DataService.getOne: entityCode={}, query={}", entityCode, query);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public List<Map<String, Object>> list(String entityCode, Map<String, Object> query) {
        log.debug("DataService.list: entityCode={}, query={}", entityCode, query);
        // TODO: 调用平台服务实现
        return Collections.emptyList();
    }

    @Override
    public DataService.PageResult<Map<String, Object>> page(String entityCode, Map<String, Object> query, int pageNum, int pageSize) {
        log.debug("DataService.page: entityCode={}, pageNum={}, pageSize={}", entityCode, pageNum, pageSize);
        // TODO: 调用平台服务实现
        return new DataService.PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
    }

    @Override
    public long count(String entityCode, Map<String, Object> query) {
        log.debug("DataService.count: entityCode={}, query={}", entityCode, query);
        // TODO: 调用平台服务实现
        return 0L;
    }

    @Override
    public Long create(String entityCode, Map<String, Object> data) {
        log.debug("DataService.create: entityCode={}, data={}", entityCode, data);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public List<Long> batchCreate(String entityCode, List<Map<String, Object>> dataList) {
        log.debug("DataService.batchCreate: entityCode={}, count={}", entityCode, dataList.size());
        // TODO: 调用平台服务实现
        return Collections.emptyList();
    }

    @Override
    public void update(String entityCode, Long id, Map<String, Object> data) {
        log.debug("DataService.update: entityCode={}, id={}, data={}", entityCode, id, data);
        // TODO: 调用平台服务实现
    }

    @Override
    public int updateByQuery(String entityCode, Map<String, Object> query, Map<String, Object> data) {
        log.debug("DataService.updateByQuery: entityCode={}, query={}, data={}", entityCode, query, data);
        // TODO: 调用平台服务实现
        return 0;
    }

    @Override
    public void delete(String entityCode, Long id) {
        log.debug("DataService.delete: entityCode={}, id={}", entityCode, id);
        // TODO: 调用平台服务实现
    }

    @Override
    public void batchDelete(String entityCode, List<Long> ids) {
        log.debug("DataService.batchDelete: entityCode={}, ids={}", entityCode, ids);
        // TODO: 调用平台服务实现
    }

    @Override
    public int deleteByQuery(String entityCode, Map<String, Object> query) {
        log.debug("DataService.deleteByQuery: entityCode={}, query={}", entityCode, query);
        // TODO: 调用平台服务实现
        return 0;
    }

    @Override
    public List<Map<String, Object>> executeSql(String sql, Object... params) {
        log.debug("DataService.executeSql: sql={}", sql);
        // TODO: 调用平台服务实现，注意安全性校验
        return Collections.emptyList();
    }
}
