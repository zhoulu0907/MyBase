package com.cmsr.onebase.module.system.service.dept;

import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.vo.dept.*;
import com.cmsr.onebase.module.system.vo.user.UserAdminOrDirectorUpdateReqVO;

import java.util.*;

/**
 * 部门 Service 接口
 *
 */
public interface DeptService {

    /**
     * 创建部门
     *
     * @param createReqVO 部门信息
     * @return 部门编号
     */
    Long createDept(DeptSaveReqVO createReqVO);

    /**
     * 更新部门
     *
     * @param updateReqVO 部门信息
     */
    void updateDept(DeptSaveReqVO updateReqVO);

    /**
     * 删除部门
     *
     * @param id 部门编号
     */
    void deleteDept(Long id);

    /**
     * 获得部门信息
     *
     * @param id 部门编号
     * @return 部门信息
     */
    DeptDO getDept(Long id);

    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    List<DeptDO> getDeptList(Collection<Long> ids);

    /**
     * 筛选部门列表
     *
     * @param reqVO 筛选条件请求 VO
     * @return 部门列表
     */
    List<DeptDO> getDeptList(DeptListReqVO reqVO);

    /**
     * 获得指定编号的部门 Map
     *
     * @param ids 部门编号数组
     * @return 部门 Map
     */
    default Map<Long, DeptDO> getDeptMap(Collection<Long> ids) {
        List<DeptDO> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, DeptDO::getId);
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param id 部门编号
     * @return 子部门列表
     */
    default List<DeptDO> getChildDeptList(Long id) {
        return getChildDeptList(Collections.singleton(id));
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param ids 部门编号数组
     * @return 子部门列表
     */
    List<DeptDO> getChildDeptList(Collection<Long> ids);

    /**
     * 获得指定领导者的部门列表
     *
     * @param id 领导者编号
     * @return 部门列表
     */
    List<DeptDO> getDeptListByLeaderUserId(Long id);

    /**
     * 获得所有子部门，从缓存���
     *
     * @param id 父部门编号
     * @return 子部门列表
     */
    Set<Long> getChildDeptIdListFromCache(Long id);

    /**
     * 校验部门们是否有效。如下情况，视为无效：
     * 1. 部门编号不存在
     * 2. 部门被禁用
     *
     * @param ids 角色编号数组
     */
    void validateDeptList(Collection<Long> ids);

    /**
     * 获��部门列表（包含人数统计）
     *
     * @param reqVO 查询条件
     * @return 部门列表（包含人数）
     */
    List<DeptRespVO> getDeptListWithUserCount(DeptListReqVO reqVO);

    /**
     * 获得部门信息（包含人数和领导姓名）
     *
     * @param id 部门编号
     * @return 部门信息
     */
    DeptRespVO getDeptWithUserCountAndLeader(Long id);

    /**
     * 查询部门和用户信息
     *
     * @param reqVO 查询条件
     * @return 部门和用户信息
     */
    DeptAndUsersRespVO getDeptAndUsers(DeptAndUsersReqVO reqVO);

    void updateAdminOrDirector(UserAdminOrDirectorUpdateReqVO reqVO);
    /**
     * 根据用户ID获取所有直属上级部门，包括一级部门
     *
     * @param id 用户/部门ID，根据 idType 决定
     * @param idType ID 类型，参见 IdTypeEnum 枚举
     * @return 所有直属上级部门列表
     */
    List<DeptDO> getParentDeptsListById(Long id, String idType);
    /**
     * 根据部门编号和部门类型，查询部门
     *
     * @param code 部门编号
     * @param type 部门类型
     * @return 部门
     */
    DeptDO findDeptByCodeAndType(DeptSaveReqVO deptRespVO);
    /**
     * 创建第三方部门
     *
     * @param deptRespVO 部门信息
     * @return 部门编号
     */
    Long createThirdDefaultDept(DeptSaveReqVO deptRespVO);

    List<DeptDO> getThirdDept();
}
