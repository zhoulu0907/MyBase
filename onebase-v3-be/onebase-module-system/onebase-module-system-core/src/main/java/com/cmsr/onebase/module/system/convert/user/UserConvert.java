package com.cmsr.onebase.module.system.convert.user;


import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.collection.MapUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.system.vo.dept.DeptSimpleRespVO;
import com.cmsr.onebase.module.system.vo.post.PostSimpleRespVO;
import com.cmsr.onebase.module.system.vo.role.RoleSimpleRespVO;
import com.cmsr.onebase.module.system.vo.user.UserProfileRespVO;
import com.cmsr.onebase.module.system.vo.user.UserRespVO;
import com.cmsr.onebase.module.system.vo.user.UserDeptSimpleRespVO;
import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.PostDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    default List<UserRespVO> convertList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> convert(user, deptMap.get(user.getDeptId())));
    }

    default UserRespVO convert(AdminUserDO user, DeptDO dept) {
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }
        return userVO;
    }

    default List<UserDeptSimpleRespVO> convertSimpleList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserDeptSimpleRespVO userVO = BeanUtils.toBean(user, UserDeptSimpleRespVO.class);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptName(dept.getName()));
            return userVO;
        });
    }

    default UserProfileRespVO convert(AdminUserDO user, List<RoleDO> userRoles,
                                      DeptDO dept, List<PostDO> posts) {
        UserProfileRespVO userVO = BeanUtils.toBean(user, UserProfileRespVO.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleRespVO.class));
        userVO.setDept(BeanUtils.toBean(dept, DeptSimpleRespVO.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleRespVO.class));
        return userVO;
    }

    /**
     * 转换用户信息，包含部门和角色信息
     *
     * @param user 用户信息
     * @param dept 部门信息
     * @param roles 角色列表
     * @return 用户响应对象
     */
    default UserRespVO convert(AdminUserDO user, DeptDO dept, List<RoleDO> roles) {
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }
        userVO.setRoles(convertRoles(roles));
        return userVO;
    }

    /**
     * 转换角色列表为角色响应对象列表
     *
     * @param roles 角色列表
     * @return 角色响应对象列表
     */
    default List<UserRespVO.UserRoleRespVO> convertRoles(List<RoleDO> roles) {
        if (org.springframework.util.CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }

        return roles.stream()
                .map(role -> {
                    UserRespVO.UserRoleRespVO roleResp = new UserRespVO.UserRoleRespVO();
                    roleResp.setId(role.getId());
                    roleResp.setName(role.getName());
                    return roleResp;
                })
                .collect(Collectors.toList());
    }

    List<UserSimpleRespVO> convertList(List<AdminUserDO> list);

    default List<AdminUserRespDTO> convert2DTOList(List<AdminUserDO> users, Map<Long, DeptDO> deptMap){
        return CollectionUtils.convertList(users, user -> {
            AdminUserRespDTO userDTO = BeanUtils.toBean(user, AdminUserRespDTO.class);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userDTO.setDeptName(dept.getName()));
            return userDTO;
        });
    }
}
