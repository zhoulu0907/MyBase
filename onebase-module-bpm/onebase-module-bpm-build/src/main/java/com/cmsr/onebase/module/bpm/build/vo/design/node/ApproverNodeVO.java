package com.cmsr.onebase.module.bpm.build.vo.design.node;

import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeDataVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 审批人节点配置
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class ApproverNodeVO extends BaseNodeVO {
    /**
     * 节点自定义配置，必须传
     */
    @Valid
    @NotNull(message = "节点自定义配置不能为空")
    private ApproverNodeDataVO data;

    /**
     * 审批人节点配置
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class ApproverNodeDataVO extends BaseNodeDataVO {
        /**
         * 审批人配置
         */
        @Valid
        @NotNull(message = "审批人配置不能为空")
        private ApproverConfigDTO approverConfig;

        /**
         * 按钮配置
         */
        @Valid
        @NotNull(message = "按钮配置不能为空")
        private List<ApproverNodeBtnCfgDTO> buttonConfigs;

        /**
         * 字段权限配置
         */
        @Valid
        private FieldPermCfgDTO fieldPermConfig = new FieldPermCfgDTO();
    }
}
