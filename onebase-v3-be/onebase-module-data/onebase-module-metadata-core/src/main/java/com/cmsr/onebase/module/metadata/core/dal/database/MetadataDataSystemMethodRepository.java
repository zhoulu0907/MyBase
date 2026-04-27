package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataSystemMethodDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataDataSystemMethodMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据数据系统方法仓储类
 * <p>
 * 提供数据系统方法相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataDataSystemMethodRepository extends ServiceImpl<MetadataDataSystemMethodMapper, MetadataDataSystemMethodDO> {

    /**
     * 获取启用的数据方法列表
     *
     * @return 数据方法列表
     */
    public List<MetadataDataSystemMethodDO> getEnabledDataMethodList() {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataDataSystemMethodDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus())
                .orderBy(MetadataDataSystemMethodDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据方法编码获取数据方法
     *
     * @param methodCode 方法编码
     * @return 数据方法对象
     */
    public MetadataDataSystemMethodDO getDataMethodByCode(String methodCode) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataDataSystemMethodDO::getMethodCode, methodCode)
                .eq(MetadataDataSystemMethodDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus());
        return getOne(queryWrapper);
    }

    /**
     * 根据方法类型获取数据方法列表
     *
     * @param methodType 方法类型
     * @return 数据方法列表
     */
    public List<MetadataDataSystemMethodDO> getDataMethodListByType(String methodType) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataDataSystemMethodDO::getMethodType, methodType)
                .eq(MetadataDataSystemMethodDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus())
                .orderBy(MetadataDataSystemMethodDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 获取所有数据方法列表（不过滤启用状态）
     *
     * @return 数据方法列表
     */
    public List<MetadataDataSystemMethodDO> getAllDataMethodList() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(MetadataDataSystemMethodDO::getCreateTime, false);
        return list(queryWrapper);
    }
}
