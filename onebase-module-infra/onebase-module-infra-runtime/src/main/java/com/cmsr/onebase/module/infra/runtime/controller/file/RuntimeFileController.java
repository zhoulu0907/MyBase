package com.cmsr.onebase.module.infra.runtime.controller.file;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileDO;
import com.cmsr.onebase.module.infra.dal.vo.app.AppFileUploadReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FileCreateReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FileListRespVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePresignedUrlRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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

import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "Runtime- 文件服务")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class RuntimeFileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public CommonResult<String> uploadFile(AppFileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return success(fileService.createFile(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType(),uploadReqVO.getEnvFlag()));
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @Parameters({
            @Parameter(name = "name", description = "文件名称", required = true),
            @Parameter(name = "directory", description = "文件目录")
    })
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(
            @RequestParam("name") String name,
            @RequestParam(value = "directory", required = false) String directory) {
        return success(fileService.getFilePresignedUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @PermitAll
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

    @GetMapping("/list-by-ids")
    @Operation(summary = "根据文件 ID 列表获取文件详情列表")
    @PermitAll
    @Parameter(name = "ids", description = "文件 ID 列表", required = true)
    public CommonResult<List<FileListRespVO>> getFileListByIds(@RequestParam("ids") Collection<Long> ids) {
        List<FileDO> fileList = fileService.getFileListByIds(ids);
        return success(BeanUtils.toBean(fileList, FileListRespVO.class));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "获取文件内容")
    @PermitAll
    @Parameters({
            @Parameter(name = "id", description = "文件编号", required = true),
            @Parameter(name = "envFlag", description = "文件环境标识")
    })
    public void getFileContent(@PathVariable("id") Long id, @RequestParam("envFlag") String envFlag, HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileService.getFileContent(id, envFlag,request, response);
    }
}