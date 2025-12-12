package com.cmsr.onebase.module.system.vo.user;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.CorpAppReationStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;


@Schema(description = "用户应用关联表分页")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserAppPageReqVO extends PageParam {

    @Schema(description = "用户ID")
   // @NotNull(message = "用户id不能为空")
    private Set<Long> userIds;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "状态")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;
}