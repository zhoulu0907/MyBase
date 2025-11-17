package com.cmsr.onebase.module.bpm.core.vo.design.node;

import com.cmsr.onebase.module.bpm.core.dto.node.base.*;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeDataVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 抄送人节点配置
 *
 */
@Data
public class CcNodeVO extends BaseNodeVO {
    /**
     * 节点自定义配置，必须传
     */
    @Valid
    @NotNull(message = "节点自定义配置不能为空")
    private CcNodeDataVO data;

    /**
     * 抄送人节点配置
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class CcNodeDataVO extends BaseNodeDataVO {
        /**
         * 抄送人配置
         */
        @Valid
        @NotNull(message = "抄送人配置不能为空")
        private CopyReceiverConfigDTO copyReceiverConfig;

        /**
         * 字段权限配置
         */
        @Valid
        private FieldPermCfgDTO fieldPermConfig = new FieldPermCfgDTO();
    }
}
