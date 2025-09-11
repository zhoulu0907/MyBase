package com.cmsr.onebase.module.metadata.api.entity.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 实体字段 API 内存 Stub 实现
 */
@Slf4j
@Service
@Primary
public class MetadataEntityFieldApiImpl implements MetadataEntityFieldApi {
    @Override
    public CommonResult<List<EntityFieldRespDTO>> getEntityFieldList(EntityFieldQueryReqDTO reqDTO) {
        log.info("Stub getEntityFieldList entityId={} 返回空列表", reqDTO.getEntityId());
        return CommonResult.success(Collections.emptyList());
    }
}
