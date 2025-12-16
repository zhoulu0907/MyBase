package com.cmsr.onebase.framework.orm.repo;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Slf4j
public class BaseBizRepository<M extends BaseMapper<T>, T extends BaseBizEntity> extends ServiceImpl<M, T> {

    protected void injectQueryFilter(QueryWrapper queryWrapper) {
        if (ApplicationManager.isIgnoreApplicationCondition() && ApplicationManager.isIgnoreVersionTagCondition()) {
            return;
        }
        if (!QueryWrapperUtils.isQueryFilterable(queryWrapper)) {
            return;
        }
        QueryTable queryTable = QueryWrapperUtils.getQueryTable(queryWrapper);
        QueryColumn applicationColumn;
        QueryColumn versionTagColumn;
        if (queryTable != null) {
            applicationColumn = new QueryColumn(queryTable, QueryWrapperUtils.APPLICATION_ID);
            versionTagColumn = new QueryColumn(queryTable, QueryWrapperUtils.VERSION_TAG);
        } else {
            applicationColumn = new QueryColumn(QueryWrapperUtils.APPLICATION_ID);
            versionTagColumn = new QueryColumn(QueryWrapperUtils.VERSION_TAG);
        }
        Long applicationId = ApplicationManager.getApplicationId();
        Long versionTag = ApplicationManager.getVersionTag();
        queryWrapper.and(applicationColumn.eq(applicationId).when(!ApplicationManager.isIgnoreApplicationCondition()));
        queryWrapper.and(versionTagColumn.eq(versionTag).when(!ApplicationManager.isIgnoreVersionTagCondition()));
    }

    //region ===== 删除（删）操作 =====

    /**
     * <p>根据查询条件删除数据。
     *
     * @param query 查询条件
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    @Override
    public boolean remove(QueryWrapper query) {
        this.injectQueryFilter(query);
        return super.remove(query);
    }
    //region ===== 更新（改）操作 =====

    /**
     * <p>根据查询条件更新数据。
     *
     * @param entity 实体类对象
     * @param query  查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若实体类属性数据为 {@code null}，该属性不会新到数据库。
     */
    @Override
    public boolean update(T entity, QueryWrapper query) {
        this.injectQueryFilter(query);
        return super.update(entity, query);
    }

    @Deprecated
    @Override
    public UpdateChain<T> updateChain() {
        Long applicationId = ApplicationManager.getApplicationId();
        Long versionTag = ApplicationManager.getVersionTag();
        //
        UpdateChain<T> updateChain = super.updateChain();
        updateChain.where(QueryWrapperUtils.APPLICATION_COLUMN.eq(applicationId).when(!ApplicationManager.isIgnoreApplicationCondition())
                .and(QueryWrapperUtils.VERSION_TAG_COLUMN.eq(versionTag).when(!ApplicationManager.isIgnoreVersionTagCondition()))
        );
        return updateChain;
    }
    //endregion ===== 更新（改）操作 =====

    //region ===== 查询（查）操作 =====

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param query 查询条件
     * @return 查询结果数据
     */
    @Override
    public T getOne(QueryWrapper query) {
        injectQueryFilter(query);
        return getMapper().selectOneByQuery(query);
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
        this.injectQueryFilter(query);
        return getMapper().selectOneByQueryAs(query, asType);
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
        this.injectQueryFilter(query);
        return getMapper().selectObjectByQuery(query);
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
        this.injectQueryFilter(query);
        return getMapper().selectObjectByQueryAs(query, asType);
    }

    /**
     * <p>查询结果集中第一列所有数据。
     *
     * @param query 查询条件
     * @return 数据列表
     */
    @Override
    public List<Object> objList(QueryWrapper query) {
        this.injectQueryFilter(query);
        return getMapper().selectObjectListByQuery(query);
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
        this.injectQueryFilter(query);
        return getMapper().selectObjectListByQueryAs(query, asType);
    }

