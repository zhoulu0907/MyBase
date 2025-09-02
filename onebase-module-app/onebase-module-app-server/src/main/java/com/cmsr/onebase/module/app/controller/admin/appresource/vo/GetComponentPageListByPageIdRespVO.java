package com.cmsr.onebase.module.app.controller.admin.appresource.vo;

import com.cmsr.onebase.module.app.api.appresource.dto.ComponentDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName GetComponentPageListByPageIdRespVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class GetComponentPageListByPageIdRespVO {
    @Schema(description = "list")
    private List<ComponentDTO> list;
}
