package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourcePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.DATASOURCE_CODE_DUPLICATE;

/**
 * 数据源 Service 实现类
 */
@Service
@Slf4j
public class MetadataDatasourceServiceImpl implements MetadataDatasourceService {

    @Resource
    private DataRepository dataRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDatasource(@Valid DatasourceSaveReqVO createReqVO) {
        // 校验编码唯一性
        validateDatasourceCodeUnique(null, createReqVO.getCode(), createReqVO.getAppId());

        // 插入数据源
        MetadataDatasourceDO datasource = BeanUtils.toBean(createReqVO, MetadataDatasourceDO.class);
        dataRepository.insert(datasource);
        
        return datasource.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDatasource(@Valid DatasourceSaveReqVO updateReqVO) {
        // 校验存在
        validateDatasourceExists(updateReqVO.getId());
        // 校验编码唯一性
        validateDatasourceCodeUnique(updateReqVO.getId(), updateReqVO.getCode(), updateReqVO.getAppId());

        // 更新数据源
        MetadataDatasourceDO updateObj = BeanUtils.toBean(updateReqVO, MetadataDatasourceDO.class);
        dataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDatasource(Long id) {
        // 校验存在
        validateDatasourceExists(id);
        
        // 删除数据源
        dataRepository.deleteById(MetadataDatasourceDO.class, id);
    }

    private void validateDatasourceExists(Long id) {
        if (dataRepository.findById(MetadataDatasourceDO.class, id) == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
    }

    private void validateDatasourceCodeUnique(Long id, String code, Long appId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        configStore.and("app_id", appId);
        if (id != null) {
            configStore.and("id", "!=", id);
        }
        
        long count = dataRepository.countByConfig(MetadataDatasourceDO.class, configStore);
        if (count > 0) {
            throw exception(DATASOURCE_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataDatasourceDO getDatasource(Long id) {
        return dataRepository.findById(MetadataDatasourceDO.class, id);
    }

    @Override
    public PageResult<MetadataDatasourceDO> getDatasourcePage(DatasourcePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        // 添加查询条件
        if (pageReqVO.getDatasourceName() != null) {
            configStore.and("datasource_name", "LIKE", "%" + pageReqVO.getDatasourceName() + "%");
        }
        if (pageReqVO.getCode() != null) {
            configStore.and("code", "LIKE", "%" + pageReqVO.getCode() + "%");
        }
        if (pageReqVO.getDatasourceType() != null) {
            configStore.and("datasource_type", pageReqVO.getDatasourceType());
        }
        if (pageReqVO.getRunMode() != null) {
            configStore.and("run_mode", pageReqVO.getRunMode());
        }
        if (pageReqVO.getAppId() != null) {
            configStore.and("app_id", pageReqVO.getAppId());
        }
        
        // 分页查询
        return dataRepository.findPageWithConditions(MetadataDatasourceDO.class, configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourceList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", Order.TYPE.DESC);
        return dataRepository.findAllByConfig(MetadataDatasourceDO.class, configStore);
    }

    @Override
    public MetadataDatasourceDO getDatasourceByCode(String code) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        return dataRepository.findOne(MetadataDatasourceDO.class, configStore);
    }

}
