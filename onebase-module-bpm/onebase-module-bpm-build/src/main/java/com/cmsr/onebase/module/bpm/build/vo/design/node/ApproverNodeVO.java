package com.cmsr.onebase.module.bpm.build.vo.design.node;

import com.cmsr.onebase.module.bpm.api.dto.node.base.ApproverConfigDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.ApproverNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeDataVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
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
     * 节点自定义配置
     */
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
        private ApproverConfigDTO approverConfig;

        /**
         * 按钮配置
         */
        private List<ApproverNodeBtnCfgDTO> buttonConfigs;

        /**
         * 字段权限配置
         */
        private FieldPermCfgDTO fieldPermConfig;
    }
}
