package com.cmsr.onebase.module.metadata.api.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源管理 API 实现
 *
 * @author matianyu
 * @date 2025-09-10
 */
@RestController
@Validated
@Slf4j
public class MetadataDatasourceApiImpl implements MetadataDatasourceApi {

    @Resource
    private MetadataDatasourceService datasourceService;

    @Resource
    private ModelMapper modelMapper;

    @Override
    public CommonResult<Long> createDefaultDatasource(@Valid DatasourceCreateDefaultReqDTO reqDTO) {
        try {
            log.info("创建默认数据源，应用ID: {}, 应用UID: {}", reqDTO.getAppId(), reqDTO.getAppUid());
            
            // 创建默认数据源配置
            DatasourceSaveReqVO datasourceVO = new DatasourceSaveReqVO();
            datasourceVO.setDatasourceName("默认数据源");
            datasourceVO.setCode("default");
            datasourceVO.setDatasourceType("mysql");
            datasourceVO.setAppId(String.valueOf(reqDTO.getAppId()));
            datasourceVO.setDescription("系统自动创建的默认数据源");
            
            // 默认配置（这里可以根据实际需要调整）
            Map<String, Object> defaultConfig = new HashMap<>();
            defaultConfig.put("url", "jdbc:mysql://localhost:3306/onebase_default");
            defaultConfig.put("username", "root");
            defaultConfig.put("password", "");
            defaultConfig.put("driverClassName", "com.mysql.cj.jdbc.Driver");
            datasourceVO.setConfig(defaultConfig);
            
            Long datasourceId = datasourceService.createDatasource(datasourceVO);
            return CommonResult.success(datasourceId);
        } catch (Exception e) {
            log.error("创建默认数据源失败", e);
            return CommonResult.<Long>error(500, "创建默认数据源失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Long> createDatasource(@Valid DatasourceSaveReqDTO reqDTO) {
        try {
            log.info("创建数据源，名称: {}, 代码: {}", reqDTO.getName(), reqDTO.getCode());
            
            DatasourceSaveReqVO datasourceVO = new DatasourceSaveReqVO();
            datasourceVO.setDatasourceName(reqDTO.getName());
            datasourceVO.setCode(reqDTO.getCode());
            datasourceVO.setDatasourceType(reqDTO.getDatasourceType());
            datasourceVO.setAppId(String.valueOf(reqDTO.getAppId()));
            datasourceVO.setDescription(reqDTO.getRemark());
            
            // 处理config字段 - DTO的config是String，VO的config是Map
            Map<String, Object> configMap = new HashMap<>();
            if (reqDTO.getConfig() != null && !reqDTO.getConfig().trim().isEmpty()) {
                // 这里可以解析JSON字符串为Map，暂时设置为简单的配置
                configMap.put("config", reqDTO.getConfig());
            }
            datasourceVO.setConfig(configMap);
            Long datasourceId = datasourceService.createDatasource(datasourceVO);
            return CommonResult.success(datasourceId);
        } catch (Exception e) {
            log.error("创建数据源失败", e);
            return CommonResult.<Long>error(500, "创建数据源失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Object> getDatasource(Long id) {
        try {
            log.info("获取数据源，ID: {}", id);
            
            // 调用service获取数据源信息
            Object datasource = datasourceService.getDatasource(id);
            return CommonResult.success(datasource);
        } catch (Exception e) {
            log.error("获取数据源失败", e);
            return CommonResult.<Object>error(500, "获取数据源失败：" + e.getMessage());
        }
    }
}
