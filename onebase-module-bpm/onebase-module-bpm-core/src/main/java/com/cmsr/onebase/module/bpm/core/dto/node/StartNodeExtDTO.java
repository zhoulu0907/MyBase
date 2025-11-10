package com.cmsr.onebase.module.bpm.core.dto.node;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import lombok.Data;

import java.util.List;

/**
 * 开始节点里的扩展字段信息
 *
 * @author liyang
 * @data 2025-10-24
 */
@Data
public class StartNodeExtDTO extends BaseNodeExtDTO {
    /**
     * 按钮配置，前端暂不可配置，默认有保存和提交按钮
     *
     * 同发起流程
     */
    private List<BaseNodeBtnCfgDTO> buttonConfigs;
}
