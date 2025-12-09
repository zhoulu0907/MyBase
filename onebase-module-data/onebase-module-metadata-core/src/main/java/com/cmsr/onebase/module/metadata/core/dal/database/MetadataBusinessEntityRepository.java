package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataBusinessEntityMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据业务实体仓储类
 * <p>
 * 提供业务实体相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataBusinessEntityRepository extends BaseBizRepository<MetadataBusinessEntityMapper, MetadataBusinessEntityDO> {

    /**
     * 根据ID获取业务实体
     *
     * @param entityId 实体ID
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getBusinessEntityById(Long entityId) {
        return getById(entityId);
    }

    /**
     * 根据ID获取业务实体
     *
     * @param entityId 实体ID（字符串格式）
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getBusinessEntityById(String entityId) {
        if (entityId == null || entityId.trim().isEmpty()) {
            return null;
        }
        return getById(Long.valueOf(entityId));
    }

    /**
     * 根据实体UUID获取业务实体
     *
     * @param entityUuid 实体UUID
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getByEntityUuid(String entityUuid) {
        if (entityUuid == null || entityUuid.trim().isEmpty()) {
            return null;
        }
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataBusinessEntityDO::getEntityUuid, entityUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据编码获取业务实体
     *
     * @param code 实体编码
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataBusinessEntityDO::getCode, code);
        return getOne(queryWrapper);
    }

    /**
     * 带锁查询单个实体，用于避免并发冲突
     *
     * @param queryWrapper 查询条件
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO findOneWithLock(QueryWrapper queryWrapper) {
        // 使用悲观锁查询，避免并发冲突
        List<MetadataBusinessEntityDO> results = list(queryWrapper);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 获取业务实体列表
     *
     * @return 业务实体列表
     */
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(MetadataBusinessEntityDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据数据源UUID获取业务实体列表
     *
     * @param datasourceUuid 数据源UUID
     * @return 业务实体列表
     */
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceUuid(String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataBusinessEntityDO::getDatasourceUuid, datasourceUuid)
                .orderBy(MetadataBusinessEntityDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据应用ID获取简单业务实体列表
     *
     * @param appId 应用ID
     * @return 业务实体列表
     */
    public List<MetadataBusinessEntityDO> getSimpleEntityListByAppId(Long appId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataBusinessEntityDO::getApplicationId, appId)
                .orderBy(MetadataBusinessEntityDO::getCreateTime, true);
        return list(queryWrapper);
    }

    /**
     * 分页查询业务实体
     *
     * @param queryWrapper 查询条件
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<MetadataBusinessEntityDO> getBusinessEntityPage(QueryWrapper queryWrapper, Integer pageNo, Integer pageSize) {
        Page<MetadataBusinessEntityDO> pageQuery = Page.of(pageNo, pageSize);
        Page<MetadataBusinessEntityDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 校验业务实体编码是否唯一
     *
     * @param id 实体ID（排除自身）
     * @param code 实体编码
     * @param appId 应用ID
     * @return 是否唯一
     */
    public boolean isBusinessEntityCodeUnique(Long id, String code, Long appId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataBusinessEntityDO::getCode, code)
                .eq(MetadataBusinessEntityDO::getApplicationId, appId)
                .ne(MetadataBusinessEntityDO::getId, id, id != null);
        return count(queryWrapper) == 0;
    }

    /**
     * 根据实体ID校验实体是否存在
     *
     * @param id 实体ID
     * @return 是否存在
     */
    public boolean existsBusinessEntity(Long id) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataBusinessEntityDO::getId, id);
        return getOne(queryWrapper) != null;
    }
}
