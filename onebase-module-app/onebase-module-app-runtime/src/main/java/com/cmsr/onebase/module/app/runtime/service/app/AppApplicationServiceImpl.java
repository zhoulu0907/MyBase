package com.cmsr.onebase.module.app.runtime.service.app;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.ApplicationStatusEnum;
import com.cmsr.onebase.module.app.runtime.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.runtime.vo.tag.TagRespVO;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class AppApplicationServiceImpl implements AppApplicationService {


    @Resource
    private AppApplicationRepository applicationRepository;

    @Resource
    private AppApplicationTagRepository applicationTagRepository;

    @Resource
    private AppTagRepository tagRepository;

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
        //AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(applicationDO);
        ApplicationRespVO respVO = BeanUtils.toBean(applicationDO, ApplicationRespVO.class
                , vo -> {
                    vo.setAppStatusText(ApplicationStatusEnum.getText(vo.getAppStatus()));
                    vo.setTags(queryAppTags(vo.getId()));
                    //vo.setCreateUser(userHelper.getUserNickname(applicationDO.getCreator()));
                    //vo.setUpdateUser(userHelper.getUserNickname(applicationDO.getUpdater()));
                });
        return respVO;
    }


}
