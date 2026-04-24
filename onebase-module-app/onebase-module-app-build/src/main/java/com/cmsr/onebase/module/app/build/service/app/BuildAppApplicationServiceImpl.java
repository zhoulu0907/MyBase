package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.service.auth.BuildAppAuthRoleService;
import com.cmsr.onebase.module.app.build.service.menu.BuildAppMenuService;
import com.cmsr.onebase.module.app.build.service.version.AppDataManager;
import com.cmsr.onebase.module.app.build.util.AppUtils;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationSimpleRespVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.app.AppNavigationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.AppPublishEnum;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
import com.cmsr.onebase.module.app.core.enums.app.DevelopStatusEnum;
import com.cmsr.onebase.module.app.core.vo.app.AppUserPhotoDTO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationNavigationConfigVO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.core.vo.tag.TagRespVO;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.etl.api.EtlDataManager;
import com.cmsr.onebase.module.flow.api.FlowDataManager;
import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import com.cmsr.onebase.module.metadata.api.version.MetadataDataManagerApi;
import com.cmsr.onebase.module.screen.api.DashboardProjectApi;
import com.cmsr.onebase.module.system.api.dict.DictDataApi;
import com.cmsr.onebase.module.system.api.project.ProjectAppRelationApi;
import jakarta.annotation.Resource;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
public class BuildAppApplicationServiceImpl implements AppApplicationService {

    public static final String       APP = "app";
    @Autowired
    private             UidGenerator uidGenerator;

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
    private BuildAppAuthRoleService authRoleService;

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

    @Autowired
    private AppNavigationRepository appNavigationRepository;

    @Resource
    private DashboardProjectApi dashboardProjectApi;

    @Resource
    private DictDataApi dictDataApi;

    @Resource
    private ProjectAppRelationApi projectAppRelationApi;

    @Override
    public PageResult<ApplicationRespVO> getApplicationPage(ApplicationPageReqVO pageReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageResult<AppApplicationDO> pageResult;
        
        // 如果传入了projectId，先查询当前项目下的所有应用ID
        if (StringUtils.isNotBlank(pageReqVO.getProjectId())) {
            CommonResult<List<Long>> projectResult = projectAppRelationApi.listApplicationIdsByProjectId(pageReqVO.getProjectId());
            if (projectResult == null || !projectResult.isSuccess() || CollectionUtils.isEmpty(projectResult.getData())) {
                return PageResult.empty();
            }
            List<Long> appIds = projectResult.getData();
            // 根据应用ID和其他过滤条件进行分页查询
            pageResult = applicationRepository.getApplicationPageByAppIds(appIds, pageReqVO);
        } else {
            // 没有projectId，使用原有的查询逻辑
            pageResult = applicationRepository.selectPage(pageReqVO, userId);
        }
        
        if (CollectionUtils.isEmpty(pageResult.getList())) {
            return PageResult.empty();
        }
        List<ApplicationRespVO> respVOS = pageResult.getList().stream().map(v -> {
            ApplicationRespVO bean = BeanUtils.toBean(v, ApplicationRespVO.class);
            bean.setAppStatusText(DevelopStatusEnum.getText(v.getAppStatus()));
            return bean;
        }).toList();
        enrichIcons(respVOS);
        enrichTags(respVOS);
        enrichUser(respVOS);
        enrichUserPhoto(respVOS);
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    private void enrichIcons(List<ApplicationRespVO> respVOS) {
        List<Long> appIds = respVOS.stream().map(ApplicationRespVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appIds)) {
            return;
        }
        List<AppNavigationDO> navigationDOS = ApplicationManager
                .withoutApplicationCondition(() -> appNavigationRepository.findByApplicationIds(appIds));
        for (ApplicationRespVO respVO : respVOS) {
            AppNavigationDO navigationDO = navigationDOS.stream()
                    .filter(navigation -> navigation.getApplicationId().equals(respVO.getId()))
                    .findFirst()
                    .orElse(null);
            if (navigationDO != null) {
                respVO.setIconName(navigationDO.getIconName());
                respVO.setIconColor(navigationDO.getIconColor());
                respVO.setThemeColor(navigationDO.getThemeColor());
            }
        }
    }

