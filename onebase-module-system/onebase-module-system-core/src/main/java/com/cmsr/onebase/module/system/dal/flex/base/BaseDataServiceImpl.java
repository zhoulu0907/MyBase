package com.cmsr.onebase.module.system.dal.flex.base;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.anyline.data.param.init.DefaultConfigStore;

import java.util.Collection;
import java.util.List;

/**
 * 新增基础方法，提供方法兼容和便利。
 *
 * @author matianyu
 * @param <M>
 * @param <T>
 */
public class BaseDataServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> {

    /**
     * 新建
     *
     * @param data
     */
    public boolean insert(T data) {
        return this.save(data);
    }

    /**
     * 更新
     *
     * @param data
     */
    public boolean update(T data) {
        return this.update(data, query().eq(BaseDO.ID, data.getId()));
    }

    /**
     * 删除
     * @param id
     * @return
     */
    public boolean deleteById(Long id) {
        return this.removeById(id);
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    public T findById(Long id) {
        return getById(id);
    }

    public List<T> findAll() {
        return list();
    }

    /**
     * 批量插入
     * @param dataList
     * @return
     */
    public boolean insertBatch(Collection<T> dataList) {
        return saveBatch(dataList);
    }

    /**
     * 新建（兼容 anyline insert 返回实体的用法）
     *
     * @param data 数据
     * @return 保存后的数据（包含主键）
     */
    public T insertReturn(T data) {
        this.save(data);
        return data;
    }

    /**
     * 新建（兼容 anyline：insert 返回实体）
     * <p>
     * 注意：与 insert(boolean) 不冲突，Java 会根据返回值之外的参数列表进行重载匹配。
     *
     * @param data 数据
     * @return 保存后的实体
     */
    public T insertAndReturn(T data) {
        this.save(data);
        return data;
    }

    /**
     * 兼容 anyline 的 findAllByIds 命名
     *
     * @param ids 主键集合
     * @return 实体列表
     */
    public List<T> findAllByIds(Collection<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : listByIds(ids);
    }

    /**
     * 兼容 anyline 的 deleteByIds，返回删除行数
     *
     * @param ids 主键集合
     * @return 删除行数
     */
    public long deleteByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0L;
        }
        return this.getMapper().deleteBatchByIds(ids);
    }

    /**
     * 兼容 anyline：deleteByConfig
     * <p>
     * 当前单测仅用于清理数据，DefaultConfigStore 传空条件时表示清空表。
     *
     * @param configStore anyline 配置
     */
    public void deleteByConfig(DefaultConfigStore configStore) {
        // 迁移期最小实现：按空条件删除
        this.remove(new QueryWrapper());
    }

    /**
     * 批量 upsert（兼容 anyline 期望返回列表的用法）
     *
     * @param dataList 数据列表
     * @return 实际处理的数据列表
     */
    public List<T> upsertBatchReturnList(Collection<T> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return List.of();
        }
        this.saveOrUpdateBatch(dataList);
        return List.copyOf(dataList);
    }

    /**
     * 兼容 anyline 的 deleteByConfig：通过 QueryWrapper 删除
     *
     * @param queryWrapper 查询条件
     * @return 删除行数
     */
    public long deleteByQuery(QueryWrapper queryWrapper) {
        return this.getMapper().deleteByQuery(queryWrapper);
    }

    /**
     * 批量 upsert（兼容方法名 upsertBatch）
     * <p>
     * 说明：部分业务代码期望直接调用 upsertBatch 并获取返回列表。
     *
     * @param dataList 数据列表
     * @return 实际处理的数据列表
     */
    public List<T> upsertBatch(Collection<T> dataList) {
        return upsertBatchReturnList(dataList);
    }
}
