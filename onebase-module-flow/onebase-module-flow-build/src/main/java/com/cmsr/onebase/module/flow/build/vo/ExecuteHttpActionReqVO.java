package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Schema(description = "执行HTTP连接器动作请求VO")
@Data
public class ExecuteHttpActionReqVO {

    @Schema(description = "路径参数值（对应pathParams中的key）", example = "{\"userId\": \"12345\"}")
    private Map<String, Object> pathParams;

    @Schema(description = "查询参数值（对应queryParams中的key）", example = "{\"includeDetail\": \"true\"}")
    private Map<String, Object> queryParams;

    @Schema(description = "请求头值（对应requestHeaders中的key）", example = "{\"Authorization\": \"Bearer token\"}")
    private Map<String, Object> headers;

    @Schema(description = "请求体值（对应requestBody中的key）", example = "{\"name\": \"test\"}")
    private Map<String, Object> bodyParams;
}
