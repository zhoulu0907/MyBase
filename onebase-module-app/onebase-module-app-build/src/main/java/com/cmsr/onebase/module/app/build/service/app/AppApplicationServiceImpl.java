package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.service.auth.AppAuthRoleService;
import com.cmsr.onebase.module.app.build.service.menu.AppMenuService;
import com.cmsr.onebase.module.app.build.service.version.AppVersionService;
import com.cmsr.onebase.module.app.build.util.AppUtils;
import com.cmsr.onebase.module.app.build.util.VersionUtils;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.build.vo.tag.TagRespVO;
import com.cmsr.onebase.module.app.core.dal.database.*;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionResourceRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.ApplicationStatusEnum;
import com.cmsr.onebase.module.app.core.vo.app.AppUserPhotoDTO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/7/23 17:11
 */
@Setter
@Service
@Validated
@Slf4j
public class AppApplicationServiceImpl implements AppApplicationService {

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private AppApplicationRepository applicationRepository;

    @Resource
    private AppApplicationTagRepository applicationTagRepository;

    @Resource
    private AppTagRepository tagRepository;

    @Resource
    private AppMenuRepository menuRepository;

    @Resource
    private AppVersionRepository versionRepository;

    @Resource
    private AppVersionResourceRepository versionResourceRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private AppAuthRoleService authRoleService;

    @Resource
    private MetadataDatasourceApi metadataDatasourceApi;

    @Resource
    private AppAuthRoleUserRepository appAuthRoleUserRepository;

    @Resource
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private AppSqlQueryRepository appSqlQueryRepository;

    @Resource
    private AppVersionService appVersionService;

    @Resource
    private AppMenuService appMenuService;

    @Override
    public PageResult<ApplicationRespVO> getApplicationPage(ApplicationPageReqVO pageReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageResult<AppApplicationDO> pageResult = applicationRepository.selectPage(pageReqVO, userId);
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());

        // 1. 获取应用ID列表
        List<Long> appIds = pageResult.getList().stream()
                .map(AppApplicationDO::getId)
                .collect(Collectors.toList());
        Map<Long, List<AppUserPhotoDTO>> userListMap = appAuthRoleRepository.findUserPhotoList(appIds);

        List<ApplicationRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    ApplicationRespVO bean = BeanUtils.toBean(v, ApplicationRespVO.class);
                    bean.setAppStatusText(ApplicationStatusEnum.getText(v.getAppStatus()));
                    bean.setTags(queryAppTags(v.getId()));
                    bean.setCreateUser(userHelper.getUserNickname(v.getCreator()));
                    bean.setUpdateUser(userHelper.getUserNickname(v.getUpdater()));
                    bean.setUserPhotoList(userListMap.get(v.getId()));
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    private List<TagRespVO> queryAppTags(Long appId) {
        List<Long> tagIds = applicationTagRepository.findTagIdsByApplicationId(appId);
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
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(applicationDO);
        ApplicationRespVO respVO = BeanUtils.toBean(applicationDO, ApplicationRespVO.class
                , vo -> {
                    vo.setAppStatusText(ApplicationStatusEnum.getText(vo.getAppStatus()));
                    vo.setTags(queryAppTags(vo.getId()));
                    vo.setCreateUser(userHelper.getUserNickname(applicationDO.getCreator()));
                    vo.setUpdateUser(userHelper.getUserNickname(applicationDO.getUpdater()));
                });
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationCreateRespVO createApplication(ApplicationCreateReqVO createReqVO) {
        validApplicationCodeDuplicate(createReqVO.getAppCode(), null);
        AppApplicationDO applicationDO = BeanUtils.toBean(createReqVO, AppApplicationDO.class);
        applicationDO.setId(null);
        applicationDO.setAppUid(findAndCreateAppUid());
        applicationDO.setVersionNumber(VersionUtils.INIT_VERSION);
        applicationDO.setAppStatus(ApplicationStatusEnum.EDITING.getValue());
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
        appMenuService.createDefaultBpmMenu(applicationDO.getId());
        return BeanUtils.toBean(applicationDO, ApplicationCreateRespVO.class);
    }

    private void createDatasource(Long appId, String appUid, DatasourceSaveReqDTO datasourceSaveReq) {
        if (datasourceSaveReq == null || datasourceSaveReq.getName() == null) {
            DatasourceCreateDefaultReqDTO defaultReq = new DatasourceCreateDefaultReqDTO();
            defaultReq.setAppId(appId);
            defaultReq.setAppUid(appUid);
            metadataDatasourceApi.createDefaultDatasource(defaultReq);
        } else {
            datasourceSaveReq.setAppId(appId);
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
            applicationTagRepository.deleteByByApplicationIdAndTagsNotIn(applicationId, tagIds);
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
    public void updateApplicationVersion(Long id, String versionNumber, String versionUrl) {
        AppApplicationDO applicationDO = applicationRepository.getById(id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        applicationDO.setVersionNumber(versionNumber);
        applicationDO.setVersionUrl(versionUrl);
        applicationRepository.updateById(applicationDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplication(Long id, String name) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(id);
        if (!StringUtils.equals(name, applicationDO.getAppName())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NAME_ERROR);
        }
        //TODO 删除应用下的全部资源
        applicationRepository.removeById(id);
        menuRepository.deleteByApplicationId(id);
        versionRepository.deleteByApplicationId(id);
        versionResourceRepository.deleteByApplicationId(id);
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
}
