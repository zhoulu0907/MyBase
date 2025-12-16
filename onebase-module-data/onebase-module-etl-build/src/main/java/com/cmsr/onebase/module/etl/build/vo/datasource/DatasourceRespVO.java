package com.cmsr.onebase.module.etl.build.vo.datasource;

import com.cmsr.onebase.module.etl.common.entity.JdbcDatasourceConfig;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlDatasourceDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDateTime;

@Schema(description = "数据工厂 - 数据源创建/修改 VO")
@Data
public class DatasourceRespVO {

    @Schema(description = "数据源ID")
    private Long id;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "数据源编号")
    private String datasourceUuid;

    @Schema(description = "数据源名称信息")
    private String datasourceName;

    @Schema(description = "数据源描述")
    private String declaration;

    @Schema(description = "数据源类型信息")
    private String datasourceType;

    @Schema(description = "数据源配置信息")
    private ConnectProperties config;

    @Schema(description = "只读")
    private Boolean readonly;

    @Schema(description = "采集状态")
    private String collectStatus;

    @Schema(description = "采集开始时间")
    private LocalDateTime collectStartTime;

    @Schema(description = "采集结束时间")
    private LocalDateTime collectEndTime;

    public static DatasourceRespVO convertFrom(EtlDatasourceDO datasourceDO) {
        DatasourceRespVO respVO = new DatasourceRespVO();
        respVO.setId(datasourceDO.getId());
        respVO.setApplicationId(datasourceDO.getApplicationId());
        respVO.setDatasourceUuid(datasourceDO.getDatasourceUuid());
        respVO.setDatasourceName(datasourceDO.getDatasourceName());
        respVO.setDeclaration(datasourceDO.getDeclaration());
        respVO.setDatasourceType(datasourceDO.getDatasourceType());
        respVO.setReadonly(BooleanUtils.toBoolean(datasourceDO.getReadonly()));
        respVO.setCollectStatus(datasourceDO.getCollectStatus().getValue());
        respVO.setCollectStartTime(datasourceDO.getCollectStartTime());
        respVO.setCollectEndTime(datasourceDO.getCollectEndTime());
        return respVO;
    }
}
