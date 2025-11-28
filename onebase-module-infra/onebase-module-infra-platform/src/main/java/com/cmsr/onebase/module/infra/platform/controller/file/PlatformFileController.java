package com.cmsr.onebase.module.infra.platform.controller.file;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.module.infra.dal.vo.file.file.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cmsr.onebase.module.infra.service.file.FileService;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理平台flatform - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class PlatformFileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    public CommonResult<String> uploadFile(FileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return success(fileService.createFile(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType()));
    }
}
