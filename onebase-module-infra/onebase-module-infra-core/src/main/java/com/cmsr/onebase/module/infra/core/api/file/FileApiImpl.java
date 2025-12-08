package com.cmsr.onebase.module.infra.core.api.file;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.module.infra.api.file.dto.FileCreateReqDTO;
import com.cmsr.onebase.module.infra.api.file.dto.FileListRespDTO;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
import com.cmsr.onebase.module.infra.service.file.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class FileApiImpl implements FileApi {

    @Resource
    private FileService fileService;

    @Override
    public CommonResult<String> createFile(FileCreateReqDTO createReqDTO) {
        return success(fileService.createFile(createReqDTO.getContent(), createReqDTO.getName(),
                createReqDTO.getDirectory(), createReqDTO.getType(), FileVisitModeEnum.PERMISSION.getValue()));
    }

    @Override
    public CommonResult<List<FileListRespDTO>> getFileListByIds(Collection<Long> ids) {
        return success(BeanUtils.toBean(fileService.getFileListByIds(ids), FileListRespDTO.class));
    }

    @Override
    public void getFileContent(Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileService.getFileContent(id, request, response);
    }

}
