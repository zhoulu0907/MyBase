package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationPageRespVO;
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
public class ApplicationServiceImpl implements ApplicationService {

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
        List<ApplicationPageRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    ApplicationPageRespVO bean = BeanUtils.toBean(v, ApplicationPageRespVO.class);
                    bean.setStatusText(ApplicationStatusEnum.getText(v.getStatus()));
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    @Override
    public Long createApplication(ApplicationCreateReqVO createReqVO) {
        createReqVO.setId(null);
        ApplicationDO applicationDO = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        applicationDO.setVersionNumber(VersionUtils.INIT_VERSION);
        applicationDO = dataRepository.insert(applicationDO);
        return applicationDO.getId();
    }

    @Override
    public void updateApplication(ApplicationCreateReqVO createReqVO) {
        appCommonService.validateApplicationExist(createReqVO.getId());
        ApplicationDO updateObj = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        dataRepository.update(updateObj);
    }

    @Override
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
        dataRepository.deleteByConfig(ApplicationMenuDO.class, configStore);
        dataRepository.deleteByConfig(ApplicationResourceDO.class, configStore);
        dataRepository.deleteByConfig(VersionDO.class, configStore);
        dataRepository.deleteByConfig(VersionMenuDO.class, configStore);
        dataRepository.deleteByConfig(VersionResourceDO.class, configStore);
    }


}
