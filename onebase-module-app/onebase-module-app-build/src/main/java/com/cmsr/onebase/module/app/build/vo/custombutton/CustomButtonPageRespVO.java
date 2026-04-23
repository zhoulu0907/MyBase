package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自定义按钮-列表响应")
public class CustomButtonPageRespVO {

    @Schema(description = "按钮列表")
    private List<CustomButtonListItemRespVO> list;
}
