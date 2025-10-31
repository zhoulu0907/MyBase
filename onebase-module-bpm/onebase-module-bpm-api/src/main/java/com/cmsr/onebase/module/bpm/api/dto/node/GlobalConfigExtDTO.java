package com.cmsr.onebase.module.bpm.api.dto.node;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 全局配置扩展字段信息
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class GlobalConfigExtDTO {
    /**
     * 自动审批配置
     */
     private List<String> autoApprove;//todo  列出字段
     /**
     * 审批人为空时
     */
     private String autoApproveIsNll;
     /**
     * 流程撤回规则
     */
      private String flowRecallRules;
      /**
     * 流程退回规则
     */
      private String  flowReturnRule;
        /**
         * 流程发起人终止权限
         */
      private  String  InitiTermiFlowPerm ;

      /**
      * 表单摘要
      */
      private  List<FieldConfigDTO>  formSummary;


    @Data
      public static class FlowRecallRule {
          /**
          * 撤回权限
          */
         private String  recallPermission;
         /**
          * 撤回时机
          */
         private String  recallTiming;
      }
    @Data
    public static class FieldConfigDTO {
        /**
         * 字段ID
         */
        @NotBlank(message = "字段ID不能为空")
        private String fieldId;

        /**
         * 字段名
         */
        @NotBlank(message = "字段名不能为空")
        private String fieldName;


    }
}
