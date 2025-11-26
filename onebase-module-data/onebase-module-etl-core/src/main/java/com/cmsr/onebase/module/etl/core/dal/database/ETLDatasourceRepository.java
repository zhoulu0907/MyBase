package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLDatasourceMapper;
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
public class ETLDatasourceRepository extends BaseAppRepository<ETLDatasourceMapper, ETLDatasourceDO> {

    public PageResult<ETLDatasourceDO> getETLDatasourcePage(DatasourcePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query().select()
                .eq(ETLDatasourceDO::getApplicationId, pageReqVO.getApplicationId())
                .like(ETLDatasourceDO::getDatasourceUuid, pageReqVO.getDatasourceUuid(), StringUtils::isNotBlank)
                .like(ETLDatasourceDO::getDatasourceName, pageReqVO.getDatasourceName(), StringUtils::isNotBlank)
                .eq(ETLDatasourceDO::getDatasourceType, pageReqVO.getDatasourceType(), StringUtils::isNotBlank)
                .eq(ETLDatasourceDO::getReadonly, pageReqVO.getReadonly(), pageReqVO.getReadonly() != null)
                .eq(ETLDatasourceDO::getCollectStatus, pageReqVO.getCollectStatus(), StringUtils::isNotBlank)
                .orderBy(ETLDatasourceDO::getUpdateTime, false)
                .orderBy(ETLDatasourceDO::getCreateTime, false);
        Page<ETLDatasourceDO> pageQuery = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<ETLDatasourceDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public List<ETLDatasourceDO> findAllByApplicationIdWithWritable(Long applicationId, Integer writable) {
        QueryWrapper queryWrapper = query().select(
                        ETLDatasourceDO::getId,
                        ETLDatasourceDO::getDatasourceUuid,
                        ETLDatasourceDO::getDatasourceName
                )
                .eq(ETLDatasourceDO::getApplicationId, applicationId)
                .eq(ETLDatasourceDO::getReadonly, 0, writable != null)
                .orderBy(ETLDatasourceDO::getUpdateTime, false)
                .orderBy(ETLDatasourceDO::getCreateTime, false);
        return this.list(queryWrapper);
    }

    public ETLDatasourceDO getByUuid(String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(ETLDatasourceDO::getDatasourceUuid, datasourceUuid);
        return this.getOne(queryWrapper);
    }

    public void changeCollectStatus(Long datasourceId, CollectStatus collectStatus, LocalDateTime editTime) {
        this.updateChain()
                .set(ETLDatasourceDO::getCollectStatus, collectStatus)
                .set(ETLDatasourceDO::getCollectStartTime, editTime, collectStatus == CollectStatus.RUNNING)
                .set(ETLDatasourceDO::getCollectEndTime, editTime, collectStatus == CollectStatus.SUCCESS || collectStatus == CollectStatus.FAILED)
                .where(ETLDatasourceDO::getId).eq(datasourceId)
                .update();
    }
}
