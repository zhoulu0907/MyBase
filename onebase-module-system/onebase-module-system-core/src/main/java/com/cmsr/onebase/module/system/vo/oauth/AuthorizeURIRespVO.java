package com.cmsr.onebase.module.system.vo.oauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "申请授权 - 返回授权信息 Request VO")
@Data
public class AuthorizeURIRespVO {

    @Schema(description = "回调地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.aaa.com")
    private String url;

    @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "c974ee03212f4e66b532d93d946a57fb")
    private String code;

    @Schema(description = "授权状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String state;

}
