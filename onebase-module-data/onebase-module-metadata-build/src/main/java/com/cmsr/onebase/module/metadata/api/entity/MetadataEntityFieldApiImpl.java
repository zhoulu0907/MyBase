package com.cmsr.onebase.module.metadata.api.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 实体字段管理 API 实现类
 *
 * @author matianyu
 * @date 2025-09-10
 */
@RestController
@Validated
@Slf4j
public class MetadataEntityFieldApiImpl implements MetadataEntityFieldApi {

    @Resource
    private MetadataEntityFieldService entityFieldService;

    @Resource
    private ModelMapper modelMapper;

    @Override
    public CommonResult<List<EntityFieldRespDTO>> getEntityFieldList(@Valid EntityFieldQueryReqDTO reqDTO) {
        log.info("RPC 接口 - 查询指定实体的字段列表，实体ID: {}", reqDTO.getEntityId());
        
        // 将DTO转换为Controller层的VO
        EntityFieldQueryReqVO queryVO = modelMapper.map(reqDTO, EntityFieldQueryReqVO.class);
        
        // 调用Service层方法
        List<EntityFieldRespVO> list = entityFieldService.getEntityFieldListWithRelated(queryVO);
        
        // 转换结果为DTO
        List<EntityFieldRespDTO> result = list.stream()
                .map(vo -> modelMapper.map(vo, EntityFieldRespDTO.class))
                .toList();
        
        log.info("RPC 接口 - 完成查询指定实体的字段列表，实体ID: {}，返回记录数: {}", reqDTO.getEntityId(), result.size());
        return CommonResult.success(result);
    }
}
