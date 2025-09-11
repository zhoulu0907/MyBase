package com.cmsr.onebase.module.metadata.api.validation.impl;

import com.cmsr.onebase.module.metadata.api.validation.MetadataPermitApi;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 权限操作类型 API 内存 Stub 实现
 */
@Slf4j
@Service
@Primary
public class MetadataPermitApiImpl implements MetadataPermitApi {
    @Override
    public List<PermitRefOtftRespDTO> getPermitRefOtftList() {
        log.info("Stub getPermitRefOtftList 返回空列表");
        return Collections.emptyList();
    }
}
