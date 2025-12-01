package com.cmsr.onebase.module.infra.api.file;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.constant.ApiConstants;
import com.cmsr.onebase.module.infra.api.file.dto.FileCreateReqDTO;
import com.cmsr.onebase.module.infra.api.file.dto.FileListRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@FeignClient(name = ApiConstants.NAME) // TODO 开发者：fallbackFactory =
@Tag(name = "RPC 服务 - 文件")
public interface FileApi {

    String PREFIX = ApiConstants.PREFIX + "/file";

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(byte[] content) {
        return createFile(content, null, null, null);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content 文件内容
     * @param name 文件名称，允许空
     * @return 文件路径
     */
    default String createFile(byte[] content, String name) {
        return createFile(content, name, null, null);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content 文件内容
     * @param name 文件名称，允许空
     * @param directory 目录，允许空
     * @param type 文件的 MIME 类型，允许空
     * @return 文件路径
     */
    default String createFile(@NotEmpty(message = "文件内容不能为空") byte[] content,
                              String name, String directory, String type) {
        return createFile(new FileCreateReqDTO().setName(name).setDirectory(directory).setType(type).setContent(content)).getCheckedData();
    }

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "保存文件，并返回文件的访问路径")
    CommonResult<String> createFile(@Valid @RequestBody FileCreateReqDTO createReqDTO);

    @GetMapping( PREFIX + "/list-by-ids")
    @Operation(summary = "根据文件 ID 列表获取文件详情列表")
    @Parameter(name = "ids", description = "文件 ID 列表", required = true)
    CommonResult<List<FileListRespDTO>> getFileListByIds(@RequestParam("ids") Collection<Long> ids);

    @GetMapping(PREFIX + "/download/{id}")
    @Operation(summary = "获取文件内容")
    @Parameter(name = "id", description = "文件编号", required = true)
    void getFileContent(@PathVariable("id") Long id, HttpServletResponse response) throws Exception;

}
