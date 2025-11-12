package com.cmsr.onebase.module.bpm.core.vo.design.node;

import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeDataVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;
import lombok.Data;

/**
 * 开始节点配置
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class StartNodeVO extends BaseNodeVO {
    /**
     * 节点自定义配置
     */
    private DataVO data;

    /**
     * 开始节点视图
     *
     * @author liyang
     * @date 2025-10-23
     */
    @Data
    public static class DataVO extends BaseNodeDataVO {

    }
}
