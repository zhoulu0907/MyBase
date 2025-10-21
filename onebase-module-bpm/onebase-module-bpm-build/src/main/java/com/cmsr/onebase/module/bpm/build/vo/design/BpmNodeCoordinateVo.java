package com.cmsr.onebase.module.bpm.build.vo.design;

import lombok.Data;

/**
 * 节点的坐标
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class BpmNodeCoordinateVo {
    /**
     * 节点的X坐标
     */
    private Integer x = 0;

    /**
     * 节点的Y坐标
     */
    private Integer y = 0;

    /**
     * 节点文本的X坐标
     */
    private Integer textX = 0;

    /**
     * 节点文本的Y坐标
     */
    private Integer textY = 0;

    /**
     * 默认为0，由前端自己控制节点宽度
     */
    private Integer width = 0;

    /**
     * 默认为0，由前端自己控制节点高度
     */
    private Integer height = 0;
}
