package com.cmsr.onebase.module.metadata.core.service.entity;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * 系统字段 Service 接口
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface MetadataSystemFieldsCoreService {

    /**
     * 根据配置查询系统字段列表
     *
     * @param queryWrapper 查询条件
     * @return 系统字段列表
     */
    List<MetadataSystemFieldsDO> findAllByConfig(QueryWrapper queryWrapper);

     /**
     * 根据配置查询系统字段列表
     *
     * @param queryWrapper 查询条件
     * @return 系统字段列表
     */
    List<MetadataSystemFieldsDO> findAllEnabeldSystemFields();
}
