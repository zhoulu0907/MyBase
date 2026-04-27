package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlDatasourceMapper;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.cmsr.onebase.module.etl.core.dal.dataobject.table.EtlDatasourceTableDef.ETL_DATASOURCE;

@Repository
@Slf4j
public class EtlDatasourceRepository extends BaseAppRepository<EtlDatasourceMapper, EtlDatasourceDO> {

    public PageResult<EtlDatasourceDO> getEtlDatasourcePage(DatasourcePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query().select(
                        ETL_DATASOURCE.ID,
                        ETL_DATASOURCE.APPLICATION_ID,
                        ETL_DATASOURCE.DATASOURCE_UUID,
                        ETL_DATASOURCE.DATASOURCE_NAME,
                        ETL_DATASOURCE.DECLARATION,
                        ETL_DATASOURCE.DATASOURCE_TYPE,
                        ETL_DATASOURCE.COLLECT_STATUS,
                        ETL_DATASOURCE.COLLECT_START_TIME,
                        ETL_DATASOURCE.COLLECT_END_TIME,
                        ETL_DATASOURCE.READONLY
                )
                .where(ETL_DATASOURCE.APPLICATION_ID.eq(pageReqVO.getApplicationId()))
                .where(ETL_DATASOURCE.DATASOURCE_UUID.like(pageReqVO.getDatasourceUuid()).when(StringUtils.isNotBlank(pageReqVO.getDatasourceUuid())))
                .where(ETL_DATASOURCE.DATASOURCE_NAME.like(pageReqVO.getDatasourceName()).when(StringUtils.isNotBlank(pageReqVO.getDatasourceName())))
                .where(ETL_DATASOURCE.DATASOURCE_TYPE.eq(pageReqVO.getDatasourceType()).when(StringUtils.isNotBlank(pageReqVO.getDatasourceType())))
                .where(ETL_DATASOURCE.READONLY.eq(pageReqVO.getReadonly()).when(pageReqVO.getReadonly() != null))
                .where(ETL_DATASOURCE.COLLECT_STATUS.eq(pageReqVO.getCollectStatus()).when(StringUtils.isNotBlank(pageReqVO.getCollectStatus())))
                .orderBy(ETL_DATASOURCE.UPDATE_TIME, false)
                .orderBy(ETL_DATASOURCE.CREATE_TIME, false);
        Page<EtlDatasourceDO> pageQuery = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<EtlDatasourceDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public List<EtlDatasourceDO> findAllByApplicationIdWithWritable(Long applicationId, Integer writable) {
        QueryWrapper queryWrapper = query().select(
                        ETL_DATASOURCE.ID,
                        ETL_DATASOURCE.DATASOURCE_UUID,
                        ETL_DATASOURCE.DATASOURCE_NAME
                )
                .where(ETL_DATASOURCE.APPLICATION_ID.eq(applicationId))
                .where(ETL_DATASOURCE.READONLY.eq(writable).when(writable != null))
                .orderBy(ETL_DATASOURCE.UPDATE_TIME, false)
                .orderBy(ETL_DATASOURCE.CREATE_TIME, false);
        return this.list(queryWrapper);
    }

    public EtlDatasourceDO getByUuid(String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(ETL_DATASOURCE.DATASOURCE_UUID.eq(datasourceUuid));
        return this.getOne(queryWrapper);
    }

    public void changeCollectStatus(Long datasourceId, CollectStatus collectStatus, LocalDateTime editTime) {
        this.updateChain()
                .set(EtlDatasourceDO::getCollectStatus, collectStatus)
                .set(EtlDatasourceDO::getCollectStartTime, editTime, collectStatus == CollectStatus.RUNNING)
                .set(EtlDatasourceDO::getCollectEndTime, editTime, collectStatus == CollectStatus.SUCCESS || collectStatus == CollectStatus.FAILED)
                .where(EtlDatasourceDO::getId).eq(datasourceId)
                .update();
    }
}
