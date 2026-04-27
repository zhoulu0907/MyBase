package com.cmsr.onebase.module.system.convert.dept;

import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersReqDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.system.vo.dept.DeptAndUsersReqVO;
import com.cmsr.onebase.module.system.vo.dept.DeptAndUsersRespVO;
import com.cmsr.onebase.module.system.vo.dept.DeptRespVO;
import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeptConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    DeptAndUsersReqVO toDeptAndUsersReqVO(DeptAndUsersReqDTO reqVO);

    DeptRespDTO toDeptRespDTO(DeptRespVO deptInfo);

    default List<AdminUserRespDTO> toAdminUserRespDTO(List<UserSimpleRespVO> userList) {
        return CollectionUtils.convertList(userList, user -> BeanUtils.toBean(user, AdminUserRespDTO.class));

    }

    default List<DeptRespDTO> toDeptRespDTOList(List<DeptRespVO> deptList) {
        return CollectionUtils.convertList(deptList, dept -> BeanUtils.toBean(dept, DeptRespDTO.class));
    }

    default DeptAndUsersRespDTO toDeptAndUsersRespDTO(DeptAndUsersRespVO result) {
        if (result == null) {
            return null;
        }
        DeptAndUsersRespDTO dto = new DeptAndUsersRespDTO();
        dto.setDeptInfo(toDeptRespDTO(result.getDeptInfo()));
        dto.setDeptList(toDeptRespDTOList(result.getDeptList()));
        dto.setUserList(toAdminUserRespDTO(result.getUserList()));

        return dto;
    }

    DeptAndUsersReqDTO toDeptAndUsersReqDTO(DeptAndUsersReqVO reqVO);
}
