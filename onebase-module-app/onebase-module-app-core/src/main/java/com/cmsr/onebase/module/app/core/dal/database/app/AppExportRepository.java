package com.cmsr.onebase.module.app.core.dal.database.app;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppExportDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppExportMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppExportTableDef.APP_EXPORT;

/**
 * @ClassName AppExportRepository
 * @Description TODO
 * @Author mickey
 * @Date 2026/1/26 11:31
 */
@Repository
public class AppExportRepository extends ServiceImpl<AppExportMapper, AppExportDO> {

    /**
     * 分页查询导出记录
     *
     * @param applicationId 应用ID
     * @param exportStatus  导出状态
     * @param pageParam     分页参数
     * @return 分页结果
     */
    public PageResult<AppExportDO> selectPage(Long applicationId, Integer exportStatus, PageParam pageParam) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_EXPORT.APPLICATION_ID.eq(applicationId).when(applicationId != null))
                .where(APP_EXPORT.EXPORT_STATUS.eq(exportStatus).when(exportStatus != null))
                .orderBy(APP_EXPORT.CREATE_TIME, false);
        Page<AppExportDO> pageQuery = Page.of(pageParam.getPageNo(), pageParam.getPageSize());
        Page<AppExportDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

}
