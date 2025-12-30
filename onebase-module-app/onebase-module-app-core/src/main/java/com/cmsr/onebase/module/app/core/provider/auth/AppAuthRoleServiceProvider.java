package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleDeptRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDeptDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleUserDO;
import com.cmsr.onebase.module.app.core.dto.auth.RoleMemberDTO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.vo.auth.*;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersReqDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:56
 */
@Slf4j
@Setter
@Service
public class AppAuthRoleServiceProvider {

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private AppAuthRoleUserRepository appAuthRoleUserRepository;

    @Autowired
    private AppAuthRoleDeptRepository appAuthRoleDeptRepository;

    @Autowired
    private DeptApi deptApi;

    public AppAuthRoleDO validateRoleExist(Long id) {
        AppAuthRoleDO authRoleDO = appAuthRoleRepository.getById(id);
        if (authRoleDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_EXISTS);
        }
        return authRoleDO;
    }

    public PageResult<AuthRoleMembersPageRespVO> pageRoleMembers(AuthRoleMembersPageReqVO reqVO) {
        validateRoleExist(reqVO.getRoleId());
        PageResult<RoleMemberDTO> result = appAuthRoleRepository.findRoleMembers(reqVO.getRoleId(), reqVO.getMemberName(), reqVO.getMemberType(), reqVO);
        List<AuthRoleMembersPageRespVO> voResult = result.getList().stream().map(v -> {
            AuthRoleMembersPageRespVO vo = new AuthRoleMembersPageRespVO();
            vo.setId(v.getId());
            vo.setMemberId(v.getMemberId());
            vo.setName(v.getMemberName());
            vo.setAvatar(v.getAvatar());
            vo.setAccount(v.getAccount());
            vo.setCreateSourceText(toCreateSourceText(v.getCreateSource()));
            vo.setType(v.getMemberType());
            vo.setDeptName(v.getDeptName());
            if (RoleMemberDTO.MEMBER_TYPE_USER.equals(v.getMemberType())) {
                vo.setTypeName("成员");
            } else if (v.getIsIncludeChild() != null && v.getIsIncludeChild() == 0) {
                vo.setTypeName("本部门");
            } else if (v.getIsIncludeChild() != null && v.getIsIncludeChild() == 1) {
                vo.setTypeName("本部门及子部门");
            }
            return vo;
        }).toList();
        return new PageResult(voResult, result.getTotal());
    }

    private String toCreateSourceText(String createSource) {
        if (createSource == null || createSource.isEmpty()) {
            return "";
        }
        if ("back".equals(createSource)) {
            return "后台创建";
        }
        if ("self".equals(createSource)) {
            return "自主注册";
        }
        return "";
    }


    @Transactional(rollbackFor = Exception.class)
    public void addRoleUser(AuthRoleAddUserReqVO reqVO) {
        AppAuthRoleDO authRoleDO = validateRoleExist(reqVO.getRoleId());
        appAuthRoleUserRepository.addRoleUser(reqVO.getRoleId(), reqVO.getUserIds());
    }


    public void addRoleDept(AuthRoleAddDeptReqVO reqVO) {
        AppAuthRoleDO authRoleDO = validateRoleExist(reqVO.getRoleId());
        appAuthRoleDeptRepository.addRoleDept(reqVO.getRoleId(), reqVO.getDeptIds(), reqVO.getIsIncludeChild());
    }

    public void deleteRoleMember(AuthRoleDeleteMemberReqVO reqVO) {
        AppAuthRoleDO authRoleDO = validateRoleExist(reqVO.getRoleId());
        for (AuthRoleDeleteMemberReqVO.DeleteMember member : reqVO.getMembers()) {
            if (RoleMemberDTO.MEMBER_TYPE_USER.equalsIgnoreCase(member.getMemberType())) {
                AppAuthRoleUserDO authRoleUserDO = appAuthRoleUserRepository.getById(member.getId());
                if (authRoleUserDO != null) {
                    appAuthRoleUserRepository.removeById(member.getId());
                }
            }
            if (RoleMemberDTO.MEMBER_TYPE_DEPT.equalsIgnoreCase(member.getMemberType())) {
                AppAuthRoleDeptDO authRoleDeptDO = appAuthRoleDeptRepository.getById(member.getId());
                if (authRoleDeptDO != null) {
                    appAuthRoleDeptRepository.removeById(member.getId());
                }
            }
        }
    }

    public DeptAndUsersRespDTO listDeptUsers(AuthRoleDeptAndUsersReqVO reqVO) {
        List<Long> userIds = appAuthRoleUserRepository.findByRoleId(reqVO.getRoleId())
                .stream()
                .map(v -> v.getUserId())
                .toList();
        DeptAndUsersReqDTO deptAndUsersReqDTO = new DeptAndUsersReqDTO();
        deptAndUsersReqDTO.setDeptId(reqVO.getDeptId());
        deptAndUsersReqDTO.setKeywords(reqVO.getKeywords());
        deptAndUsersReqDTO.setExcludeUserIds(userIds);
        deptAndUsersReqDTO.setUserType(reqVO.getUserType());
        return deptApi.getDeptAndUsers(deptAndUsersReqDTO).getData();
    }


}
