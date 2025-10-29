package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 按钮列表VO
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "按钮列表VO")
public class ListActButtonRespVO {
    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 操作按钮列表
     */
   private List<ActionButton> buttons;

   @Data
   public static class ActionButton {
       @Schema(description = "按钮名称")
       private String buttonName;

       @Schema(description = "按钮类型")
       private String buttonType;

       @Schema(description = "按钮显示名称")
       private String displayName;

       @Schema(description = "按钮编码")
       private String buttonCode;
   }
}