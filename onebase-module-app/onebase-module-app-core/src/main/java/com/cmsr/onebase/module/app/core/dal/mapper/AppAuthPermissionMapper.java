package com.cmsr.onebase.module.app.core.dal.mapper;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 应用权限-基础总表 映射层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public interface AppAuthPermissionMapper extends BaseMapper<AppAuthPermissionDO> {

    List<AppAuthPermissionDO> findByAppIdAndRoleIdsAndMenuId(@Param("applicationId") Long applicationId,
                                                             @Param("roleIds") Set<Long> roleIds,
                                                             @Param("menuId") Long menuId);

    List<AppAuthPermissionDO> findByAppIdAndRoleIds(@Param("applicationId") Long applicationId,
                                                    @Param("roleIds") Set<Long> roleIds);
}
