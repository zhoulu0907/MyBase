package com.cmsr.onebase.module.infra.build.controller.file;

import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileDO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePageReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FileRespVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FileUploadReqVO;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
import com.cmsr.onebase.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    public CommonResult<String> uploadFile(@Valid FileUploadReqVO uploadReqVO) throws Exception {
        if (FileVisitModeEnum.PERMISSION.getValue().equals(uploadReqVO.getVisitMode())) {
            return CommonResult.error(BAD_REQUEST);
        }
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return success(fileService.createFile(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType(), uploadReqVO.getVisitMode()));
    }

    // @PostMapping("/delete")
    // @Operation(summary = "删除文件")
    // @Parameter(name = "id", description = "编号", required = true)
    // @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    // public CommonResult<Boolean> deleteFile(@RequestParam("id") Long id) throws Exception {
    //     fileService.deleteFile(id);
    //     return success(true);
    // }

    // @GetMapping("/{configId}/get/**")
    // @PermitAll
    // @TenantIgnore
    // @Operation(summary = "下载文件")
    // @Parameter(name = "configId", description = "配置编号", required = true)
    // public void getFileContent(HttpServletRequest request,
    //                            HttpServletResponse response,
    //                            @PathVariable("configId") Long configId) throws Exception {
    //     // 获取请求的路径
    //     String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
    //     if (StrUtil.isEmpty(path)) {
    //         throw new IllegalArgumentException("结尾的 path 路径必须传递");
    //     }
    //     // 解码，解决中文路径的问题 https://gitee.com/zhijiantianya/onebase_v3/pulls/807/
    //     path = URLUtil.decode(path);
    //
    //     // 读取内容
    //     byte[] content = fileService.getFileContent(configId, path);
    //     if (content == null) {
    //         log.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
    //         response.setStatus(HttpStatus.NOT_FOUND.value());
    //         return;
    //     }
    //     writeAttachment(response, path, content);
    // }

    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<PageResult<FileRespVO>> getFilePage(@Valid FilePageReqVO pageVO) {
        PageResult<FileDO> pageResult = fileService.getFilePage(pageVO);

        return success(BeanUtils.toBean(pageResult, FileRespVO.class));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "获取文件内容")
    @PermitAll
    @TenantIgnore
    // @ApiSign
    @Parameter(name = "id", description = "文件编号", required = true)
    public void getFileContent(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileService.getFileContent(id, request, response, null);
    }

}
