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

@Repository
@Slf4j
public class EtlDatasourceRepository extends BaseAppRepository<EtlDatasourceMapper, EtlDatasourceDO> {

    public PageResult<EtlDatasourceDO> getEtlDatasourcePage(DatasourcePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query().select()
                .eq(EtlDatasourceDO::getApplicationId, pageReqVO.getApplicationId())
                .like(EtlDatasourceDO::getDatasourceUuid, pageReqVO.getDatasourceUuid(), StringUtils::isNotBlank)
                .like(EtlDatasourceDO::getDatasourceName, pageReqVO.getDatasourceName(), StringUtils::isNotBlank)
                .eq(EtlDatasourceDO::getDatasourceType, pageReqVO.getDatasourceType(), StringUtils::isNotBlank)
                .eq(EtlDatasourceDO::getReadonly, pageReqVO.getReadonly(), pageReqVO.getReadonly() != null)
                .eq(EtlDatasourceDO::getCollectStatus, pageReqVO.getCollectStatus(), StringUtils::isNotBlank)
                .orderBy(EtlDatasourceDO::getUpdateTime, false)
                .orderBy(EtlDatasourceDO::getCreateTime, false);
        Page<EtlDatasourceDO> pageQuery = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<EtlDatasourceDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public List<EtlDatasourceDO> findAllByApplicationIdWithWritable(Long applicationId, Integer writable) {
        QueryWrapper queryWrapper = query().select(
                        EtlDatasourceDO::getId,
                        EtlDatasourceDO::getDatasourceUuid,
                        EtlDatasourceDO::getDatasourceName
                )
                .eq(EtlDatasourceDO::getApplicationId, applicationId)
                .eq(EtlDatasourceDO::getReadonly, 0, writable != null)
                .orderBy(EtlDatasourceDO::getUpdateTime, false)
                .orderBy(EtlDatasourceDO::getCreateTime, false);
        return this.list(queryWrapper);
    }

    public EtlDatasourceDO getByUuid(String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(EtlDatasourceDO::getDatasourceUuid, datasourceUuid);
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
