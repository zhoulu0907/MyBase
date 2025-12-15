package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.dal.dataobject.user.UserAppRelationDO;
import com.cmsr.onebase.module.system.vo.user.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 用户应用关联 Service 接口
 *
 */
public interface UserAppRelationService {
    /**
     * 获得用户授权应用列表-分页
     *
     * @param userAppPageReqVO 获取用户授权应用列表-分页请求参数
     * @return 用户授权应用列表-分页结果
     */
    List<UserAppRelationDO> getUserAppRelationList(UserAppPageReqVO userAppPageReqVO);

    /**
     * 获得用户授权应用列表-分页
     * /
     */
    List<UserAppVO> getAppByUserId(Long userId);


    void createUserAppRelation(@Valid UserAppRelationInertReqVO userAppRelationInertReqVO);

    List<ApplicationDTO> getUserNoRelationAppList(UserRelationAppReqVO relationAppReqVO);
}
