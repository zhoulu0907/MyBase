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
package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDOInterface;
import jakarta.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

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
public abstract class WarmDaoImpl<T extends RootEntity & BaseDOInterface> implements WarmDao<T> {

    public abstract DataRepository<T> getRepository();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) {
            return null;
        }

        return getRepository().findById(longId);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        if (ids.isEmpty()) {
            return null;
        }
        List<Long> longIds = ids.stream().map(this::convertToLong).filter(ObjectUtil::isNotNull).toList();
        return getRepository().findAllByIds(longIds);
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        // 构建查询条件
        ConfigStore configs = buildQueryConfig(entity);

        // 设置排序
        if (StringUtils.isNotEmpty(page.getOrderBy())) {
            boolean isAsc = page.getIsAsc() != null && page.getIsAsc().equals("ASC");
            configs.order(page.getOrderBy(), isAsc);
        }

        // 执行分页查询
        PageResult<T> pageResult = getRepository().findPageWithConditions(configs, page.getPageNum(), page.getPageSize());

        if (ObjectUtil.isNotNull(pageResult)) {
            // 转换为WarmFlow的Page对象
            Page<T> result = new Page<>(pageResult.getList(), pageResult.getTotal());
            result.setPageNum(page.getPageNum());
            result.setPageSize(page.getPageSize());
            return result;
        }

        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        ConfigStore configs = buildQueryConfig(entity);

        // 设置排序
        if (ObjectUtil.isNotNull(query) && StringUtils.isNotEmpty(query.getOrderBy())) {
            boolean isAsc = query.getIsAsc() != null && query.getIsAsc().equals("ASC");
            configs.order(query.getOrderBy(), isAsc);
        }

        return getRepository().findAllByConfig(configs);
    }

    @Override
    public long selectCount(T entity) {
        ConfigStore configs = buildQueryConfig(entity);
        return getRepository().countByConfig(configs);
    }

    @Override
    public int save(T entity) {
        getRepository().insert(entity);
        return 1;
    }

    @Override
    public int updateById(T entity) {
        return (int) getRepository().update(entity);
    }

    @Override
    public int delete(T entity) {
        ConfigStore configs = buildQueryConfig(entity);
        return (int) getRepository().deleteByConfig(configs);
    }

    @Override
    public int deleteById(Serializable id) {
        Long longId = convertToLong(id);
        return (int) getRepository().deleteById(longId);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        List<Long> longIds = ids.stream().map(this::convertToLong).filter(ObjectUtil::isNotNull).toList();
        return (int) getRepository().deleteByIds(longIds);
    }

    @Override
    public void saveBatch(List<T> list) {
        getRepository().insertBatch(list);
    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            updateById(record);
        }
    }

    public List<Field> getAllFields(T entity) {
        List<Field> fields = new ArrayList<>();
        // 替换为while 循环
        Class<?> c = entity.getClass();

        while (c != null && c != Object.class) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }

        return fields;
    }

    /**
     * 构建查询条件
     * 子类可重写此方法以添加更多查询条件
     * 按照 MyBatis-Plus 的做法，将实体中所有非 null 的字段都作为查询条件
     *
     * @param entity 查询实体
     * @return ConfigStore
     */
    protected ConfigStore buildQueryConfig(T entity) {
        ConfigStore configs = new DefaultConfigStore();
        if (entity == null) {
            return configs;
        }

        // 使用反射获取实体所有字段，构建查询条件
        List<Field> fields = getAllFields(entity);

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(entity);

                // 跳过 null 值
                if (value == null) {
                    continue;
                }

                // 跳过集合类型
                if (value instanceof Collection || value instanceof Map) {
                    continue;
                }

                // 获取字段名
                String fieldName = field.getName();

                // 获取 @Column 注解的 name 属性，如果没有则说明非数据库字段
                Column columnAnno = field.getAnnotation(Column.class);

                if (columnAnno == null) {
                    continue;
                }

                String columnName = !columnAnno.name().isEmpty() ? columnAnno.name() : fieldName;

                // 添加等值条件
                configs.and(Compare.EQUAL, columnName, value);
            } catch (IllegalAccessException e) {
                log.warn("无法访问字段: {}", field.getName(), e);
            }
        }

        return configs;
    }

    private Long convertToLong(Serializable id) {
        if (id == null) {
            return null;
        }

        if (id instanceof Long) {
            return (Long) id;
        }

        if (id instanceof String) {
            try { return Long.parseLong((String) id); } catch (NumberFormatException ignored) { return null; }
        }

        if (id instanceof Number) {
            return ((Number) id).longValue();
        }

        return null;
    }

}
