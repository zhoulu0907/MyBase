package com.cmsr.onebase.module.dashboard.build.service.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.dal.mapper.DashboardTemplateMapper;
import com.cmsr.onebase.module.dashboard.build.service.DashboardTemplateService;
import com.cmsr.onebase.module.dashboard.build.vo.template.DashboardTemplatePageReqVO;
import com.cmsr.onebase.module.dashboard.build.vo.template.DashboardTemplateSaveReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.dashboard.build.enums.ErrorCodeConstants.TEMPLATE_NOT_EXISTS;

/**
 * 仪表盘模板 Service 实现类
 *
 * @author lingma
 */
@Slf4j
@Service
public class DashboardTemplateServiceImpl extends ServiceImpl<DashboardTemplateMapper, DashboardTemplateDO> implements DashboardTemplateService {

    @Resource
    private DashboardTemplateMapper dashboardTemplateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDashboardTemplate(DashboardTemplateSaveReqVO saveReqVO) {
        SecurityFrameworkUtils.getLoginUserId();
        DashboardTemplateDO templateDO = BeanUtils.toBean(saveReqVO, DashboardTemplateDO.class);
        templateDO.setId(null);
        templateDO.setCreator(SecurityFrameworkUtils.getLoginUserId());
        // 让数据库生成UUID
        dashboardTemplateMapper.insert(templateDO);
        return templateDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDashboardTemplate(DashboardTemplateSaveReqVO saveReqVO) {
        // 校验存在
        validateDashboardTemplateExists(saveReqVO.getId());

        // 更新
        DashboardTemplateDO updateObj = BeanUtils.toBean(saveReqVO, DashboardTemplateDO.class);
        updateObj.setUpdater(SecurityFrameworkUtils.getLoginUserId());
        dashboardTemplateMapper.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDashboardTemplate(Long id) {
        // 校验存在
        validateDashboardTemplateExists(id);

        // 删除
        dashboardTemplateMapper.deleteById(id);
    }

    private void validateDashboardTemplateExists(Long id) {

        if (dashboardTemplateMapper.selectOneById(id) == null) {
            throw exception(TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public DashboardTemplateDO getDashboardTemplate(Long id) {

        return dashboardTemplateMapper.selectOneById(id);
    }

    @Override
    public List<DashboardTemplateDO> getDashboardTemplateList(List<Long> ids) {
        return dashboardTemplateMapper.selectListByIds(ids);
    }

    @Override
    public PageResult<DashboardTemplateDO> getDashboardTemplatePage(DashboardTemplatePageReqVO pageReqVO) {

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(DashboardTemplateDO::getTemplateType, pageReqVO.getTemplateType(), StringUtils.isNotBlank(pageReqVO.getTemplateType()))
                .like(DashboardTemplateDO::getTemplateName, pageReqVO.getTemplateName(), StringUtils.isNotBlank(pageReqVO.getTemplateName()))
                .eq(DashboardTemplateDO::getHot, pageReqVO.getHot(), pageReqVO.getHot() != null)
                .eq(DashboardTemplateDO::getAppId, ApplicationManager.getApplicationId())
                .orderBy(DashboardTemplateDO::getCreateTime, false);

        Page<DashboardTemplateDO> page = dashboardTemplateMapper.paginate(pageReqVO.getPageNo(), pageReqVO.getPageSize(), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }
}