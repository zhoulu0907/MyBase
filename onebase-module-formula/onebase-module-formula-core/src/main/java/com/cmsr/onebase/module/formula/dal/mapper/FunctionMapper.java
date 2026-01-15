package com.cmsr.onebase.module.formula.dal.mapper;

import com.cmsr.onebase.module.formula.dal.dataflexdo.FunctionDO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

public interface FunctionMapper extends BaseMapper<FunctionDO> {

    List<FunctionDO> selectListByCondition(QueryWrapper queryWrapper, Class<FunctionDO> functionDOClass);
}
