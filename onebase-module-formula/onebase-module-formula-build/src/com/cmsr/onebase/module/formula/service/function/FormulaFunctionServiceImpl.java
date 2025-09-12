package com.cmsr.onebase.module.formula.service.function;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.formula.controller.admin.function.vo.FunctionInsertReqVO;
import com.cmsr.onebase.module.formula.controller.admin.function.vo.FunctionListReqVO;
import com.cmsr.onebase.module.formula.controller.admin.function.vo.FunctionPageReqVO;
import com.cmsr.onebase.module.formula.controller.admin.function.vo.FunctionUpdateReqVO;
import com.cmsr.onebase.module.formula.dal.database.FunctionDataRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FunctionDO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.formula.enums.ErrorCodeConstants.*;

/**
 * 函数 Service 实现类
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Service("formulaFunctionService")
@Validated
@Slf4j
public class FormulaFunctionServiceImpl implements FormulaFunctionService {

    @Resource
    private FunctionDataRepository  functionDataRepository;

    @Override
    public Long createFunction(FunctionInsertReqVO createReqVO) {
        // 校验函数名的唯一性
        validateFunctionNameUnique(null, createReqVO.getName());

        // 插入函数
        FunctionDO function = BeanUtils.toBean(createReqVO, FunctionDO.class);
        functionDataRepository.insert(function);

        return function.getId();
    }

    @Override
    public void updateFunction(FunctionUpdateReqVO updateReqVO) {
        // 校验自己存在
        validateFunctionExists(updateReqVO.getId());
        // 校验函数名的唯一性
        validateFunctionNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 更新函数
        FunctionDO updateObj = BeanUtils.toBean(updateReqVO, FunctionDO.class);
        functionDataRepository.updateStrict(updateObj);
    }

    @Override
    public void deleteFunction(Long id) {
        // 校验是否存在
        validateFunctionExists(id);
        // 删除函数
        functionDataRepository.deleteById(id);
    }

    @Override
    public FunctionDO getFunction(Long id) {
        return functionDataRepository.findById(id);
    }

    @Override
    public List<FunctionDO> getFunctionList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return functionDataRepository.findAllByIds(new ArrayList<>(ids));
    }

    @Override
    public List<FunctionDO> getFunctionList(FunctionListReqVO reqVO) {
        return functionDataRepository.findAllByConditions(reqVO.getType(), reqVO.getName(), reqVO.getStatus());
    }

    @Override
    public PageResult<FunctionDO> getFunctionPage(FunctionPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getType() != null) {
            configs.and(org.anyline.entity.Compare.EQUAL, FunctionDO.FIELD_TYPE, reqVO.getType());
        }
        if (reqVO.getName() != null) {
            configs.and(org.anyline.entity.Compare.LIKE, FunctionDO.FIELD_NAME, reqVO.getName());
        }
        if (reqVO.getStatus() != null) {
            configs.and(org.anyline.entity.Compare.EQUAL, FunctionDO.FIELD_STATUS, reqVO.getStatus());
        }

        // 设置分页参数
        PageNavi page = new DefaultPageNavi(reqVO.getPageNo(), reqVO.getPageSize());
        configs.setPageNavi(page);
        configs.order(FunctionDO.CREATE_TIME, org.anyline.entity.Order.TYPE.DESC);

        return functionDataRepository.findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    public void validateFunctionList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得函数信息
        List<FunctionDO> functions = getFunctionList(ids);
        // 校验
        ids.forEach(id -> {
            FunctionDO function = functions.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
            if (function == null) {
                throw exception(FUNCTION_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(function.getStatus())) {
                throw exception(FUNCTION_IS_DISABLE, function.getName());
            }
        });
    }

    @VisibleForTesting
    void validateFunctionExists(Long id) {
        if (id == null) {
            return;
        }
        FunctionDO function = functionDataRepository.findById(id);
        if (function == null) {
            throw exception(FUNCTION_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateFunctionNameUnique(Long id, String name) {
        FunctionDO function = functionDataRepository.findOneByName(name);
        if (function == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的函数
        if (id == null) {
            throw exception(FUNCTION_NAME_DUPLICATE, name);
        }
        if (ObjectUtil.notEqual(function.getId(), id)) {
            throw exception(FUNCTION_NAME_DUPLICATE, name);
        }
    }

}
