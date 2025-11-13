package com.cmsr.onebase.module.system.api.dept;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersReqDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.enums.dept.IdTypeEnum;
import com.cmsr.onebase.module.system.vo.dept.DeptAndUsersRespVO;
import com.cmsr.onebase.module.system.convert.dept.DeptConvert;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptService deptService;

    @Override
    public CommonResult<DeptRespDTO> getDept(Long id) {
        DeptDO dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DeptRespDTO.class));
    }

    @Override
    public CommonResult<List<DeptRespDTO>> getDeptList(Collection<Long> ids) {
        List<DeptDO> depts = deptService.getDeptList(ids);
        return success(BeanUtils.toBean(depts, DeptRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> validateDeptList(Collection<Long> ids) {
        deptService.validateDeptList(ids);
        return success(true);
    }

    @Override
    public CommonResult<DeptAndUsersRespDTO> getDeptAndUsers(DeptAndUsersReqDTO reqVO) {
        DeptAndUsersRespVO result = deptService.getDeptAndUsers(DeptConvert.INSTANCE.toDeptAndUsersReqVO(reqVO));
        return success(DeptConvert.INSTANCE.toDeptAndUsersRespDTO(result));
    }

    @Override
    public CommonResult<List<DeptRespDTO>> getChildDeptList(Long id) {
        List<DeptDO> depts = deptService.getChildDeptList(id);
        return success(BeanUtils.toBean(depts, DeptRespDTO.class));
    }

    @Override
    public CommonResult<List<DeptRespDTO>> getParentDeptsListByUserId(Long userId) {
        List<DeptDO> deptDOList = deptService.getParentDeptsListById(userId, IdTypeEnum.USER.getCode());
        return success(BeanUtils.toBean(deptDOList, DeptRespDTO.class));
    }
}