    /**
     * <p>根据查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    @Override
    public List<T> list(QueryWrapper query) {
        this.injectQueryFilter(query);
        return getMapper().selectListByQuery(query);
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
        this.injectQueryFilter(query);
        return getMapper().selectListByQueryAs(query, asType);
    }

    /**
     * <p>根据数据主键查询数据集合。
     *
     * @param ids 数据主键
     * @return 数据集合
     */
    @Override
    public List<T> listByIds(Collection<? extends Serializable> ids) {
        QueryWrapper query = QueryWrapper.create().in("id", ids);
        this.injectQueryFilter(query);
        return getMapper().selectListByQuery(query);
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
        return exists(CPI.getWhereQueryCondition(query));
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
        QueryWrapper query = QueryMethods.selectOne().where(condition).limit(1);
        this.injectQueryFilter(query);
        // 获取数据集合，空集合：[] 不存在数据，有一个元素的集合：[1] 存在数据
        List<Object> objects = getMapper().selectObjectListByQuery(query);
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
        this.injectQueryFilter(query);
        return getMapper().selectCountByQuery(query);
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
        this.injectQueryFilter(query);
        return getMapper().paginateAs(page, query, asType);
    }

    //endregion ===== 分页查询操作 =====

    // 1、备份运行态数据为历史版本
    public void moveRuntimeToHistory(Long applicationId, Long versionTag) {
        // 实现备份逻辑
        // 执行update动作。
        // 1、update：把versionTag为1的数据update为新值（参数`versionTag`）
        QueryColumn applicationIdCol = new QueryColumn(QueryWrapperUtils.APPLICATION_ID);
        QueryColumn versionTagCol = new QueryColumn(QueryWrapperUtils.VERSION_TAG);
        this.updateChain()
                .set(versionTagCol, versionTag)
                .where(applicationIdCol.eq(applicationId))
                .where(versionTagCol.eq(VersionTagEnum.RUNTIME.getValue()))
                .update();
    }

    // 2、编辑态数据变成运行态数据
    public void copyEditToRuntime(Long applicationId) {
        // 实现发布逻辑
        // 执行select 和 insert 动作。
        // 1、select： versionTag为0的数据
        // 2、insert：把第一步查询出来的数据插入为versionTag为1
        QueryColumn applicationIdCol = new QueryColumn(QueryWrapperUtils.APPLICATION_ID);
        QueryColumn versionTagCol = new QueryColumn(QueryWrapperUtils.VERSION_TAG);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(applicationIdCol.eq(applicationId))
                .where(versionTagCol.eq(VersionTagEnum.BUILD.getValue()));
        List<T> entities = this.getMapper().selectListByQuery(queryWrapper);
        entities.forEach(entity -> {
            entity.setId(null);
            entity.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        });
        this.saveBatch(entities);
    }

    // 3、历史版本数据回滚为运行态数据
    public void copyHistoryToRuntime(Long applicationId, Long versionTag) {
        // 实现回滚逻辑
        // 执行select、insert 动作。
        // 1、select：查询versionTag为参数`versionTag`值的数据
        // 2、insert：插入第一步查询出来的数据，versionTag为1
        QueryColumn applicationIdCol = new QueryColumn(QueryWrapperUtils.APPLICATION_ID);
        QueryColumn versionTagCol = new QueryColumn(QueryWrapperUtils.VERSION_TAG);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(applicationIdCol.eq(applicationId))
                .where(versionTagCol.eq(versionTag));
        List<T> entities = this.getMapper().selectListByQuery(queryWrapper);
        entities.forEach(entity -> {
            entity.setId(null);
            entity.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        });
        this.saveBatch(entities);
    }

    public boolean deleteAllApplicationData(Long applicationId) {
        QueryColumn applicationColumn = new QueryColumn(QueryWrapperUtils.APPLICATION_ID);
        return super.updateChain()
                .where(applicationColumn.eq(applicationId))
                .remove();
    }

    public boolean deleteApplicationVersionData(Long applicationId, Long versionId) {
        QueryColumn applicationColumn = new QueryColumn(QueryWrapperUtils.APPLICATION_ID);
        QueryColumn versionTagColumn = new QueryColumn(QueryWrapperUtils.VERSION_TAG);
        return super.updateChain()
                .where(applicationColumn.eq(applicationId).and(versionTagColumn.eq(versionId)))
                .remove();
    }
}
