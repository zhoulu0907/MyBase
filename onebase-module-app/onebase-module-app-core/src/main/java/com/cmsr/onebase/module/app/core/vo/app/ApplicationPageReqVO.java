package com.cmsr.onebase.module.app.core.vo.app;

import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:04
 */
@Schema(description = "应用管理 - 应用分页 Request VO")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApplicationPageReqVO extends PageParam {

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "是否查询当前用户创建的应用还是全部的应用")
    private Integer ownerTag;

    @Schema(description = "根据创建时间或者更新时间排序", example = "create,update")
    private String orderByTime;

    @Schema(description = "应用状态")
    private Integer status;

    @Schema(description = "发布模式")
    @InEnum(value = CommonPublishModelEnum.class, message = "返回值类型必须是 {value}")
    private String publishModel;

}
