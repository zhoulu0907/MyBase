package com.cmsr.onebase.module.app.build.vo.appresource;

import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ListPageViewRespVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class ListPageViewRespVO {
    @Schema(description = "pages")
    private List<PageDTO> pages;
}
