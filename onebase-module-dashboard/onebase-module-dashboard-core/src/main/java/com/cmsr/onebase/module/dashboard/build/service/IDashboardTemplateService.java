package com.cmsr.onebase.module.dashboard.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.vo.template.DashboardTemplatePageReqVO;
import com.cmsr.onebase.module.dashboard.build.vo.template.DashboardTemplateSaveReqVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 仪表盘模板Service接口
 */
public interface IDashboardTemplateService {

    /**
     * 创建仪表盘模板
     *
     * @param saveReqVO 创建信息
     * @return 仪表盘模板ID
     */
    Long createDashboardTemplate(DashboardTemplateSaveReqVO saveReqVO);

    /**
     * 更新仪表盘模板
     *
     * @param saveReqVO 更新信息
     */
    void updateDashboardTemplate(DashboardTemplateSaveReqVO saveReqVO);

    /**
     * 删除仪表盘模板
     *
     * @param id 仪表盘模板ID
     */
    void deleteDashboardTemplate(Long id);

    /**
     * 获得仪表盘模板
     *
     * @param id 仪表盘模板ID
     * @return 仪表盘模板
     */
    DashboardTemplateDO getDashboardTemplate(Long id);

    /**
     * 获得仪表盘模板列表
     *
     * @param ids 仪表盘模板ID集合
     * @return 仪表盘模板列表
     */
    List<DashboardTemplateDO> getDashboardTemplateList(List<Long> ids);

    /**
     * 分页查询仪表盘模板
     *
     * @param pageReqVO 分页查询条件
     * @return 仪表盘模板分页列表
     */
    PageResult<DashboardTemplateDO> getDashboardTemplatePage(DashboardTemplatePageReqVO pageReqVO);

}