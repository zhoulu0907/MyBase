package com.cmsr.onebase.module.system.dal.flex.base;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 * 新增基础方法，提供方法兼容和便利。
 *
 * @author matianyu
 * @param <M>
 * @param <T>
 */
public class BaseDataServiceImpl<M extends BaseMapper<T>, T extends BaseTenantEntity> extends ServiceImpl<M, T> {

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

}
