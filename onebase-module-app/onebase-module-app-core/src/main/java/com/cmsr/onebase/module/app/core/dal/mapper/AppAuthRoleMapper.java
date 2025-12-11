package com.cmsr.onebase.module.app.core.dal.mapper;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dto.auth.RoleMemberDTO;
import com.cmsr.onebase.module.app.core.vo.app.AppUserPhotoDTO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用角色 映射层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public interface AppAuthRoleMapper extends BaseMapper<AppAuthRoleDO> {

    List<RoleMemberDTO> selectRoleUsers(@Param("roleId") Long roleId, @Param("memberName") String memberName);

    List<RoleMemberDTO> selectRoleDepts(@Param("roleId") Long roleId, @Param("memberName") String memberName);

    List<RoleMemberDTO> selectRoleMembers(@Param("roleId") Long roleId, @Param("memberName") String memberName);

    List<AppUserPhotoDTO> findUserPhotoList(List<Long> appIds);

}
