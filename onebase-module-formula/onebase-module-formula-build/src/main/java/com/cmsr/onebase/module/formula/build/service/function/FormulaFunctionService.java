package com.cmsr.onebase.module.formula.build.service.function;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.formula.build.controller.function.vo.FunctionInsertReqVO;
import com.cmsr.onebase.module.formula.build.controller.function.vo.FunctionListReqVO;
import com.cmsr.onebase.module.formula.build.controller.function.vo.FunctionPageReqVO;
import com.cmsr.onebase.module.formula.build.controller.function.vo.FunctionUpdateReqVO;
import com.cmsr.onebase.module.formula.dal.dataobject.FunctionDO;

import java.util.Collection;
import java.util.List;

/**
 * 函数 Service 接口
 *
 * @author matianyu
 * @date 2025-08-28
 */
public interface FormulaFunctionService {

    /**
     * 创建函数
     *
     * @param createReqVO 创建函数 Request VO
     * @return 函数编号
     */
    Long createFunction(FunctionInsertReqVO createReqVO);

    /**
     * 更新函数
     *
     * @param updateReqVO 更新函数 Request VO
     */
    void updateFunction(FunctionUpdateReqVO updateReqVO);

    /**
     * 删除函数
     *
     * @param id 函数编号
     */
    void deleteFunction(Long id);

    /**
     * 获得函数
     *
     * @param id 函数编号
     * @return 函数
     */
    FunctionDO getFunction(Long id);

    /**
     * 获得函数列表
     *
     * @param ids 函数编号列表
     * @return 函数列表
     */
    List<FunctionDO> getFunctionList(Collection<Long> ids);

    /**
     * 获得函数列表
     *
     * @param reqVO 查询条件
     * @return 函数列表
     */
    List<FunctionDO> getFunctionList(FunctionListReqVO reqVO);

    /**
     * 获得函数分页
     *
     * @param reqVO 查询条件
     * @return 函数分页
     */
    PageResult<FunctionDO> getFunctionPage(FunctionPageReqVO reqVO);

    /**
     * 校验函数是否存在
     *
     * @param ids 函数编号列表
     */
    void validateFunctionList(Collection<Long> ids);

}
