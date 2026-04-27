package com.cmsr.onebase.module.infra.platform.controller.file;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.vo.file.file.*;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cmsr.onebase.module.infra.service.file.FileService;

import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "Platform - 文件服务")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class PlatformFileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @ApiSignIgnore
    public CommonResult<String> uploadFile(@Valid FileUploadReqVO uploadReqVO) throws Exception {
        if (FileVisitModeEnum.PERMISSION.getValue().equals(uploadReqVO.getVisitMode())){
            return CommonResult.error(BAD_REQUEST);
        }
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return success(fileService.createFile(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType(), uploadReqVO.getVisitMode()));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "获取文件内容")
    @PermitAll
    @TenantIgnore
    @ApiSignIgnore
    @Parameter(name = "id", description = "文件编号", required = true)
    public void getFileContent(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileService.getFileContent(id, request, response, null);
    }
}
