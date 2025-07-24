package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationPageRespVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationMenuDO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 17:11
 */
@Service
@Validated
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private DataRepository dataRepository;

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
            configs.order("create_time", "DESC");
        }
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "update")) {
            configs.order("update_time", "DESC");
        }
        PageResult<ApplicationDO> pageResult = dataRepository.findPageWithConditions(ApplicationDO.class, configs,
                pageReqVO.getPageNo(), pageReqVO.getPageSize());
        List<ApplicationPageRespVO> respVOS = pageResult.getList().stream()
                .map(v -> BeanUtils.toBean(v, ApplicationPageRespVO.class))
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    @Override
    public Long createApplication(ApplicationCreateReqVO createReqVO) {
        createReqVO.setId(null);
        ApplicationDO applicationDO = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        applicationDO = dataRepository.insert(applicationDO);
        return applicationDO.getId();
    }

    @Override
    public void updateApplication(ApplicationCreateReqVO createReqVO) {
        validateApplicationExist(createReqVO.getId());
        ApplicationDO updateObj = BeanUtils.toBean(createReqVO, ApplicationDO.class);
        dataRepository.update(updateObj);
    }

    @Override
    public void updateApplicationName(Long id, String name) {
        validateApplicationExist(id);
        ApplicationDO applicationDO = new ApplicationDO();
        applicationDO.setId(id);
        applicationDO.setAppName(name);
        dataRepository.update(applicationDO);
    }

    @Override
    public void deleteApplication(Long id, String name) {
        ApplicationDO applicationDO = validateApplicationExist(id);
        if (!StringUtils.equals(name, applicationDO.getAppName())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NAME_ERROR);
        }
        //TODO 删除应用下的全部资源
        dataRepository.deleteById(ApplicationDO.class, id);
        dataRepository.delete(ApplicationMenuDO.class, new DefaultConfigStore().eq("application_id", id));
    }

    private ApplicationDO validateApplicationExist(Long id) {
        ApplicationDO applicationDO = dataRepository.findById(ApplicationDO.class, id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        return applicationDO;
    }
}
