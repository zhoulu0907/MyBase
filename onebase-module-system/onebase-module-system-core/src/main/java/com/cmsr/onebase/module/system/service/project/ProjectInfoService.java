package com.cmsr.onebase.module.system.service.project;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.project.ProjectInfoDO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoCreateReqVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoPageReqVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoRespVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoUpdateReqVO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 项目信息 Service 接口
 *
 * @author claude
 * @date 2026-03-23
 */
public interface ProjectInfoService {

    /**
     * 创建项目
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProject(@Valid ProjectInfoCreateReqVO createReqVO);

    /**
     * 更新项目
     *
     * @param updateReqVO 更新信息
     */
    void updateProject(@Valid ProjectInfoUpdateReqVO updateReqVO);

    /**
     * 删除项目
     *
     * @param id 编号
     */
    void deleteProject(Long id);

    /**
     * 获得项目
     *
     * @param id 编号
     * @return 项目
     */
    ProjectInfoDO getProject(Long id);

    /**
     * 根据项目编码获得项目
     *
     * @param projectCode 项目编码
     * @return 项目
     */
    ProjectInfoDO getProjectByCode(String projectCode);

    /**
     * 获得项目分页
     *
     * @param pageReqVO 分页查询
     * @return 项目分页
     */
    PageResult<ProjectInfoRespVO> getProjectPage(ProjectInfoPageReqVO pageReqVO);

    /**
     * 获得启用的项目列表
     *
     * @return 项目列表
     */
    List<ProjectInfoRespVO> getEnabledProjectList();

}