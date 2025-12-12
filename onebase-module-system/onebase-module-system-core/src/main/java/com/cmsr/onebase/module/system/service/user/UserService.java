package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.module.system.vo.auth.AuthRegisterReqVO;
import com.cmsr.onebase.module.system.vo.auth.ThirdAuthLoginReqVO;
import com.cmsr.onebase.module.system.vo.dept.DeptSimpleListRespVO;
import com.cmsr.onebase.module.system.vo.user.UserProfileUpdatePasswordReqVO;
import com.cmsr.onebase.module.system.vo.user.UserProfileUpdateReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.vo.user.*;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户 Service 接口
 *
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param createReqVO 用户信息
     * @return 用户编号
     */
    Long createUser(@Valid UserInsertReqVO createReqVO);


    /**
     * 创建用户
     *
     * @param createReqVO 用户信息
     * @return 用户编号
     */
    Long createCorpAdminUser(@Valid AdminUserDO createReqVO);

    /**
     * 创建平台用户
     *
     * @param createReqVO 用户信息
     * @return 用户编号
     */
    Long createPlatformUser(UserInsertReqVO createReqVO);

    /**
     * 注册用户
     *
     * @param registerReqVO 用户信息
     * @return 用户编号
     */
    Long registerUser(@Valid AuthRegisterReqVO registerReqVO);

    /**
     * 修改用户
     *
     * @param updateReqVO 用户信息
     */
    void updateUser(@Valid UserUpdateReqVO updateReqVO);

    /**
     * 修改用户管理员类型
     *
     * @param adminType 修改管理员类型
     * @param id        用户编号
     */
    void updateAdminType(Long id,  Integer adminType);


    /**
     * 修改用户
     *
     * @param email 修改平台管理员邮箱
     * @param id    用户编号
     */
    void updatePlatformUserEmail(Long id, String email);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id      用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(Long id, String loginIp);

    /**
     * 修改用户个人信息
     *
     * @param id    用户编号
     * @param reqVO 用户个人信息
     */
    void updateUserProfile(Long id, @Valid UserProfileUpdateReqVO reqVO);

    /**
     * 修改用户个人密码
     *
     * @param id    用户编号
     * @param reqVO 更新用户个人密码
     */
    void updateUserPassword(Long id, @Valid UserProfileUpdatePasswordReqVO reqVO);

    /**
     * 修改密码
     *
     * @param id       用户编号
     * @param password 密码
     */
    void updateUserPassword(Long id, String password);

    /**
     * 修改状态
     *
     * @param id     用户编号
     * @param status 状态
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 删除用户
     *
     * @param id 用户编号
     */
    void deleteUser(Long id);

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    AdminUserDO getUserByUsername(String username);

    /**
     * 通过手机号获取用户
     *
     * @param mobile 手机号
     * @return 用户对象信息
     */
    AdminUserDO getUserByMobile(String mobile);

    /**
     * 获得用户分页列表
     *
     * @param reqVO 分页条件
     * @return 分页列表
     */
    PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO);

    PageResult<AdminUserDO> getSimpleEnableUserPage(UserSimplePageReqVO reqVO);

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    AdminUserDO getUser(Long id);

    /**
     * 获得指定部门的用户数组
     *
     * @param deptIds 部门数组
     * @return 用户数组
     */
    List<AdminUserDO> getUserListByDeptIds(Collection<Long> deptIds);

    List<AdminUserDO> getUserListNoDept();

    /**
     * 获得指定岗位的用户数组
     *
     * @param postIds 岗位数组
     * @return 用户数组
     */
    List<AdminUserDO> getUserListByPostIds(Collection<Long> postIds);

    /**
     * 获得用户列表
     *
     * @param ids 用户编号数组
     * @return 用户列表
     */
    List<AdminUserDO> getUserList(Collection<Long> ids);

    /**
     * 不传租户tenant_id，获得用户列表，
     *
     * @param ids 用户编号数组
     * @return 用户列表
     */
    List<AdminUserDO> getUserListByIgnoreTenantId(Collection<Long> ids);

    /**
     * 校验用户们是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param ids 用户编号数组
     */
    void validateUserList(Collection<Long> ids);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<Long, AdminUserDO> getUserMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        return CollectionUtils.convertMap(getUserList(ids), AdminUserDO::getId);
    }

    /**
     * 获得用户列表，基于昵称模糊匹配
     *
     * @param nickname 昵称
     * @return 用户列表
     */
    List<AdminUserDO> getUserListByNickname(String nickname);

    /**
     * 批量导入用户
     *
     * @param importUsers     导入用户列表
     * @param isUpdateSupport 是否支持更新
     * @return 导入结果
     */
    UserImportRespVO importUserList(List<UserImportExcelVO> importUsers, boolean isUpdateSupport);

    /**
     * 获得指定状态的用户们
     *
     * @param status 状态
     * @return 用户们
     */
    List<AdminUserDO> getUserListByStatus(Integer status, String userNickName);

    /**
     * 获取所有平台管理员列表
     *
     * @param userSearchReqVO
     * @return 用户们
     */
    List<AdminUserDO> getPlatformAdminListByStatus(UserSearchReqVO userSearchReqVO);


    /**
     * 判断密码是否匹配
     *
     * @param rawPassword     未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 获得指定状态的租户数量
     *
     * @param status 状态
     * @return 用户数量
     */
    Integer getUserCountByStatus(Integer status);

    /**
     * 批量获取部门人数统计
     *
     * @param deptIds 部门编号集合
     * @return 部门ID与人数的映射关系
     */
    Map<Long, Integer> getUserCountByDeptIds(Collection<Long> deptIds);

    /**
     * 批量获取部门人数统计（包含下级部门）
     *
     * @param deptIds 部门编号集合
     * @return 部门ID与人数的映射关系（包含该部门及其所有下级部门的人数）
     */
    Map<Long, Integer> getUserCountByDeptIdsIncludeChildren(Collection<Long> deptIds);

    /**
     * 获取用户详情，包含角色信息
     *
     * @param id 用户ID
     * @return 用户详情响应对象
     */
    UserRespVO getUserWithRoles(Long id);

    List<String> getUserRoleByRoleIdAndTenantId(Long id, Long tenantId);

    Map<Long, Integer> getTenantExistUserCountByIds(List<Long> tenantIds);

    /**
     * 获取指定部门的直属用户简要信息（分页）
     *
     * @param reqVO 分页条件
     * @return 用户简要信息分页列表
     */
    PageResult<AdminUserDO> getUserByDeptPage(UserByDeptPageReqVO reqVO);

    boolean findAdminByRoleIdAndUserId(Long roleId, Long userId);

    Long getUserCountByCorpId(Long id);
    /**
     * 验证企业用户信息
     *
     * @param corpAdminReqVO 分页条件
     * @return 用户简要信息分页列表
     */
    void checkCorpAdminUser(AdminUserDO corpAdminReqVO);

    Map<Long, Integer> getCorpExistUserCountByCorpIds(List<Long> corpIds);

    /**
     * 获取指定部门的直属用户简要信息（不分页）
     *
     * @param
     * @return 用户简要信息分页列表
     */
    List<AdminUserDO> getUserListByStatusAndDeptId(DeptSimpleListRespVO reqVO);

    /**
     * 转换用户数据信息
     * @param pageResult
     * @return
     */
    List<UserRespVO> getConvertUserPage(PageResult<AdminUserDO> pageResult);

    /**
     * 忘记密码
     * @param reqVO
     */
    void forgetPassword(@Valid UserForgetPasswordReqVO reqVO);

    /**
     * 创建第三方用户
     * @param reqVO
     * @return
     */
    AdminUserDO createThirdUser(ThirdAuthLoginReqVO reqVO);
    /**
     * 补充用户信息
     * @param reqVO
     * @return
     */
    Long supplementUser(ThirdSupplementUserReqVO reqVO);
    /**
     * 创建用户并关联应用
     * @param reqVO
     * @return
     */
    Long createUserAndUserAppRelation( ThirdUserAppCombinedInsertReqVO reqVO);
    /**
     * 更新用户并关联应用
     * @param reqVO
     * @return
     */
    Long updateUserAndUserAppRelation(ThirdUserAppCombinedUpdateReqVO reqVO);
}
