package com.cmsr.onebase.module.infra.core.api.file;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.module.infra.api.file.dto.FileCreateReqDTO;
import com.cmsr.onebase.module.infra.service.file.FileService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class FileApiImpl implements FileApi {

    @Resource
    private FileService fileService;

    @Override
    public CommonResult<String> createFile(FileCreateReqDTO createReqDTO) {
        return success(fileService.createFile(createReqDTO.getContent(), createReqDTO.getName(),
                createReqDTO.getDirectory(), createReqDTO.getType()));
    }

}
