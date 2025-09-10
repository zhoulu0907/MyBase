package com.cmsr.onebase.module.metadata.api.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.convert.entity.EntityFieldConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实体字段管理 API 实现类
 *
 * @author matianyu
 * @date 2025-08-14
 */
@RestController
@Validated
@Slf4j
public class MetadataEntityFieldApiImpl implements MetadataEntityFieldApi {

    @Resource
    private MetadataEntityFieldService entityFieldService;

    @Override
    public CommonResult<List<EntityFieldRespDTO>> getEntityFieldList(EntityFieldQueryReqDTO reqDTO) {
        log.info("RPC 接口 - 查询指定实体的字段列表，实体ID: {}", reqDTO.getEntityId());
        
        // 将DTO转换为Service层的VO
        EntityFieldQueryVO queryVO = EntityFieldConvert.INSTANCE.convertDTOToQueryVO(reqDTO);
        
        // 调用Service层方法
        List<MetadataEntityFieldDO> list = entityFieldService.getEntityFieldListByConditions(queryVO);
        
        // 转换结果为DTO
        List<EntityFieldRespDTO> result = EntityFieldConvert.INSTANCE.convertListToDTO(list);
        
        log.info("RPC 接口 - 完成查询指定实体的字段列表，实体ID: {}，返回记录数: {}", reqDTO.getEntityId(), result.size());
        return CommonResult.success(result);
    }

}
