package com.cmsr.onebase.module.etl.build.vo.datasource;

import com.cmsr.onebase.module.etl.core.vo.ConnectCryptoProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "数据工厂 - 数据源 - 测试连接请求VO")
@Data
public class TestConnectionVO {
    @Schema(description = "数据源ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "数据源类型信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源类型不能为空")
    private String datasourceType;

    @Schema(description = "数据源配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源配置信息不能为空")
    private ConnectCryptoProperties config;

}
