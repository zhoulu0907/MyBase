package com.cmsr.onebase.module.system.service.tenant;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.system.vo.tenant.TenantPageReqVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantInsertReqVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantRespVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantUpdateReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantInfoHandler;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantMenuHandler;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 空间 Service 接口
 *
 */
public interface TenantService {

    /**
     * 创建空间
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTenant(@Valid TenantInsertReqVO createReqVO);

    /**
     * 更新空间
     *
     * @param updateReqVO 更新信息
     */
    void updateTenant(@Valid TenantUpdateReqVO updateReqVO);

    /**
     * 更新空间的角色菜单
     *
     * @param tenantId 空间编号
     * @param menuIds  菜单编号数组
     */
    void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds);

    /**
     * 删除空间
     *
     * @param id 编号
     */
    void deleteTenant(Long id);

    /**
     * 获得空间
     *
     * @param id 编号
     * @return 空间
     */
    TenantDO getTenant(Long id);

    /**
     * 获得空间信息（包含应用数量）
     *
     * @param id 编号
     * @return 空间信息
     */
    TenantRespVO getTenantWithAppCount(Long id);

    /**
     * 获得空间分页
     *
     * @param pageReqVO 分页查询
     * @return 空间分页
     */
    PageResult<TenantRespVO> getTenantPage(TenantPageReqVO pageReqVO);

    /**
     * 获得名字对应的空间
     *
     * @param name 空间名
     * @return 空间
     */
    TenantDO getTenantByName(String name);

    /**
     * 获得域名对应的空间
     *
     * @param website 域名
     * @return 空间
     */
    TenantDO getTenantByWebsite(String website);

    /**
     * 获得使用指定套餐的空间数量
     *
     * @param packageId 租户套餐编号
     * @return 租户数量
     */
    Long getTenantCountByPackageId(Long packageId);

    /**
     * 获得指定状态的租户数量
     *
     * @param status 状态
     * @return 租户数量
     */
    Integer getTenantCountByStatus(Integer status);

    /**
     * 获得使用指定套餐的租户数组
     *
     * @param packageId 租户套餐编号
     * @return 租户数组
     */
    List<TenantDO> getTenantListByPackageId(Long packageId);

    /**
     * 获得指定状态的租户列表
     *
     * @param status 状态
     * @return 租户列表
     */
    List<TenantDO> getTenantListByStatus(Integer status);

    /**
     * 进行租户的信息处理逻辑
     * 其中，租户编号从 {@link TenantContextHolder} 上下文中获取
     *
     * @param handler 处理器
     */
    void handleTenantInfo(TenantInfoHandler handler);

    /**
     * 进行租户的菜单处理逻辑
     * 其中，租户编号从 {@link TenantContextHolder} 上下文中获取
     *
     * @param handler 处理器
     */
    void handleTenantMenu(TenantMenuHandler handler);

    /**
     * 获得所有租户
     *
     * @return 租户编号数组
     */
    List<Long> getTenantIdList();

    /**
     * 校验租户是否合法
     *
     * @param id 租户编号
     */
    void validTenant(Long id);

    /**
     * 获取可分配租户数量
     *
     * @return 租户数量
     */
    Long getAvailableAccountCount();
    /**
     * 获取其他租户的所有用户数量
     *
     * @return 其他租户的所有用户数量和
     */
    Long getOtherTenantUserLimitCount(Long tenantId);

    /**
     * 获取当前租户已存在的用户数量
     *
     * @return 获取当前租户已存在的用户数量
     */
    Long getTenantExistUserCount(Long tenantId);

}
