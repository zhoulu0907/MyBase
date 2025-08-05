package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationPageRespVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.TagRespVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.*;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.app.ApplicationStatusEnum;
import com.cmsr.onebase.module.app.util.VersionUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
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
    private DataRepository dataRepository;

    @Resource
    private AppCommonService appCommonService;

    @Override
    public PageResult<ApplicationPageRespVO> getApplicationPage(ApplicationPageReqVO pageReqVO) {
        ConfigStore configs = new DefaultConfigStore();
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            configs.and(Compare.LIKE, "app_name", pageReqVO.getName());
        }
        if (pageReqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, "status", pageReqVO.getStatus());
        }
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "create")) {
            configs.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        }
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "update")) {
            configs.order(BaseDO.UPDATE_TIME, Order.TYPE.DESC);
        }
        PageResult<ApplicationDO> pageResult = dataRepository.findPageWithConditions(ApplicationDO.class, configs,
                pageReqVO.getPageNo(), pageReqVO.getPageSize());
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());
        List<ApplicationPageRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    ApplicationPageRespVO bean = BeanUtils.toBean(v, ApplicationPageRespVO.class);
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
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("application_id", appId);
        List<Long> tagIds = dataRepository.findAll(ApplicationTagDO.class, configStore).stream()
                .map(ApplicationTagDO::getTagId)
                .toList();
        configStore = new DefaultConfigStore();
        configStore.in("id", tagIds);
        return dataRepository.findAll(TagDO.class, configStore).stream()
                .map(v -> BeanUtils.toBean(v, TagRespVO.class))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createApplication(ApplicationCreateReqVO createReqVO) {
        validApplicationCodeDuplicate(createReqVO.getAppCode(), null);
        ApplicationDO applicationDO = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        applicationDO.setVersionNumber(VersionUtils.INIT_VERSION);
        applicationDO.setAppStatus(ApplicationStatusEnum.EDITING.getValue());
        applicationDO = dataRepository.insert(applicationDO);
        mergeApplicationTags(applicationDO.getId(), createReqVO.getTagIds());
        return applicationDO.getId();
    }

    private void mergeApplicationTags(Long id, List<Long> tagIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("application_id", id);
        dataRepository.deleteByConfig(ApplicationTagDO.class, configStore);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds) {
            ApplicationTagDO applicationTagDO = new ApplicationTagDO();
            applicationTagDO.setApplicationId(id);
            applicationTagDO.setTagId(tagId);
            dataRepository.insert(applicationTagDO);
        }
    }

    @Override
    public void updateApplication(ApplicationCreateReqVO createReqVO) {
        appCommonService.validateApplicationExist(createReqVO.getId());
        validApplicationCodeDuplicate(createReqVO.getAppCode(), createReqVO.getId());
        ApplicationDO updateObj = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        mergeApplicationTags(createReqVO.getId(), createReqVO.getTagIds());
        dataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationName(Long id, String name) {
        appCommonService.validateApplicationExist(id);
        ApplicationDO applicationDO = new ApplicationDO();
        applicationDO.setId(id);
        applicationDO.setAppName(name);
        dataRepository.update(applicationDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplication(Long id, String name) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(id);
        if (!StringUtils.equals(name, applicationDO.getAppName())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NAME_ERROR);
        }
        //TODO 删除应用下的全部资源
        dataRepository.deleteById(ApplicationDO.class, id);
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("application_id", id);
        dataRepository.deleteByConfig(MenuDO.class, configStore);
        dataRepository.deleteByConfig(ResourceDO.class, configStore);
        dataRepository.deleteByConfig(VersionDO.class, configStore);
        dataRepository.deleteByConfig(VersionMenuDO.class, configStore);
        dataRepository.deleteByConfig(VersionResourceDO.class, configStore);
    }


    /**
     * 检查 ApplicationDO 表 code 码是否重复，重复跑出异常
     */
    private void validApplicationCodeDuplicate(String code, Long id) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("app_code", code);
        if (id == null) {
            if (dataRepository.findOne(ApplicationDO.class, configs) != null) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_CODE_DUPLICATE);
            }
        } else {
            configs.ne("id", id);
            if (dataRepository.findOne(ApplicationDO.class, configs) != null) {
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_CODE_DUPLICATE);
            }
        }
    }

}
