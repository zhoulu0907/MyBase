package com.cmsr.onebase.module.app.core.vo.resource;

import com.cmsr.onebase.module.app.core.dto.resource.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName GetPageListByAppIdRespVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class GetPageListByAppIdRespVO {
    @Schema(description = "pages")
    private List<PageDTO> pages;
}
