package com.cmsr.onebase.module.system.service.project;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.project.ProjectInfoDO;
import com.cmsr.onebase.module.system.dal.database.ProjectInfoDataRepository;
import com.cmsr.onebase.module.system.enums.project.ProjectSourceEnum;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoCreateReqVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoPageReqVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoRespVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoUpdateReqVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 项目信息 Service 实现类
 *
 * @author claude
 * @date 2026-03-23
 */
@Service
@Validated
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Resource
    private ProjectInfoDataRepository projectInfoDataRepository;

    @Override
    public Long createProject(ProjectInfoCreateReqVO createReqVO) {
        // 校验项目编码是否重复
        validateProjectCodeUnique(null, createReqVO.getProjectCode());
        // 插入
        ProjectInfoDO project = BeanUtils.toBean(createReqVO, ProjectInfoDO.class);
        // 设置默认值
        if (project.getSourcePlatform() == null) {
            project.setSourcePlatform(ProjectSourceEnum.INTERNAL.getSource());
        }
        if (project.getStatus() == null) {
            project.setStatus(CommonStatusEnum.ENABLE.getStatus());
        }
        projectInfoDataRepository.insert(project);
        return project.getId();
    }

    @Override
    public void updateProject(ProjectInfoUpdateReqVO updateReqVO) {
        // 校验存在
        validateProjectExists(updateReqVO.getId());
        // 更新
        ProjectInfoDO updateObj = BeanUtils.toBean(updateReqVO, ProjectInfoDO.class);
        projectInfoDataRepository.update(updateObj);
    }

    @Override
    public void deleteProject(Long id) {
        // 校验存在
        validateProjectExists(id);
        // 删除
        projectInfoDataRepository.deleteById(id);
    }

    @Override
    public ProjectInfoDO getProject(Long id) {
        return projectInfoDataRepository.findById(id);
    }

    @Override
    public ProjectInfoDO getProjectByCode(String projectCode) {
        return projectInfoDataRepository.findOneByProjectCode(projectCode);
    }

    @Override
    public PageResult<ProjectInfoRespVO> getProjectPage(ProjectInfoPageReqVO pageReqVO) {
        PageResult<ProjectInfoDO> pageResult = projectInfoDataRepository.findPage(pageReqVO);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), ProjectInfoRespVO.class), pageResult.getTotal());
    }

    @Override
    public List<ProjectInfoRespVO> getEnabledProjectList() {
        List<ProjectInfoDO> list = projectInfoDataRepository.findListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return BeanUtils.toBean(list, ProjectInfoRespVO.class);
    }

    /**
     * 校验项目是否存在
     *
     * @param id 项目ID
     */
    private void validateProjectExists(Long id) {
        if (projectInfoDataRepository.findById(id) == null) {
            throw exception(PROJECT_NOT_EXISTS);
        }
    }

    /**
     * 校验项目编码是否唯一
     *
     * @param id          项目ID
     * @param projectCode 项目编码
     */
    private void validateProjectCodeUnique(Long id, String projectCode) {
        ProjectInfoDO project = projectInfoDataRepository.findOneByProjectCode(projectCode);
        if (project == null) {
            return;
        }
        // 如果 id 为空，说明不用存在
        if (id == null) {
            throw exception(PROJECT_CODE_EXISTS);
        }
        // 如果 id 不为空，说明是更新，判断是否是同一个
        if (!project.getId().equals(id)) {
            throw exception(PROJECT_CODE_EXISTS);
        }
    }

}