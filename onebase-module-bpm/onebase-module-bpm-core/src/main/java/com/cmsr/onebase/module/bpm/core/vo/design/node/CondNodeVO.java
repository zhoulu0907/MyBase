package com.cmsr.onebase.module.bpm.core.vo.design.node;

import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeDataVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;
import lombok.Data;

/**
 *
 * 条件节点配置
 *
 * @author liyang
 * @date 2025-11-14
 */
@Data
public class CondNodeVO extends BaseNodeVO {
    /**
     * 节点自定义配置
     */
    private DataVO data;


    /**
     * 条件节点视图
     *
     * @author liyang
     * @date 2025-11-14
     */
    @Data
    public static class DataVO extends BaseNodeDataVO {
        // todo 增加自定义配置，如条件优先级等
    }
}
