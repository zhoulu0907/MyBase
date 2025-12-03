/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;

import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.WarmMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * WarmDao AnyLine 实现
 *
 * 基于 AnyLine 框架实现 WarmDao 接口，提供与 MyBatis-Plus 类似风格的 API
 * 支持实体类的增删改查操作，包含分页、排序、条件查询等功能
 *
 * @author liyang
 * @date 2025-10-10
 */
@Slf4j
public abstract class WarmDaoImpl<U extends WarmMapper<T>, T extends RootEntity> implements WarmDao<T> {
    public abstract ServiceImpl<U, T> getRepository();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        return getRepository().getById(id);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        return getRepository().listByIds(ids);
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        com.mybatisflex.core.paginate.Page<T> pageQuery = new com.mybatisflex.core.paginate.Page<>(page.getPageNum(), page.getPageSize());

        QueryWrapper queryWrapper = QueryWrapper.create(entity);

        if (StringUtils.isNotEmpty(page.getOrderBy())) {
            String orderByColumn = page.getOrderBy();
            boolean isAsc = "ASC".equalsIgnoreCase(page.getIsAsc());

            queryWrapper.orderBy(orderByColumn, isAsc);
        }

        com.mybatisflex.core.paginate.Page<T> tPage = getRepository().page(pageQuery, queryWrapper);

        if (ObjectUtil.isNotNull(tPage)) {
            Page<T> rPage = new Page<>(tPage.getRecords(), tPage.getTotalRow());
            rPage.setPageNum(page.getPageNum());
            rPage.setPageSize(page.getPageSize());
            return rPage;
        }

        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity);

        if (ObjectUtil.isNotNull(query) && StringUtils.isNotEmpty(query.getOrderBy())) {
            String orderByColumn = query.getOrderBy();
            boolean isAsc = "ASC".equalsIgnoreCase(query.getIsAsc());

            queryWrapper.orderBy(orderByColumn, isAsc);
        }

        return getRepository().list(queryWrapper);
    }

    @Override
    public long selectCount(T entity) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity);
        return getRepository().count(queryWrapper);
    }

    @Override
    public int save(T entity) {
        return getRepository().save(entity) ? 1 : 0;
    }

    @Override
    public int updateById(T entity) {
        return getRepository().updateById(entity) ? 1 : 0;
    }

    @Override
    public int delete(T entity) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity);
        return getRepository().remove(queryWrapper) ? 1 : 0;
    }

    @Override
    public int deleteById(Serializable id) {
        return getRepository().removeById(id) ? 1 : 0;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return getRepository().removeByIds(ids) ? 1 : 0;
    }

    @Override
    public void saveBatch(List<T> list) {
        getRepository().saveBatch(list);
    }

    @Override
    public void updateBatch(List<T> list) {
        getRepository().updateBatch(list);
    }
}
