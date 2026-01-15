package com.cmsr.onebase.module.formula.dal.dataflex;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.formula.dal.dataflexdo.FunctionDO;
import com.cmsr.onebase.module.formula.dal.mapper.FunctionMapper;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 函数数据访问层
 *
 * 基于 MyBatis-Flex 实现函数相关的数据操作
 *
 * @author
 */
@Repository
public class FunctionDataRepository extends ServiceImpl<FunctionMapper, FunctionDO> {

    /**
     * 根据条件查询函数列表
     *
     * @param type   函数类型
     * @param name   函数名称（模糊匹配）
     * @param status 函数状态
     * @return 函数列表
     */
    public List<FunctionDO> findAllByConditions(String type, String name, Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (type != null) {
            queryWrapper.eq(FunctionDO.FIELD_TYPE, type);
        }
        if (name != null) {
            queryWrapper.like(FunctionDO.FIELD_NAME, name);
        }
        if (status != null) {
            queryWrapper.eq(FunctionDO.FIELD_STATUS, status);
        }

        return this.list(queryWrapper);
    }

    /**
     * 分页查询函数
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @param type     函数类型
     * @param name     函数名称（模糊匹配）
     * @param status   函数状态
     * @return 分页结果
     */
    public PageResult<FunctionDO> findPageWithConditions(int pageNo, int pageSize, String type, String name, Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq(FunctionDO.FIELD_TYPE, type);
        }
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(FunctionDO.FIELD_NAME, name);
        }
        if (status != null) {
            queryWrapper.eq(FunctionDO.FIELD_STATUS, status);
        }

        // 按创建时间降序排序
        queryWrapper.orderBy("create_time", false);

        Page<FunctionDO> page = this.page(new Page<>(pageNo, pageSize), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());

    }
}