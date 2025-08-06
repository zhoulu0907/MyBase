package com.cmsr.onebase.module.metadata.service.helper;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.annotation.Resource;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数据源服务助手类
 * <p>
 * 提供数据源相关的公共服务方法，避免在多个Service中重复相同的逻辑
 *
 * @author matianyu
 * @date 2025-08-05
 */
@Component
public class DatasourceServiceHelper {

    @Resource
    private DataRepository dataRepository;
    
    @Resource
    private DatasourceConvert datasourceConvert;

    /**
     * 根据数据源DO对象创建临时的AnylineService用于数据库操作
     *
     * @param datasource 数据源配置对象
     * @return AnylineService实例
     */
    public AnylineService<?> createTemporaryService(MetadataDatasourceDO datasource) {
        // 从数据源配置中获取连接参数
        Map<String, Object> config = datasourceConvert.stringToMap(datasource.getConfig());
        config.put("datasourceType", datasource.getDatasourceType());
        
        // 使用 DataRepository 的统一方法创建临时服务
        return dataRepository.createTemporaryService(config);
    }
}
