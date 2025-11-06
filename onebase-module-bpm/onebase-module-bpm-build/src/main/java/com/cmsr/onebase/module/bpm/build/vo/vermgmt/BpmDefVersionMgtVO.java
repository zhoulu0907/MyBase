package com.cmsr.onebase.module.bpm.build.vo.vermgmt;

import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程版本管理VO
 *
 */

@Data
@Schema(description = "流程定义VO")
public class BpmDefVersionMgtVO {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程版本")
    private String version;

    @Schema(description = "流程版本备注")
    private String versionAlias;

    @Schema(description = "版本状态")
    private String versionStatus;

    @Schema(description = "创建人")
    private UserBasicInfoVO creator;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改人")
    private UserBasicInfoVO updater;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
