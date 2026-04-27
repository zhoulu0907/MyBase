package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataSystemMethodDO;

import java.util.List;

/**
 * 数据系统方法 Service 接口
 *
 * @author bty418
 * @date 2025-01-27
 */
public interface MetadataDataSystemMethodCoreService {

    /**
     * 根据方法编码获取数据方法
     *
     * @param methodCode 方法编码
     * @return 数据方法对象
     */
    MetadataDataSystemMethodDO getDataMethodByCode(String methodCode);

    /**
     * 获取启用的数据方法列表
     *
     * @return 启用的数据方法列表
     */
    List<MetadataDataSystemMethodDO> getEnabledDataMethodList();

    /**
     * 根据ID获取数据方法
     *
     * @param id 数据方法ID
     * @return 数据方法对象
     */
    MetadataDataSystemMethodDO getDataMethodById(Long id);

}
