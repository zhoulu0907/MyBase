package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.service.auth.AppAuthRoleService;
import com.cmsr.onebase.module.app.build.service.menu.BuildAppMenuService;
import com.cmsr.onebase.module.app.build.service.version.AppDataManager;
import com.cmsr.onebase.module.app.build.util.AppUtils;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.AppPublishEnum;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
import com.cmsr.onebase.module.app.core.vo.app.AppUserPhotoDTO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationNavigationConfigVO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.core.vo.tag.TagRespVO;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.etl.api.EtlDataManager;
import com.cmsr.onebase.module.flow.api.FlowDataManager;
import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import com.cmsr.onebase.module.metadata.api.version.MetadataDataManagerApi;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author：huangjie
 * @Date：2025/7/23 17:11
 */
@Setter
@Service
@Validated
@Slf4j
public class AppApplicationServiceImpl implements AppApplicationService {

    @Autowired
    private UidGenerator uidGenerator;

    @Autowired
    private AppApplicationRepository applicationRepository;

    @Autowired
    private AppApplicationTagRepository applicationTagRepository;

    @Autowired
    private AppTagRepository tagRepository;

    @Autowired
    private AppVersionRepository versionRepository;

    @Autowired
    private AppCommonService appCommonService;

    @Autowired
    private AppAuthRoleService authRoleService;

    @Autowired
    private MetadataDatasourceApi metadataDatasourceApi;

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private BuildAppMenuService buildAppMenuService;

    @Autowired
    private AppDataManager appDataManager;

    @Autowired
    private FlowDataManager flowDataManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EtlDataManager etlDataManager;

    @Autowired
    private BpmDataManager bpmDataManager;

    @Autowired
    private MetadataDataManagerApi metadataDataManagerApi;

