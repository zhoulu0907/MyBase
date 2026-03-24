package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUserService;
import com.cmsr.onebase.module.system.dal.database.UserAppRelationDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.external.SystemExternalUserDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.UserAppRelationDO;
import com.cmsr.onebase.module.system.service.external.SystemExternalUserService;
import com.cmsr.onebase.module.system.vo.user.*;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 用户应用关联 Service 实现类
 */
@Slf4j
@Service("userAppRelationService")
@Validated
public class UserAppRelationServiceImpl implements UserAppRelationService {

    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private UserService userService;

    @Resource
    private UserAppRelationDataRepository userAppRelationDataRepository;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Autowired
    private AppAuthRoleUserService appAuthRoleUserService;

    @Resource
    private SystemExternalUserService systemExternalUserService;

    @Override
    public List<UserAppRelationDO> getUserAppRelationList(UserAppPageReqVO userAppPageReqVO) {
        return userAppRelationDataRepository.getUserAppRelationList(userAppPageReqVO);
    }

    @Override
    public List<UserAppVO> getAppByUserId(Long userId) {
        List<UserAppRelationDO> userAppRelationDO = userAppRelationDataRepository.getUserAppRelationByUserId(userId);
        if (CollectionUtils.isEmpty(userAppRelationDO)) {
            return Collections.emptyList();
        }
        // 通过关系获取应用id
        List<Long> applicationIds = userAppRelationDO.stream().map(UserAppRelationDO::getApplicationId).toList();
        List<ApplicationDTO> applicationDTOList = appApplicationApi.findAppApplicationByAppIds(applicationIds);
        return applicationDTOList.stream()
                .map(applicationDTO -> {
                    UserAppVO userAppVO = BeanUtils.toBean(applicationDTO, UserAppVO.class);
                    userAppVO.setAppId(applicationDTO.getId());
                    return userAppVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createUserAppRelation(UserAppRelationInertReqVO userAppReqVO) {

        if (null != userAppReqVO.getUserId()) {
            userService.updateUserByUserAppReqVO(userAppReqVO);
        }

        // 保存关联关联
        if (!CollectionUtils.isEmpty(userAppReqVO.getApplicationIdList())) {
            // 先删除后插入
            QueryWrapper queryWrapper = new QueryWrapper()
                    .eq(UserAppRelationDO.USER_ID, userAppReqVO.getUserId());
            userAppRelationDataRepository.remove(queryWrapper);
            // 插入
            userAppReqVO.getApplicationIdList().forEach(appId -> {
                // 验证是否重复提交，先删除后插入
                UserAppRelationDO userAppRelationDO = new UserAppRelationDO();
                userAppRelationDO.setApplicationId(appId);
                userAppRelationDO.setStatus(CorpStatusEnum.ENABLE.getValue());
                userAppRelationDO.setUserId(userAppReqVO.getUserId());
                userAppRelationDataRepository.insert(userAppRelationDO);

                // 添加应用外部用户权限
                ApplicationManager.withoutApplicationCondition(() -> {
                    appAuthRoleUserService.grantThirdpartyUserPrivileges(userAppReqVO.getUserId(), appId);
                });
            });

            // TODO: 添加应用外部用户相关代码
        }
    }

    @Override
    public List<ApplicationDTO> getUserNoRelationAppList(UserRelationAppReqVO relationAppReqVO) {
        List<ApplicationDTO> applicationDTOList = appApplicationApi.findAppApplicationByAppName(relationAppReqVO.getAppName());
        if (null == relationAppReqVO.getUserId()) {
            // 用于用户创建时拉取全部应用
            return applicationDTOList;
        }
        // 获取用户已关联的数据
        List<UserAppRelationDO> userAppRelationDOList = userAppRelationDataRepository.getUserAppRelationByUserId(relationAppReqVO.getUserId());
        if (userAppRelationDOList.isEmpty()) {
            return applicationDTOList;
        }
        // 获取已关联的应用ID集合
        Set<Long> relatedAppIds = userAppRelationDOList.stream()
                .map(UserAppRelationDO::getApplicationId)
                .collect(Collectors.toSet());

        // 过滤掉已关联的应用
        return applicationDTOList.stream()
                .filter(app -> !relatedAppIds.contains(app.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public UserAppCountRespVO getUserAppCount(UserAppCountReqVO reqVO) {

        UserAppCountRespVO respVO = new UserAppCountRespVO();
        //1.查询用户关联表有无关联用户
        SystemExternalUserDO systemExternalUserDO = systemExternalUserService.getByExternalUserId(reqVO.getUserId(), reqVO.getPlatformType(), reqVO.getTenantId());
        if (systemExternalUserDO != null) {
            //2.查询用户关联表关联用户关联应用数量
            Long count = appApplicationApi.countApplicationByTenantIdAndUserId(systemExternalUserDO.getObTenantId(), systemExternalUserDO.getObUserId());
            respVO.setAppCount(Objects.requireNonNullElse(count, NumberUtils.LONG_ZERO));
        } else {
            respVO.setAppCount(NumberUtils.LONG_ZERO);
        }
        return respVO;
    }
}
