package com.cmsr.onebase.framework.orm.repo;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Slf4j
public class BaseBizRepository<M extends BaseMapper<T>, T extends BaseBizEntity> extends ServiceImpl<M, T> {

    protected QueryWrapper injectBizFilter(QueryWrapper queryWrapper) {
        // TODO: add filters like applicationId, versionFlag
        Long applicationId = ApplicationManager.getApplicationId();
//        Long applicationId = XXXXX;
//        var versionStatus = 0,1,xxxxxxx;
//        return queryWrapper
//                .eq("version_flag", versionFlag);
        log.debug("注入SQL查询条件");
        return queryWrapper;
    }

    //region ===== 查询（查）操作 =====

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param query 查询条件
     * @return 查询结果数据
     */
    @Override
    public T getOne(QueryWrapper query) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectOneByQuery(queryWrapper);
    }

    /**
     * <p>根据数据主键查询一条数据。
     *
     * @param id 数据主键
     * @return 查询结果数据
     */
    @Override
    public T getById(Serializable id) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("id", id);
        return getOne(queryWrapper);
    }

    /**
     * <p>根据查询条件查询一条数据，并通过 asType 进行接收。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 查询结果数据
     */
    @Override
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectOneByQueryAs(queryWrapper, asType);
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param condition 查询条件
     * @return 查询结果数据
     */
    @Override
    public T getOne(QueryCondition condition) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(condition).limit(1);
        return getOne(queryWrapper);
    }

    /**
     * <p>查询结果集中第一列，且第一条数据。
     *
     * @param query 查询条件
     * @return 数据值
     */
    @Override
    public Object getObj(QueryWrapper query) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectObjectByQuery(queryWrapper);
    }

    /**
     * <p>查询结果集中第一列，且第一条数据，并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据值
     */
    @Override
    public <R> R getObjAs(QueryWrapper query, Class<R> asType) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectObjectByQueryAs(queryWrapper, asType);
    }

    /**
     * <p>查询结果集中第一列所有数据。
     *
     * @param query 查询条件
     * @return 数据列表
     */
    @Override
    public List<Object> objList(QueryWrapper query) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectObjectListByQuery(queryWrapper);
    }

    /**
     * <p>查询结果集中第一列所有数据，并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据列表
     */
    @Override
    public <R> List<R> objListAs(QueryWrapper query, Class<R> asType) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectObjectListByQueryAs(queryWrapper, asType);
    }

    /**
     * <p>根据查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    @Override
    public List<T> list(QueryWrapper query) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectListByQuery(queryWrapper);
    }

    /**
     * <p>根据查询条件查询数据集合，并通过 asType 进行接收。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据集合
     */
    @Override
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectListByQueryAs(queryWrapper, asType);
    }

    /**
     * <p>根据数据主键查询数据集合。
     *
     * @param ids 数据主键
     * @return 数据集合
     */
    @Override
    public List<T> listByIds(Collection<? extends Serializable> ids) {
        QueryWrapper queryWrapper = this.injectBizFilter(QueryWrapper.create().in("id", ids));
        return getMapper().selectListByQuery(queryWrapper);
    }
    //endregion ===== 查询（查）操作 =====

    //region ===== 数量查询操作 =====

    /**
     * <p>根据查询条件判断数据是否存在。
     *
     * @param query 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    @Override
    public boolean exists(QueryWrapper query) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return exists(CPI.getWhereQueryCondition(queryWrapper));
    }

    /**
     * <p>根据查询条件判断数据是否存在。
     *
     * @param condition 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    @Override
    public boolean exists(QueryCondition condition) {
        // 根据查询条件构建 SQL 语句
        // SELECT 1 FROM table WHERE ... LIMIT 1
        QueryWrapper queryWrapper = this.injectBizFilter(QueryMethods.selectOne().where(condition).limit(1));
        // 获取数据集合，空集合：[] 不存在数据，有一个元素的集合：[1] 存在数据
        List<Object> objects = getMapper().selectObjectListByQuery(queryWrapper);
        // 判断是否存在数据
        return CollectionUtil.isNotEmpty(objects);
    }

    /**
     * <p>根据查询条件查询数据数量。
     *
     * @param query 查询条件
     * @return 数据数量
     */
    @Override
    public long count(QueryWrapper query) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().selectCountByQuery(queryWrapper);
    }
    //endregion ===== 数量查询操作 =====

    //region ===== 分页查询操作 =====

    /**
     * <p>根据查询条件分页查询数据，并通过 asType 进行接收。
     *
     * @param page   分页对象
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 分页对象
     */
    @Override
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        QueryWrapper queryWrapper = this.injectBizFilter(query);
        return getMapper().paginateAs(page, queryWrapper, asType);
    }
    //endregion ===== 分页查询操作 =====
}