    @Override
    public PageResult<ApplicationRespVO> getApplicationPage(ApplicationPageReqVO pageReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageResult<AppApplicationDO> pageResult = applicationRepository.selectPage(pageReqVO, userId);
        List<ApplicationRespVO> respVOS = pageResult.getList().stream().map(v -> {
            ApplicationRespVO bean = BeanUtils.toBean(v, ApplicationRespVO.class);
            bean.setAppStatusText(AppStatusEnum.getText(v.getAppStatus()));
            return bean;
        }).toList();
        enrichTags(respVOS);
        enrichUser(respVOS);
        enrichUserPhoto(respVOS);
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    private void enrichTags(List<ApplicationRespVO> respVOS) {
        List<Long> appIds = respVOS.stream().map(ApplicationRespVO::getId).collect(Collectors.toList());
        List<TagRespVO> appTagDOS = tagRepository.selectTagVoByAppIds(appIds);
        for (ApplicationRespVO respVO : respVOS) {
            List<TagRespVO> tagRespVOS = appTagDOS.stream()
                    .filter(tagDO -> tagDO.getApplicationId().equals(respVO.getId()))
                    .toList();
            respVO.setTags(tagRespVOS);
        }
    }

    private void enrichUser(List<ApplicationRespVO> respVOS) {
        Set<Long> userIds = respVOS.stream()
                .flatMap(vo -> Stream.of(vo.getCreator(), vo.getUpdater()))
                .collect(Collectors.toSet());
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(userIds);
        for (ApplicationRespVO respVO : respVOS) {
            respVO.setCreateUser(userHelper.getUserNickname(respVO.getCreator()));
            respVO.setUpdateUser(userHelper.getUserNickname(respVO.getUpdater()));
        }
    }

    private void enrichUserPhoto(List<ApplicationRespVO> respVOS) {
        List<Long> appIds = respVOS.stream().map(ApplicationRespVO::getId).collect(Collectors.toList());
        Map<Long, List<AppUserPhotoDTO>> userListMap = appAuthRoleRepository.findUserPhotoList(appIds);
        for (ApplicationRespVO respVO : respVOS) {
            respVO.setUserPhotoList(userListMap.get(respVO.getId()));
        }
    }


    private List<TagRespVO> queryAppTags(Long appId) {
        List<Long> tagIds = applicationTagRepository.findTagIdsByApplicationId(appId);
        if (CollectionUtils.isEmpty(tagIds)) {
            return Collections.emptyList();
        }
        return tagRepository.listByIds(tagIds).stream()
                .map(v -> BeanUtils.toBean(v, TagRespVO.class))
                .toList();
    }

    @Override
    public ApplicationRespVO getApplication(Long id) {
        AppApplicationDO applicationDO = applicationRepository.getById(id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        ApplicationRespVO respVO = BeanUtils.toBean(applicationDO, ApplicationRespVO.class);
        respVO.setAppStatusText(AppStatusEnum.getText(respVO.getAppStatus()));
        respVO.setTags(queryAppTags(respVO.getId()));
        //
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(applicationDO);
        respVO.setCreateUser(userHelper.getUserNickname(applicationDO.getCreator()));
        respVO.setUpdateUser(userHelper.getUserNickname(applicationDO.getUpdater()));
        //
        enrichDevelopStatus(respVO);
        return respVO;
    }

    private void enrichDevelopStatus(ApplicationRespVO vo) {
        if (AppPublishEnum.isNotPublished(vo.getAppStatus())) {
            vo.setDevelopStatus("开发中");
        }
        AppVersionDO versionDO = versionRepository.findRuntimeByApplicationId(vo.getId());
        if (versionDO == null) {
            return;
        }
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(versionDO);
        vo.setVersionNumber(versionDO.getVersionNumber());
        vo.setPublisher(userHelper.getUserNickname(versionDO.getCreator()));
        vo.setPublishTime(versionDO.getCreateTime());
        LocalDateTime appUpdateTime = vo.getUpdateTime();
        LocalDateTime versionPublishTime = versionDO.getCreateTime();
        if (appUpdateTime.isBefore(versionPublishTime)) {
            vo.setDevelopStatus("已发布");
        } else {
            vo.setDevelopStatus("有更新");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationCreateRespVO createApplication(ApplicationCreateReqVO createReqVO) {
        validApplicationCodeDuplicate(createReqVO.getAppCode(), null);
        AppApplicationDO applicationDO = BeanUtils.toBean(createReqVO, AppApplicationDO.class);
        applicationDO.setId(null);
        applicationDO.setAppUid(findAndCreateAppUid());
        applicationDO.setAppStatus(AppStatusEnum.OFFLINE.getValue());

        if (StringUtils.isNoneBlank(createReqVO.getAppCode())) {
            applicationDO.setAppCode(createReqVO.getAppCode());
        } else {
            applicationDO.setAppCode(AppUtils.createAppCode());
        }
        // 新增发布模式，新增空间id
        applicationDO.setPublishModel(createReqVO.getPublishModel() == null ? CommonPublishModelEnum.InnerModel.getValue() : createReqVO.getPublishModel());
        applicationRepository.save(applicationDO);
        saveApplicationTags(applicationDO.getId(), createReqVO.getTagIds());
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        authRoleService.createDefaultRole(applicationDO.getId(), userId);
        createDatasource(applicationDO.getId(), applicationDO.getAppUid(), createReqVO.getDatasourceSaveReq());
        buildAppMenuService.createDefaultBpmMenu(applicationDO.getId());
        return BeanUtils.toBean(applicationDO, ApplicationCreateRespVO.class);
    }

    private void createDatasource(Long appId, String appUid, DatasourceSaveReqDTO datasourceSaveReq) {
        if (datasourceSaveReq == null || datasourceSaveReq.getName() == null) {
            DatasourceCreateDefaultReqDTO defaultReq = new DatasourceCreateDefaultReqDTO();
            defaultReq.setApplicationId(appId);
            defaultReq.setAppUid(appUid);
            metadataDatasourceApi.createDefaultDatasource(defaultReq);
        } else {
            datasourceSaveReq.setApplicationId(appId);
            datasourceSaveReq.setAppUid(appUid);
            metadataDatasourceApi.createDatasource(datasourceSaveReq);
        }
    }

    /**
     * 更新应用关联的标签，先删除没有的标签，再添加新的标签
     *
     * @param applicationId
     * @param tagIds
     */
    private void saveApplicationTags(Long applicationId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            applicationTagRepository.deleteByApplicationId(applicationId);
        } else {
            applicationTagRepository.deleteByApplicationIdAndTagsNotIn(applicationId, tagIds);
            applicationTagRepository.saveAll(applicationId, tagIds);
        }
    }


    @Override
    public void updateApplication(ApplicationCreateReqVO createReqVO) {
        appCommonService.validateApplicationExist(createReqVO.getId());
        validApplicationCodeDuplicate(createReqVO.getAppCode(), createReqVO.getId());
        AppApplicationDO updateObj = BeanUtils.toBean(createReqVO, AppApplicationDO.class);
        updateObj.setPublishModel(createReqVO.getPublishModel() == null ? CommonPublishModelEnum.InnerModel.getValue() : createReqVO.getPublishModel());
        saveApplicationTags(createReqVO.getId(), createReqVO.getTagIds());
        applicationRepository.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationName(Long id, String name) {
        appCommonService.validateApplicationExist(id);
        AppApplicationDO applicationDO = new AppApplicationDO();
        applicationDO.setId(id);
        applicationDO.setAppName(name);
        applicationRepository.updateById(applicationDO);
    }

    @Override
    public void deleteApplication(Long id, String name) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(id);
        if (!StringUtils.equals(name, applicationDO.getAppName())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NAME_ERROR);
        }
        etlDataManager.offlineAllByApplication(id);
        flowDataManager.offlineRuntimeData(id);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            bpmDataManager.removeApplication(id);
            metadataDataManagerApi.deleteAllApplicationData(id);
            etlDataManager.deleteAllApplicationData(id);
            flowDataManager.deleteAllApplicationData(id);
            appDataManager.deleteAllApplicationData(id);
            versionRepository.deleteByApplicationId(id);
            applicationTagRepository.deleteByApplicationId(id);
            applicationRepository.removeById(id);
        });
    }

