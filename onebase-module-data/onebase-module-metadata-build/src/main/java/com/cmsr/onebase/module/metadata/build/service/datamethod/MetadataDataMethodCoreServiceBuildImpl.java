package com.cmsr.onebase.module.metadata.build.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 数据方法 Service 编辑态临时实现类 - 用于编辑态服务启动
 * 注意：此类仅作为临时实现，确保编辑态服务能够正常启动
 * 实际的数据操作应在运行态服务中实现
 *
 * @author bty418
 * @date 2025-10-23
 */
@Service
@Slf4j
public class MetadataDataMethodCoreServiceBuildImpl implements MetadataDataMethodCoreService {

    @Override
    public Map<String, Object> createData(Long entityId, Map<String, Object> data, String methodCode) {
        log.warn("编辑态服务不支持数据创建操作，entityId: {}", entityId);
        throw new UnsupportedOperationException("编辑态服务不支持数据创建操作，请使用运行态服务");
    }

    @Override
    public Map<String, Object> updateData(Long entityId, Object id, Map<String, Object> data, String methodCode) {
        log.warn("编辑态服务不支持数据更新操作，entityId: {}, id: {}", entityId, id);
        throw new UnsupportedOperationException("编辑态服务不支持数据更新操作，请使用运行态服务");
    }

    @Override
    public Boolean deleteData(Long entityId, Object id, String methodCode) {
        log.warn("编辑态服务不支持数据删除操作，entityId: {}, id: {}", entityId, id);
        throw new UnsupportedOperationException("编辑态服务不支持数据删除操作，请使用运行态服务");
    }

    @Override
    public Map<String, Object> getData(Long entityId, Object id, String methodCode) {
        log.warn("编辑态服务不支持数据查询操作，entityId: {}, id: {}", entityId, id);
        throw new UnsupportedOperationException("编辑态服务不支持数据查询操作，请使用运行态服务");
    }

    @Override
    public PageResult<Map<String, Object>> getDataPage(Long entityId, Integer pageNo, Integer pageSize,
                                                       String sortField, String sortDirection,
                                                       Map<String, Object> filters, String methodCode) {
        log.warn("编辑态服务不支持分页查询操作，entityId: {}", entityId);
        throw new UnsupportedOperationException("编辑态服务不支持分页查询操作，请使用运行态服务");
    }

    @Override
    public PageResult<Map<String, Object>> getDataPageOr(Long entityId, Integer pageNo, Integer pageSize,
                                                         String sortField, String sortDirection,
                                                         List<Map<String, Object>> orConditionGroups,
                                                         String methodCode) {
        log.warn("编辑态服务不支持OR复合查询操作，entityId: {}", entityId);
        throw new UnsupportedOperationException("编辑态服务不支持OR复合查询操作，请使用运行态服务");
    }
}

