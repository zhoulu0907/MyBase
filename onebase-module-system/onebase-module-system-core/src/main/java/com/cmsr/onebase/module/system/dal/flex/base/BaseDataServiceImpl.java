package com.cmsr.onebase.module.system.dal.flex.base;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

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

    public boolean upsertBatch(Collection<T> dataList) {
        return saveOrUpdateBatch(dataList);
    }
}
