package com.cmsr.onebase.module.app.runtime.service.app;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.app.AppNavigationRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationNavigationConfigVO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.core.vo.tag.TagRespVO;
import com.cmsr.onebase.module.app.runtime.vo.app.AppLeastInfo;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 17:11
 */
@Setter
@Service
@Validated
@Slf4j
public class RuntimeAppApplicationServiceImpl implements AppApplicationService {

    @Autowired
    private AppApplicationRepository applicationRepository;

    @Autowired
    private AppApplicationTagRepository applicationTagRepository;

    @Autowired
    private AppTagRepository tagRepository;

    @Autowired
    private AppNavigationRepository appNavigationRepository;

    private List<TagRespVO> queryAppTags(Long appId) {
        List<Long> tagIds = applicationTagRepository.findTagIdsByApplicationId(appId);
        if (CollectionUtils.isEmpty(tagIds)) {
            return Collections.emptyList();
        }
        return tagRepository.listByIds(tagIds).stream()
                .map(v -> BeanUtils.toBean(v, TagRespVO.class))
                .toList();
    }

    /**
     * // TODO 有问题的，绕行了
     *
     * @param id
     * @return
     */
    @Override
    public ApplicationRespVO getApplication(Long id) {
        AppApplicationDO applicationDO = TenantManager.withoutTenantCondition(() -> applicationRepository.getById(id));
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
        return respVO;
    }

    @Override
    public ApplicationNavigationConfigVO getApplicationNavigationConfig(Long id) {
        AppNavigationDO appNavigationDO = appNavigationRepository.findByApplicationId(id);
        ApplicationNavigationConfigVO respVO = new ApplicationNavigationConfigVO();
        if (appNavigationDO != null) {
            BeanUtils.copyProperties(appNavigationDO, respVO);
        }
        return respVO;
    }

    @Override
    public AppLeastInfo getApplicationLeastInfo(Long id) {
        AppApplicationDO applicationDO = TenantManager.withoutTenantCondition(() -> applicationRepository.getById(id));
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        AppLeastInfo respVO = new AppLeastInfo();
        AppNavigationDO appNavigationDO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withoutApplicationIdAndVersionTag(() ->
                appNavigationRepository.findByApplicationId(id)
        ));
        if (appNavigationDO != null) {
            BeanUtils.copyProperties(appNavigationDO, respVO);
        }
        BeanUtils.copyProperties(applicationDO, respVO);
        return respVO;
    }

}
