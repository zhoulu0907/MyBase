package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataSystemFieldsMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据系统字段仓储类
 * <p>
 * 提供系统字段相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataSystemFieldsRepository extends ServiceImpl<MetadataSystemFieldsMapper, MetadataSystemFieldsDO> {

    /**
     * 获取系统字段列表
     *
     * @return 系统字段列表
     */
    public List<MetadataSystemFieldsDO> getSystemFields() {
        QueryWrapper queryWrapper = this.query()
                .eq("is_system_field", true)
                .eq(MetadataSystemFieldsDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus());
        return list(queryWrapper);
    }

    /**
     * 获取所有系统字段列表（不过滤启用状态）
     *
     * @return 系统字段列表
     */
    public List<MetadataSystemFieldsDO> getAllSystemFields() {
        QueryWrapper queryWrapper = this.query()
                .eq("is_system_field", true)
                .orderBy(MetadataSystemFieldsDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据字段名获取系统字段
     *
     * @param fieldName 字段名
     * @return 系统字段对象
     */
    public MetadataSystemFieldsDO getSystemFieldByName(String fieldName) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataSystemFieldsDO::getFieldName, fieldName)
                .eq("is_system_field", true);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段编码获取系统字段
     *
     * @param fieldCode 字段编码
     * @return 系统字段对象
     */
    public MetadataSystemFieldsDO getSystemFieldByCode(String fieldCode) {
        QueryWrapper queryWrapper = this.query()
                .eq("field_code", fieldCode)
                .eq("is_system_field", true);
        return getOne(queryWrapper);
    }
}