    private void enrichTags(List<ApplicationRespVO> respVOS) {
        List<Long> appIds = respVOS.stream().map(ApplicationRespVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appIds)) {
            return;
        }
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
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(userIds);
        for (ApplicationRespVO respVO : respVOS) {
            respVO.setCreateUser(userHelper.getUserNickname(respVO.getCreator()));
            respVO.setUpdateUser(userHelper.getUserNickname(respVO.getUpdater()));
        }
    }

    private void enrichUserPhoto(List<ApplicationRespVO> respVOS) {
        List<Long> appIds = respVOS.stream().map(ApplicationRespVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appIds)) {
            return;
        }
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
        ApplicationRespVO respVO = new ApplicationRespVO();
        AppNavigationDO appNavigationDO = appNavigationRepository.findByApplicationId(id);
        if (appNavigationDO != null) {
            BeanUtils.copyProperties(appNavigationDO, respVO);
        }
        BeanUtils.copyProperties(applicationDO, respVO);
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
            vo.setDevelopStatus(DevelopStatusEnum.DEVELOPING.getText());
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
            vo.setDevelopStatus(DevelopStatusEnum.PUBLISHED.getText());
        } else {
            vo.setDevelopStatus(DevelopStatusEnum.UPDATED.getText());
        }
    }

    @Override
    public ApplicationCreateRespVO createApplication(ApplicationCreateReqVO createReqVO) {
        validApplicationCodeDuplicate(createReqVO.getAppCode(), null);
        return transactionTemplate.execute(transactionStatus -> {

            AppApplicationDO applicationDO = BeanUtils.toBean(createReqVO, AppApplicationDO.class);
            applicationDO.setId(null);
            applicationDO.setAppUid(appCommonService.findAndCreateAppUid());
            applicationDO.setAppStatus(AppStatusEnum.OFFLINE.getValue());

            if (StringUtils.isNoneBlank(createReqVO.getAppCode())) {
                applicationDO.setAppCode(createReqVO.getAppCode());
            } else {
                applicationDO.setAppCode(AppUtils.createAppCode());
            }
            // 新增发布模式，新增空间id
            applicationDO.setPublishModel(
                    createReqVO.getPublishModel() == null ? CommonPublishModelEnum.InnerModel.getValue()
                            : createReqVO.getPublishModel());
            applicationRepository.save(applicationDO);

            if (StringUtils.isNotBlank(createReqVO.getProjectId())) {
                //插入项目应用关联表
                projectAppRelationApi.createProjectAppRelation(createReqVO.getProjectId(), applicationDO.getId());
            }
            // 保存导航配置
            AppNavigationDO appNavigationDO = new AppNavigationDO();
            BeanUtils.copyProperties(createReqVO, appNavigationDO);
            appNavigationDO.setId(null);
            appNavigationDO.setApplicationId(applicationDO.getId());
            appNavigationRepository.save(appNavigationDO);
            // 保存标签
            saveApplicationTags(applicationDO.getId(), createReqVO.getTagIds());
            // 创建默认角色
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            authRoleService.createDefaultRole(applicationDO.getId(), userId);
            // 创建数据源
            createDatasource(applicationDO.getId(), applicationDO.getAppUid(), createReqVO.getDatasourceSaveReq());
            // 创建默认流程菜单
            buildAppMenuService.createDefaultBpmMenu(applicationDO.getId());
            return BeanUtils.toBean(applicationDO, ApplicationCreateRespVO.class);
        });
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

    private void saveApplicationNavigation(Long applicationId, ApplicationCreateReqVO createReqVO) {
        AppNavigationDO appNavigationDO = appNavigationRepository.findByApplicationId(applicationId);
        if (appNavigationDO == null) {
            appNavigationDO = new AppNavigationDO();
        }
        appNavigationDO.setApplicationId(applicationId);
        appNavigationDO.setVersionTag(VersionTagEnum.BUILD.getValue());
        appNavigationDO.setIconName(createReqVO.getIconName());
        appNavigationDO.setIconColor(createReqVO.getIconColor());
        appNavigationDO.setThemeColor(createReqVO.getThemeColor());
        appNavigationRepository.saveOrUpdate(appNavigationDO);
    }

    @Override
    public void updateApplication(ApplicationCreateReqVO createReqVO) {
        appCommonService.validateApplicationExist(createReqVO.getId());
        validApplicationCodeDuplicate(createReqVO.getAppCode(), createReqVO.getId());
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            AppApplicationDO updateObj = BeanUtils.toBean(createReqVO, AppApplicationDO.class);
            updateObj.setPublishModel(
                    createReqVO.getPublishModel() == null ? CommonPublishModelEnum.InnerModel.getValue()
                            : createReqVO.getPublishModel());
            applicationRepository.updateById(updateObj);
            saveApplicationTags(createReqVO.getId(), createReqVO.getTagIds());
            saveApplicationNavigation(createReqVO.getId(), createReqVO);
        });
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
        if (AppStatusEnum.isOnline(applicationDO.getAppStatus())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_ONLINE_ERROR);
        }
        etlDataManager.offlineAllByApplication(id);
        flowDataManager.deleteRuntimeData(id);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            bpmDataManager.removeApplication(id);
            metadataDataManagerApi.deleteAllApplicationData(id);
            etlDataManager.deleteAllApplicationData(id);
            flowDataManager.deleteAllApplicationData(id);
            appDataManager.deleteAllApplicationData(id);
            versionRepository.deleteByApplicationId(id);
            applicationTagRepository.deleteByApplicationId(id);
            applicationRepository.removeById(id);
            // 删除大屏
            dashboardProjectApi.removeDashboardByAppId(id);
            // 删除应用级别字典Dict
            dictDataApi.deleteDictDataByDictOwner(APP, id);

            projectAppRelationApi.removeProjectAppRelation(id);
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

    @Override
    public List<AppApplicationDO> getSimpleAppList(Integer status, String projectCode) {
        return applicationRepository.getSimpleAppList(status, projectCode);
    }

    @Override
    public List<AppApplicationDO> getSimpleAllAppList(Long tenantId) {
        return applicationRepository.getSimpleAllAppList(tenantId);
    }

    @Override
    public List<ApplicationSimpleRespVO> getMySimpleAppListByName(String appName) {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        List<AppApplicationDO> applicationList = applicationRepository.findMyAppApplicationByAppName(appName, currentUserId);
        List<ApplicationRespVO> applicationRespVOS = BeanUtils.toBean(applicationList, ApplicationRespVO.class);
        enrichIcons(applicationRespVOS);
        return BeanUtils.toBean(applicationRespVOS, ApplicationSimpleRespVO.class);
    }

    @Override
    public ApplicationNavigationConfigVO getApplicationNavigationConfig(Long id) {
        appCommonService.validateApplicationExist(id);
        AppNavigationDO appNavigationDO = appNavigationRepository.findByApplicationId(id);
        if (appNavigationDO == null) {
            appNavigationDO = new AppNavigationDO();
        }
        return BeanUtils.toBean(appNavigationDO, ApplicationNavigationConfigVO.class);
    }

    @Override
    public void updateApplicationNavigationConfig(ApplicationNavigationConfigVO updateReqVO) {
        appCommonService.validateApplicationExist(updateReqVO.getId());
        AppNavigationDO appNavigationDO = appNavigationRepository.findByApplicationId(updateReqVO.getId());
        if (appNavigationDO == null) {
            appNavigationDO = new AppNavigationDO();
        }
        // 设置数据
        appNavigationDO.setWebDefaultMenu(updateReqVO.getWebDefaultMenu());
        appNavigationDO.setWebNavLayout(updateReqVO.getWebNavLayout());
        appNavigationDO.setMobileDefaultMenu(updateReqVO.getMobileDefaultMenu());
        appNavigationDO.setMobileNavLayout(updateReqVO.getMobileNavLayout());

        // 登录相关配置
        appNavigationDO.setAppLoginMainPic(updateReqVO.getAppLoginMainPic());
        appNavigationDO.setAppThirdUserEnable(updateReqVO.getAppThirdUserEnable());
        appNavigationDO.setAppUserForgetPwdShow(updateReqVO.getAppUserForgetPwdShow());
        appNavigationDO.setAppUserRegisterShow(updateReqVO.getAppUserRegisterShow());
        appNavigationRepository.saveOrUpdate(appNavigationDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplicationsByProjectId(String projectId) {
        LocalDateTime startTime = LocalDateTime.now();
        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("开始删除项目ID: {} 下的应用，开始时间: {}", projectId, startTimeStr);
        
        // 根据项目ID获取所有应用ID
        CommonResult<List<Long>> projectResult = projectAppRelationApi.listApplicationIdsByProjectId(projectId);
        if (projectResult == null || !projectResult.isSuccess() || CollectionUtils.isEmpty(projectResult.getData())) {
            log.warn("项目ID: {} 下没有找到任何应用", projectId);
            return;
        }
        
        List<Long> appIds = projectResult.getData();
        int totalApps = appIds.size();
        log.info("项目ID: {} 下共有 {} 个应用需要删除", projectId, totalApps);
        
        // 同步串行删除应用
        int deletedCount = 0;
        for (Long appId : appIds) {
            try {
                AppApplicationDO applicationDO = applicationRepository.getById(appId);
                if (applicationDO == null) {
                    log.warn("应用ID: {} 不存在，跳过", appId);
                    continue;
                }

                // 检查应用是否在线
                if (AppStatusEnum.isOnline(applicationDO.getAppStatus())) {
                    log.warn("应用ID: {} 处于在线状态，无法删除，先将其下线", appId);
                    throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_ONLINE_ERROR);
                }

                // 执行删除逻辑（复用现有的deleteApplication方法的核心逻辑）
                etlDataManager.offlineAllByApplication(appId);
                flowDataManager.deleteRuntimeData(appId);

                bpmDataManager.removeApplication(appId);
                metadataDataManagerApi.deleteAllApplicationData(appId);
                etlDataManager.deleteAllApplicationData(appId);
                flowDataManager.deleteAllApplicationData(appId);
                appDataManager.deleteAllApplicationData(appId);
                versionRepository.deleteByApplicationId(appId);
                applicationTagRepository.deleteByApplicationId(appId);
                applicationRepository.removeById(appId);
                dashboardProjectApi.removeDashboardByAppId(appId);
                dictDataApi.deleteDictDataByDictOwner(APP, appId);
                projectAppRelationApi.removeProjectAppRelation(appId);

                log.info("成功删除应用ID: {}, 名称: {}", appId, applicationDO.getAppName());
                deletedCount++;
            } catch (Exception e) {
                log.error("删除应用ID: {} 时发生错误", appId, e);
                throw new RuntimeException("删除应用ID: " + appId + " 失败", e);
            }
        }

        LocalDateTime endTime = LocalDateTime.now();
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long durationSeconds = ChronoUnit.SECONDS.between(startTime, endTime);
        log.info("项目ID: {} 下的应用删除完成，开始时间: {}, 结束时间: {}, 总应用数: {}, 成功删除: {}, 耗时: {} 秒",
                projectId, startTimeStr, endTimeStr, totalApps, deletedCount, durationSeconds);
    }

}
