package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.project.ProjectInfoDO;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemProjectInfoMapper;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 项目信息数据访问层
 *
 * @author claude
 * @date 2026-03-23
 */
@Repository
public class ProjectInfoDataRepository extends BaseDataRepository<SystemProjectInfoMapper, ProjectInfoDO> {

    /**
     * 根据项目编码查找项目
     *
     * @param projectCode 项目编码
     * @return 项目
     */
    public ProjectInfoDO findOneByProjectCode(String projectCode) {
        if (StringUtils.isBlank(projectCode)) {
            return null;
        }
        return getOne(query().eq(ProjectInfoDO.PROJECT_CODE, projectCode));
    }

    /**
     * 根据外部项目ID和来源平台查询
     * @param externalId
     * @param sourcePlatform
     * @return
     */
    public ProjectInfoDO findByIdAndSource(String externalId, String sourcePlatform) {
        return getOne(query().eq(ProjectInfoDO.EXTERNAL_PROJECT_ID, externalId).eq(ProjectInfoDO.SOURCE_PLATFORM, sourcePlatform));
    }

    /**
     * 根据状态查询项目列表
     *
     * @param status 状态
     * @return 项目列表
     */
    public List<ProjectInfoDO> findListByStatus(Integer status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return list(query().eq(ProjectInfoDO.STATUS, status));
    }

    /**
     * 分页查询项目
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<ProjectInfoDO> findPage(ProjectInfoPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query()
                .like(ProjectInfoDO.PROJECT_CODE, pageReqVO.getProjectCode(), StringUtils.isNotBlank(pageReqVO.getProjectCode()))
                .like(ProjectInfoDO.PROJECT_NAME, pageReqVO.getProjectName(), StringUtils.isNotBlank(pageReqVO.getProjectName()))
                .eq(ProjectInfoDO.STATUS, pageReqVO.getStatus(), pageReqVO.getStatus() != null)
                .eq(ProjectInfoDO.SOURCE_PLATFORM, pageReqVO.getSourcePlatform(), StringUtils.isNotBlank(pageReqVO.getSourcePlatform()))
                .orderBy(ID, false);

        Page<ProjectInfoDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}