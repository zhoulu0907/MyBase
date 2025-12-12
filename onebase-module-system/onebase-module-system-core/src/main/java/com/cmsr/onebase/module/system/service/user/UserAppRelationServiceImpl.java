package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserAppRelationDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.UserAppRelationDO;
import com.cmsr.onebase.module.system.vo.corp.CorpAppVo;
import com.cmsr.onebase.module.system.vo.corp.CorpApplicationRespVO;
import com.cmsr.onebase.module.system.vo.corp.CorpRespVO;
import com.cmsr.onebase.module.system.vo.user.UserAppPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppVO;
import com.cmsr.onebase.module.system.vo.user.UserApplicationRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

/**
 * 用户应用关联 Service 实现类
 */
@Slf4j
@Service("userAppRelationService")
@Validated
public class UserAppRelationServiceImpl implements UserAppRelationService {


    @Resource
    private UserAppRelationDataRepository userAppRelationDataRepository;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Override
    public PageResult<UserApplicationRespVO> getUserAppRelationPage(UserAppPageReqVO userAppPageReqVO) {
        PageResult<UserAppRelationDO> pageResult = userAppRelationDataRepository.selectPage(userAppPageReqVO);
        List<UserAppRelationDO> corpList = pageResult.getList();
        if (CollectionUtils.isEmpty(corpList)) {
            return new PageResult<>(Collections.emptyList(), pageResult.getTotal());
        }
        // TODO 获取用户名称
        // TODO 获取应用名称
        List<UserApplicationRespVO> filteredList = pageResult.getList().stream()
                .map(userApp -> {
                    UserApplicationRespVO userAppVO = new UserApplicationRespVO();
                    userAppVO.setUserId(userApp.getUserId());
                    return userAppVO;
                })
                .collect(Collectors.toList());
        // 返回过滤后的结果和总数
        return new PageResult<>(filteredList, pageResult.getTotal());
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
    public void createUserAppRelation(UserAppRelationInertReqVO userAppReqVO) {
        if (!CollectionUtils.isEmpty(userAppReqVO.getApplicationIdList())) {
            // 插入
            userAppReqVO.getApplicationIdList().forEach(appId -> {
                // 先删除后插入
                ConfigStore configs = new DefaultConfigStore();
                configs.eq(UserAppRelationDO.USER_ID, userAppReqVO.getUserId());
                configs.eq(CorpAppRelationDO.APPLICATION_ID, appId);
                userAppRelationDataRepository.deleteByConfig(configs);

                // 验证是否重复提交，先删除后插入
                UserAppRelationDO corpAppRelationDO = new UserAppRelationDO();
                corpAppRelationDO.setApplicationId(appId);
                corpAppRelationDO.setStatus(CorpStatusEnum.ENABLE.getValue());
                corpAppRelationDO.setUserId(userAppReqVO.getUserId());
                userAppRelationDataRepository.insert(corpAppRelationDO);
            });
        }
    }
}
