package com.cmsr.onebase.module.app.core.vo.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
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
public class QueryComponentListRespVO {

    @Schema(description = "list")
    private List<ComponentDTO> list;

}
