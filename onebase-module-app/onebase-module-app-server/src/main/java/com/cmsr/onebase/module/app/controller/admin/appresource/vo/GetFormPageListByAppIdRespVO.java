package com.cmsr.onebase.module.app.controller.admin.appresource.vo;

import com.cmsr.onebase.module.app.api.appresource.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @ClassName GetFormPageListByAppIdRespVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class GetFormPageListByAppIdRespVO {
    @Schema(description = "pages")
    private List<PageDTO> pages;
}
