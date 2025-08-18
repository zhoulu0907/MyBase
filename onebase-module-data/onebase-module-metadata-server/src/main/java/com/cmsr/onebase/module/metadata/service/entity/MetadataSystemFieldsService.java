package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataSystemFieldsDO;
import org.anyline.data.param.init.DefaultConfigStore;

import java.util.List;

/**
 * 系统字段 Service 接口
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface MetadataSystemFieldsService {

    /**
     * 根据配置查询系统字段列表
     *
     * @param configStore 查询配置
     * @return 系统字段列表
     */
    List<MetadataSystemFieldsDO> findAllByConfig(DefaultConfigStore configStore);
}
