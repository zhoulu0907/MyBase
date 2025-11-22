package com.cmsr.onebase.framework.mybatis;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BaseBizRepository<M extends BaseMapper<T>, T extends BaseBizEntity> extends ServiceImpl<M, T> {

    private QueryWrapper injectQueryLimitation(QueryWrapper queryWrapper) {
        // TODO: add filters like applicationId, loginEnv
        Long applicationId = 1234567890L;
        String loginEnv = "bld";
        return queryWrapper
                .eq(T::getApplicationId, applicationId)
                .eq(T::getLoginEnv, loginEnv);
    }

    private QueryWrapper injectCondition(QueryCondition condition) {
        return this.query().where(condition);
    }

    @Override
    public T getById(Serializable id) { //
        return this.getOne(QueryWrapper.create().eq(T::getId, id));
    }

    @Override
    public T getOneByEntityId(T entity) { //
        return this.getOne(QueryWrapper.create(entity));
    }

    @Override
    public Optional<T> getByEntityIdOpt(T entity) { //
        return Optional.ofNullable(this.getOneByEntityId(entity));
    }

    @Override
    public Optional<T> getByIdOpt(Serializable id) { //
        return Optional.ofNullable(this.getById(id));
    }

    @Override
    public T getOne(QueryWrapper query) { //
        return super.getOne(this.injectQueryLimitation(query));
    }

    @Override
    public Optional<T> getOneOpt(QueryWrapper query) { //
        return Optional.ofNullable(getOne(query));
    }

    @Override
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) { //
        return super.getOneAs(this.injectQueryLimitation(query), asType);
    }

    @Override
    public <R> Optional<R> getOneAsOpt(QueryWrapper query, Class<R> asType) { //
        return Optional.ofNullable(this.getOneAs(query, asType));
    }

    @Override
    public T getOne(QueryCondition condition) { //
        return this.getOne(this.injectCondition(condition));
    }

    @Override
    public Optional<T> getOneOpt(QueryCondition condition) { //
        return Optional.ofNullable(this.getOne(condition));
    }

    @Override
    public Object getObj(QueryWrapper query) { //
        return super.getObj(this.injectQueryLimitation(query));
    }

    @Override
    public Optional<Object> getObjOpt(QueryWrapper query) { //
        return Optional.ofNullable(this.getObj(query));
    }

    @Override
    public <R> R getObjAs(QueryWrapper query, Class<R> asType) { //
        return super.getObjAs(this.injectQueryLimitation(query), asType);
    }

    @Override
    public <R> Optional<R> getObjAsOpt(QueryWrapper query, Class<R> asType) {
        return Optional.ofNullable(this.getObjAs(query, asType));
    }

    @Override
    public List<Object> objList(QueryWrapper query) {
        return super.objList(this.injectQueryLimitation(query));
    }

    @Override
    public <R> List<R> objListAs(QueryWrapper query, Class<R> asType) {
        return super.objListAs(this.injectQueryLimitation(query), asType);
    }

    @Override
    public List<T> list() {
        return this.list(QueryWrapper.create());
    }

    @Override
    public List<T> list(QueryWrapper query) {
        return super.list(this.injectQueryLimitation(query));
    }

    @Override
    public List<T> list(QueryCondition condition) {
        return super.list(this.injectCondition(condition));
    }

    @Override
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return super.listAs(this.injectQueryLimitation(query), asType);
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> ids) {
        return this.list(QueryWrapper.create().in(T::getId, ids));
    }

    @Override
    public List<T> listByMap(Map<String, Object> query) {
        return this.list(QueryWrapper.create(query));
    }

    @Override
    public boolean exists(QueryWrapper query) {
        return super.exists(this.injectQueryLimitation(query));
    }

    @Override
    public boolean exists(QueryCondition condition) {
        return super.exists(this.injectCondition(condition));
    }

    @Override
    public long count() {
        return this.count(QueryWrapper.create());
    }

    @Override
    public long count(QueryWrapper query) {
        return super.count(this.injectQueryLimitation(query));
    }

    @Override
    public long count(QueryCondition condition) {
        return super.count(this.injectCondition(condition));
    }

    @Override
    public Page<T> page(Page<T> page) {
        return this.page(page, QueryWrapper.create());
    }

    @Override
    public Page<T> page(Page<T> page, QueryWrapper query) {
        return super.page(page, this.injectQueryLimitation(query));
    }

    @Override
    public Page<T> page(Page<T> page, QueryCondition condition) {
        return super.page(page, this.injectCondition(condition));
    }

    @Override
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, this.injectQueryLimitation(query), asType);
    }

    @Override
    public QueryWrapper query() {
        return this.injectQueryLimitation(super.query());
    }
}
