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
import com.cmsr.onebase.module.app.dal.database.app.AppResourceRepository;
import com.cmsr.onebase.module.app.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionMenuRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionResourceRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.app.ApplicationStatusEnum;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.util.VersionUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
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
    private AppResourceRepository resourceRepository;

    @Resource
    private AppVersionRepository versionRepository;

    @Resource
    private AppVersionMenuRepository versionMenuRepository;

    @Resource
    private AppVersionResourceRepository versionResourceRepository;

    @Resource
    private AppCommonService appCommonService;

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
    @Transactional(rollbackFor = Exception.class)
    public ApplicationCreateRespVO createApplication(ApplicationCreateReqVO createReqVO) {
        validApplicationCodeDuplicate(createReqVO.getAppCode(), null);
        ApplicationDO applicationDO = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        applicationDO.setVersionNumber(VersionUtils.INIT_VERSION);
        applicationDO.setAppStatus(ApplicationStatusEnum.EDITING.getValue());
        applicationDO = applicationRepository.insert(applicationDO);
        applicationTagRepository.saveApplicationTags(applicationDO.getId(), createReqVO.getTagIds());
        return BeanUtils.toBean(applicationDO, ApplicationCreateRespVO.class);
    }

    @Override
    public ApplicationRespVO getApplication(Long id) {
        ApplicationDO applicationDO = applicationRepository.findById(id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        return BeanUtils.toBean(applicationDO, ApplicationRespVO.class);
    }


    @Override
    public void updateApplication(ApplicationCreateReqVO createReqVO) {
        appCommonService.validateApplicationExist(createReqVO.getId());
        validApplicationCodeDuplicate(createReqVO.getAppCode(), createReqVO.getId());
        ApplicationDO updateObj = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        applicationTagRepository.saveApplicationTags(createReqVO.getId(), createReqVO.getTagIds());
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplication(Long id, String name) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(id);
        if (!StringUtils.equals(name, applicationDO.getAppName())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NAME_ERROR);
        }
        //TODO 删除应用下的全部资源
        applicationRepository.deleteById(id);
        menuRepository.deleteByApplicationId(id);
        resourceRepository.deleteByApplicationId(id);
        versionRepository.deleteByApplicationId(id);
        versionMenuRepository.deleteByApplicationId(id);
        versionResourceRepository.deleteByApplicationId(id);
    }


    /**
     * 检查 ApplicationDO 表 code 码是否重复，重复跑出异常
     */
    private void validApplicationCodeDuplicate(String code, Long id) {
        if (id == null) {
            if (applicationRepository.findOneByAppCode(code) != null) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_CODE_DUPLICATE);
            }
        } else {
            if (applicationRepository.findByAppCodeAndIdNot(code, id) != null) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_CODE_DUPLICATE);
            }
        }
    }

}
