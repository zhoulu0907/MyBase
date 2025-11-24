package com.cmsr.onebase.module.system.api.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.system.api.user.dto.UserByDeptPageReqDTO;
import com.cmsr.onebase.module.system.api.user.dto.UserSimpleRespDTO;
import com.cmsr.onebase.module.system.convert.user.UserConvert;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.user.UserByDeptPageReqVO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class AdminUserApiImpl implements AdminUserApi {

    @Resource
    private UserService userService;
    @Resource
    private DeptService deptService;

    @Override
    public CommonResult<AdminUserRespDTO> getUser(Long id) {
        AdminUserDO user = userService.getUser(id);
        return success(BeanUtils.toBean(user, AdminUserRespDTO.class));
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListBySubordinate(Long id) {
        // 1.1 获取用户负责的部门
        AdminUserDO user = userService.getUser(id);
        if (user == null) {
            return success(Collections.emptyList());
        }
        ArrayList<Long> deptIds = new ArrayList<>();
        DeptDO dept = deptService.getDept(user.getDeptId());
        if (dept == null) {
            return success(Collections.emptyList());
        }
        if (ObjUtil.notEqual(dept.getLeaderUserId(), id)) { // 校验为管理员
            return success(Collections.emptyList());
        }
        deptIds.add(dept.getId());
        // 1.2 获取所有子部门
        List<DeptDO> childDeptList = deptService.getChildDeptList(dept.getId());
        if (CollUtil.isNotEmpty(childDeptList)) {
            deptIds.addAll(convertSet(childDeptList, DeptDO::getId));
        }

        // 2. 获取部门对应的用户信息
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        users.removeIf(item -> ObjUtil.equal(item.getId(), id)); // 排除自己
        return success(BeanUtils.toBean(users, AdminUserRespDTO.class));
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserList(Collection<Long> ids) {
        List<AdminUserDO> users = userService.getUserList(ids);
        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(users, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convert2DTOList(users, deptMap));
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListByDeptIds(Collection<Long> deptIds) {
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        return success(BeanUtils.toBean(users, AdminUserRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> validateUserList(Collection<Long> ids) {
        userService.validateUserList(ids);
        return success(true);
    }

    @Override
    public CommonResult<PageResult<UserSimpleRespDTO>> getUserPageByDept(UserByDeptPageReqDTO reqDTO) {
        PageResult<AdminUserDO>  result = userService.getUserByDeptPage(BeanUtils.toBean(reqDTO, UserByDeptPageReqVO.class));
        return success(BeanUtils.toBean(result, UserSimpleRespDTO.class));
    }

}
