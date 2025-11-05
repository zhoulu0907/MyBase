package com.cmsr.onebase.module.bpm.build.vo.vermgmt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private OperationUser creator;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改人")
    private OperationUser updater;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "操作人信息")
    @Data
    public static class OperationUser  {
        @Schema(description = "操作人ID")
        private Long operationUserId;

        @Schema(description = "操作人名称")
        private String operationName;

        @Schema(description = "操作人头像")
        private String operationUserAvatar;
    }
}
