package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;

import java.util.List;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;

@Schema(description = "记录上下文 DTO")
@Data
public class SemanticRecordContextDTO {
    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "是否包含子表")
    private Boolean containSubTable;

    @Schema(description = "是否包含关系")
    private Boolean containRelation;

    @Schema(description = "方法编码（可选）")
    private SemanticMethodCodeEnum methodCode;

    @Schema(description = "操作类型：CREATE/UPDATE/DELETE/GET/GET_PAGE")
    private MetadataDataMethodOpEnum operationType;

    @Schema(description = "页码")
    private Integer pageNo;

    @Schema(description = "分页大小")
    private Integer pageSize;

    @Schema(description = "多字段排序规则")
    private List<SemanticSortRuleDTO> sortBy;

    @Schema(description = "过滤条件")
    private SemanticConditionDTO filters;

    @Schema(description = "权限上下文")
    private MetadataPermissionContext permissionContext;

    @Schema(description = "登录用户上下文")
    private LoginUserCtx loginUserCtx;
}
