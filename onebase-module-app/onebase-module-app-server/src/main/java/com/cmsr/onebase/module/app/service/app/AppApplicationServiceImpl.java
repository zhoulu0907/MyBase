package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationRespVO;
import com.cmsr.onebase.module.app.controller.admin.tag.vo.TagRespVO;
import com.cmsr.onebase.module.app.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionResourceRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.app.ApplicationStatusEnum;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.service.auth.AppAuthRoleService;
import com.cmsr.onebase.module.app.util.AppUtils;
import com.cmsr.onebase.module.app.util.VersionUtils;
import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 17:11
 */
@Setter
@Service
@Validated
public class AppApplicationServiceImpl implements AppApplicationService {

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

    @Override
    public PageResult<ApplicationRespVO> getApplicationPage(ApplicationPageReqVO pageReqVO) {
        PageResult<ApplicationDO> pageResult = applicationRepository.selectPage(pageReqVO);
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());
        List<ApplicationRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    ApplicationRespVO bean = BeanUtils.toBean(v, ApplicationRespVO.class);
                    bean.setAppStatusText(ApplicationStatusEnum.getText(v.getAppStatus()));
                    bean.setTags(queryAppTags(v.getId()));
                    bean.setCreateUser(userHelper.getUserName(v.getCreator()));
                    bean.setUpdateUser(userHelper.getUserName(v.getUpdater()));
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    private List<TagRespVO> queryAppTags(Long appId) {
        List<Long> tagIds = applicationTagRepository.findTagIdsByApplicationId(appId);
        return tagRepository.findAllByIds(tagIds).stream()
                .map(v -> BeanUtils.toBean(v, TagRespVO.class))
                .toList();
    }

    @Override
    public ApplicationRespVO getApplication(Long id) {
        ApplicationDO applicationDO = applicationRepository.findById(id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(applicationDO);
        ApplicationRespVO respVO = BeanUtils.toBean(applicationDO, ApplicationRespVO.class
                , vo -> {
                    vo.setAppStatusText(ApplicationStatusEnum.getText(vo.getAppStatus()));
                    vo.setTags(queryAppTags(vo.getId()));
                    vo.setCreateUser(userHelper.getUserName(applicationDO.getCreator()));
                    vo.setUpdateUser(userHelper.getUserName(applicationDO.getUpdater()));
                });
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationCreateRespVO createApplication(ApplicationCreateReqVO createReqVO) {
        validApplicationCodeDuplicate(createReqVO.getAppCode(), null);
        ApplicationDO applicationDO = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        applicationDO.setId(null);
        applicationDO.setAppUid(findAndCreateAppUid());
        applicationDO.setVersionNumber(VersionUtils.INIT_VERSION);
        applicationDO.setAppStatus(ApplicationStatusEnum.EDITING.getValue());
        if (StringUtils.isNoneBlank(createReqVO.getAppCode())) {
            applicationDO.setAppCode(createReqVO.getAppCode());
        } else {
            applicationDO.setAppCode(AppUtils.createAppCode());
        }
        applicationDO = applicationRepository.insert(applicationDO);
        saveApplicationTags(applicationDO.getId(), createReqVO.getTagIds());
        authRoleService.createDefaultRole(applicationDO.getId());
        metadataDatasourceApi.createDefaultDatasource(applicationDO.getId());
        return BeanUtils.toBean(applicationDO, ApplicationCreateRespVO.class);
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
        ApplicationDO updateObj = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        saveApplicationTags(createReqVO.getId(), createReqVO.getTagIds());
        applicationRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationName(Long id, String name) {
        appCommonService.validateApplicationExist(id);
        ApplicationDO applicationDO = new ApplicationDO();
        applicationDO.setId(id);
        applicationDO.setAppName(name);
        applicationRepository.update(applicationDO);
    }

    @Override
    public void updateApplicationVersion(Long id, String versionNumber, String versionUrl) {
        ApplicationDO applicationDO = applicationRepository.findById(id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        applicationDO.setVersionNumber(versionNumber);
        applicationDO.setVersionUrl(versionUrl);
        applicationRepository.update(applicationDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplication(Long id, String name) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(id);
        if (!StringUtils.equals(name, applicationDO.getAppName())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NAME_ERROR);
        }
        //TODO 删除应用下的全部资源
        applicationRepository.deleteById(id);
        menuRepository.deleteByApplicationId(id);
        versionRepository.deleteByApplicationId(id);
        versionResourceRepository.deleteByApplicationId(id);
    }


    /**
     * 检查 ApplicationDO 表 code 码是否重复，重复跑出异常
     */
    private void validApplicationCodeDuplicate(String code, Long id) {
        if (id == null) {
            if (applicationRepository.findOneByAppCode(code) != null) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_KEY_DUPLICATE);
            }
        } else {
            if (applicationRepository.findByAppCodeAndIdNot(code, id) != null) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_KEY_DUPLICATE);
            }
        }
    }

    /**
     * 随机生成一个appUid，然后去数据库里面查询是否唯一，如果不唯一，则重新生成一个，尝试10次
     *
     * @return 唯一的appUid
     */
    private String findAndCreateAppUid() {
        for (int i = 0; i < 10; i++) {
            String appUid = AppUtils.createAppUid();
            if (applicationRepository.findOneByUid(appUid) == null) {
                return appUid;
            }
        }
        throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_UID_GENERATE_FAILED);
    }

}
