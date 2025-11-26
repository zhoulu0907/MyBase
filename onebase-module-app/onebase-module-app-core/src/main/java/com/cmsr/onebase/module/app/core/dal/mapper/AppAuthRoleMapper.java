package com.cmsr.onebase.module.app.core.dal.mapper;

import com.cmsr.onebase.module.app.core.dto.auth.RoleMemberDTO;
import com.mybatisflex.core.BaseMapper;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.mybatisflex.core.paginate.Page;

/**
 * 应用角色 映射层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public interface AppAuthRoleMapper extends BaseMapper<AppAuthRoleDO> {

    Page<RoleMemberDTO> selectRoleMembers(Page page, Long roleId, String memberName);

}
