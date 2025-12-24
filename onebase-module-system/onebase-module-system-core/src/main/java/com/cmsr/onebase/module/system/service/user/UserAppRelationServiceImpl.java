package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.system.dal.database.UserAppRelationDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.user.UserAppRelationDO;
import com.cmsr.onebase.module.system.vo.user.UserAppPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppVO;
import com.cmsr.onebase.module.system.vo.user.UserRelationAppReqVO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
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
    private AppAuthRoleUser appAuthRoleUser;

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
            // 插入
            userAppReqVO.getApplicationIdList().forEach(appId -> {
                // 先删除后插入
                QueryWrapper queryWrapper = new QueryWrapper()
                        .eq(UserAppRelationDO.USER_ID, userAppReqVO.getUserId())
                        .eq(UserAppRelationDO.APPLICATION_ID, appId);
                userAppRelationDataRepository.remove(queryWrapper);

                // 验证是否重复提交，先删除后插入
                UserAppRelationDO corpAppRelationDO = new UserAppRelationDO();
                corpAppRelationDO.setApplicationId(appId);
                corpAppRelationDO.setStatus(CorpStatusEnum.ENABLE.getValue());
                corpAppRelationDO.setUserId(userAppReqVO.getUserId());
                userAppRelationDataRepository.insert(corpAppRelationDO);

                // 添加应用外部用户权限
                ApplicationManager.withoutApplicationCondition(() -> {
                    appAuthRoleUser.grantThirdpartyUserPrivileges(userAppReqVO.getUserId(), appId);
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
}