    @Override
    public Long generateId() {
        Long uid = uidGenerator.getUID();
        log.info("Generator id: {}", uid);
        return uid;
    }


    /**
     * 检查 AppApplicationDO 表 code 码是否重复，重复跑出异常
     */
    private void validApplicationCodeDuplicate(String code, Long id) {
        if (id == null) {
            if (applicationRepository.findOneByAppCode(code) != 0) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_CODE_DUPLICATE);
            }
        } else {
            if (applicationRepository.findByAppCodeAndIdNot(code, id) != 0) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_CODE_DUPLICATE);
            }
        }
    }

    /**
     * 随机生成一个appUid，然后去数据库里面查询是否唯一，如果不唯一，则重新生成一个，尝试25次
     *
     * @return 唯一的appUid
     */
    private String findAndCreateAppUid() {
        for (int i = 0; i < 25; i++) {
            String appUid = AppUtils.createAppUid();
            if (applicationRepository.findOneByUid(appUid) == 0) {
                return appUid;
            }
        }
        throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_UID_GENERATE_FAILED);
    }

    @Override
    public List<AppApplicationDO> getSimpleAppList(Integer status) {
        return applicationRepository.getSimpleAppList(status);
    }

    @Override
    public List<AppApplicationDO> getMySimpleAppListByName(String appName) {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        return applicationRepository.findMyAppApplicationByAppName(appName, currentUserId);
    }

    @Override
    public ApplicationNavigationConfigVO getApplicationNavigationConfig(Long id) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(id);
        ApplicationNavigationConfigVO respVO = BeanUtils.toBean(applicationDO, ApplicationNavigationConfigVO.class);
        return respVO;
    }

    @Override
    public void updateApplicationNavigationConfig(ApplicationNavigationConfigVO updateReqVO) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(updateReqVO.getId());
        applicationDO.setWebDefaultMenu(updateReqVO.getWebDefaultMenu());
        applicationDO.setWebNavLayout(updateReqVO.getWebNavLayout());
        applicationDO.setMobileDefaultMenu(updateReqVO.getMobileDefaultMenu());
        applicationDO.setMobileNavLayout(updateReqVO.getMobileNavLayout());
        applicationRepository.updateById(applicationDO);
    }

}
