package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataDatasourceMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据数据源仓储类
 * <p>
 * 提供数据源相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataDatasourceRepository extends ServiceImpl<MetadataDatasourceMapper, MetadataDatasourceDO> {

    /**
     * 根据ID获取数据源
     *
     * @param datasourceId 数据源ID（字符串格式）
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceById(String datasourceId) {
        if (datasourceId == null || datasourceId.trim().isEmpty()) {
            return null;
        }
        return getById(Long.valueOf(datasourceId));
    }

    /**
     * 根据ID获取数据源
     *
     * @param datasourceId 数据源ID
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceById(Long datasourceId) {
        return getById(datasourceId);
    }

    /**
     * 根据编码获取数据源
     *
     * @param code 数据源编码
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceByCode(String code) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataDatasourceDO::getCode, code);
        return getOne(queryWrapper);
    }

    /**
     * 获取数据源列表
     *
     * @return 数据源列表
     */
    public List<MetadataDatasourceDO> getDatasourceList() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(MetadataDatasourceDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据应用ID获取数据源列表
     *
     * @param appId 应用ID
     * @return 数据源列表
     */
    public List<MetadataDatasourceDO> getDatasourceListByAppId(Long appId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataDatasourceDO::getApplicationId, appId);
        return list(queryWrapper);
    }

    /**
     * 校验数据源编码是否唯一
     *
     * @param id 数据源ID（排除自身）
     * @param code 数据源编码
     * @param appId 应用ID
     * @return 是否唯一
     */
    public boolean isDatasourceCodeUnique(Long id, String code, Long appId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataDatasourceDO::getCode, code)
                .eq(MetadataDatasourceDO::getApplicationId, appId)
                .ne(MetadataDatasourceDO::getId, id, id != null);
        return count(queryWrapper) == 0;
    }

    /**
     * 分页查询数据源
     *
     * @param queryWrapper 查询条件
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageResult<MetadataDatasourceDO> getDatasourcePage(QueryWrapper queryWrapper, int pageNo, int pageSize) {
        // 添加排序
        queryWrapper.orderBy(MetadataDatasourceDO::getCreateTime, false);
        Page<MetadataDatasourceDO> page = page(new Page<>(pageNo, pageSize), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }
}
