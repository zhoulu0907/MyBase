package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLDatasourceMapper;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ETLDatasourceRepository extends ServiceImpl<ETLDatasourceMapper, ETLDatasourceDO> {

    private DataRepository<ETLDatasourceDO> dataRepository;

    @Autowired
    private AnylineService<ETLDatasourceDO> anylineService;

    @PostConstruct
    public void init() {
        dataRepository = new DataRepository<>(ETLDatasourceDO.class);
        dataRepository.setAnylineService(anylineService);
    }

    public PageResult<ETLDatasourceDO> getETLDatasourcePage(DatasourcePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query()
                .eq(ETLDatasourceDO::getApplicationId, pageReqVO.getApplicationId())
                .like(ETLDatasourceDO::getDatasourceCode, pageReqVO.getDatasourceCode(), StringUtils.isNotBlank(pageReqVO.getDatasourceCode()))
                .like(ETLDatasourceDO::getDatasourceName, pageReqVO.getDatasourceName(), StringUtils.isNotBlank(pageReqVO.getDatasourceName()))
                .eq(ETLDatasourceDO::getDatasourceType, pageReqVO.getDatasourceType(), StringUtils.isNotBlank(pageReqVO.getDatasourceType()))
                .eq(ETLDatasourceDO::getReadonly, pageReqVO.getReadonly(), pageReqVO.getReadonly() != null)
                .eq(ETLDatasourceDO::getCollectStatus, pageReqVO.getCollectStatus(), StringUtils.isNotBlank(pageReqVO.getCollectStatus()))
                .orderBy(ETLDatasourceDO::getUpdateTime, false)
                .orderBy(ETLDatasourceDO::getCreateTime, false);
        Page<ETLDatasourceDO> pageResult = getMapper().paginate(pageReqVO.getPageNo(), pageReqVO.getPageSize(), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public List<ETLDatasourceDO> findAllByApplicationIdWithWritable(Long applicationId, Integer writable) {
        QueryWrapper queryWrapper = query()
                .eq(ETLDatasourceDO::getApplicationId, applicationId)
                .eq(ETLDatasourceDO::getReadonly, 0, writable != null)
                .orderBy(ETLDatasourceDO::getUpdateTime, false)
                .orderBy(ETLDatasourceDO::getCreateTime, false);
        return getMapper().selectListByQuery(queryWrapper);
    }
}
