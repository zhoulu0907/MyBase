package com.cmsr.onebase.module.app.core.dal.mapper;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 数据权限-权限组配置表 映射层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public interface AppAuthDataGroupMapper extends BaseMapper<AppAuthDataGroupDO> {


    List<AppAuthDataGroupDO> findByAppIdAndRoleIdsAndMenuId(@Param("applicationId") Long applicationId,
                                                            @Param("roleIds") Set<Long> roleIds,
                                                            @Param("menuId") Long menuId);
}