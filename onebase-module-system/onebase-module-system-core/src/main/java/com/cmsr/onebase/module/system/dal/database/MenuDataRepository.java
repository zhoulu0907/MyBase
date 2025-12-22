package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemMenuMapper;
import com.cmsr.onebase.module.system.vo.menu.SystemMenuListReqVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO.COMPONENT_NAME;
import static com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO.NAME;
import static com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO.PARENT_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO.PERMISSION;
import static com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO.STATUS;

/**
 * 菜单数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class MenuDataRepository extends BaseDataServiceImpl<SystemMenuMapper, MenuDO> {

    /**
     * 根据父菜单ID统计子菜单数量
     *
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    public int countByParentId(Long parentId) {
        if (parentId == null) {
            return 0;
        }
        long count = count(query().eq(PARENT_ID, parentId));
        return (int) count;
    }

    /**
     * 根据父菜单ID和菜单名称查找菜单
     *
     * @param parentId 父菜单ID
     * @param name 菜单名称
     * @return 菜单对象
     */
    public MenuDO findOneByParentIdAndName(Long parentId, String name) {
        if (parentId == null || StringUtils.isBlank(name)) {
            return null;
        }
        return getOne(query().eq(PARENT_ID, parentId).eq(NAME, name));
    }

    /**
     * 根据组件名称查找菜单
     *
     * @param componentName 组件名称
     * @return 菜单对象
     */
    public MenuDO findOneByComponentName(String componentName) {
        if (StringUtils.isBlank(componentName)) {
            return null;
        }
        return getOne(query().eq(COMPONENT_NAME, componentName));
    }

    /**
     * 根据权限标识查找菜单列表
     *
     * @param permission 权限标识
     * @return 菜单列表
     */
    public List<MenuDO> findListByPermission(String permission) {
        if (StringUtils.isBlank(permission)) {
            return Collections.emptyList();
        }
        return list(query().eq(PERMISSION, permission));
    }

    /**
     * 根据条件查询菜单列表
     *
     * @param reqVO 查询条件
     * @return 菜单列表
     */
    public List<MenuDO> findList(SystemMenuListReqVO reqVO) {
        if (reqVO == null) {
            return Collections.emptyList();
        }
        return list(query()
                .like(NAME, reqVO.getName(), StringUtils.isNotBlank(reqVO.getName()))
                .likeRight(PERMISSION, reqVO.getCode(), StringUtils.isNotBlank(reqVO.getCode()))
                .eq(STATUS, reqVO.getStatus(), reqVO.getStatus() != null));
    }

    /**
     * 根据 code 查询启用的菜单列表
     *
     * @param codes 查询条件
     * @return 菜单列表
     */
    public List<MenuDO> findAllEnableByCodes(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query()
                .in(PERMISSION, codes)
                .eq(STATUS, CommonStatusEnum.ENABLE.getStatus()));
    }
}
