package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
public class CorpSimpleRespVO {

    @Schema(description = "企业名称")
    private String corpName;

    @Schema(description = "企业id")
    private String corpId;

    @Schema(description = "id")
    private  Long  id;



}
