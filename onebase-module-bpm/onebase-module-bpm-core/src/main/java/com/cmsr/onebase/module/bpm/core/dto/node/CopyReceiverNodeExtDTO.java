package com.cmsr.onebase.module.bpm.core.dto.node;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.CopyReceiverConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 抄送人节点里的扩展字段信息
 *
 * @author liyang
 * @data 2025-10-24
 */
@Data
public class CopyReceiverNodeExtDTO extends BaseNodeExtDTO {
    /**
     * 抄送人配置
     */
    @Valid
    @NotNull(message = "抄送人配置不能为空")
    private CopyReceiverConfigDTO copyReceiverConfig;

    /**
     * 字段权限配置
     */
    private FieldPermCfgDTO fieldPermConfig;
}
