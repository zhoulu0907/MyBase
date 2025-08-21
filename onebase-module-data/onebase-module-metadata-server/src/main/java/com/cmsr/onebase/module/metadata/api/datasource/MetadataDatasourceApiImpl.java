package com.cmsr.onebase.module.metadata.api.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceRespDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据源管理 API 实现类
 *
 * @author matianyu
 * @date 2025-08-13
 */
@RestController
@Validated
@Slf4j
public class MetadataDatasourceApiImpl implements MetadataDatasourceApi {

    @Resource
    private MetadataDatasourceService datasourceService;

    @Override
    public CommonResult<Long> createDefaultDatasource(DatasourceCreateDefaultReqDTO reqDTO) {
        Long appId = reqDTO.getAppId();
        String appUid = reqDTO.getAppUid();
        log.info("RPC 接口 - 创建默认数据源，应用ID: {}，appUid: {}", appId, appUid);

        Long id = datasourceService.createDefaultDatasource(appId, appUid);

        log.info("RPC 接口 - 完成创建默认数据源，应用ID: {}，appUid: {}，数据源ID: {}", appId, appUid, id);
        return CommonResult.success(id);
    }

    @Override
    public CommonResult<Long> createDatasource(DatasourceSaveReqDTO reqDTO) {
        log.info("RPC 接口 - 创建数据源，数据源名称: {}", reqDTO.getDatasourceName());

        // 转换为内部使用的VO
        DatasourceSaveReqVO reqVO = BeanUtils.toBean(reqDTO, DatasourceSaveReqVO.class);

        Long id = datasourceService.createDatasource(reqVO);

        log.info("RPC 接口 - 完成创建数据源，数据源名称: {}，数据源ID: {}", reqDTO.getDatasourceName(), id);
        return CommonResult.success(id);
    }

    @Override
    public CommonResult<Boolean> updateDatasource(DatasourceSaveReqDTO reqDTO) {
        log.info("RPC 接口 - 更新数据源，数据源ID: {}", reqDTO.getId());

        // 转换为内部使用的VO
        DatasourceSaveReqVO reqVO = BeanUtils.toBean(reqDTO, DatasourceSaveReqVO.class);

        datasourceService.updateDatasource(reqVO);

        log.info("RPC 接口 - 完成更新数据源，数据源ID: {}", reqDTO.getId());
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<Boolean> deleteDatasource(Long id) {
        log.info("RPC 接口 - 删除数据源，数据源ID: {}", id);

        datasourceService.deleteDatasource(id);

        log.info("RPC 接口 - 完成删除数据源，数据源ID: {}", id);
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<DatasourceRespDTO> getDatasource(Long id) {
        log.info("RPC 接口 - 查询数据源，数据源ID: {}", id);

        MetadataDatasourceDO datasource = datasourceService.getDatasource(id);

        // 转换为响应DTO
        DatasourceRespDTO respDTO = BeanUtils.toBean(datasource, DatasourceRespDTO.class);

        log.info("RPC 接口 - 完成查询数据源，数据源ID: {}", id);
        return CommonResult.success(respDTO);
    }

}
