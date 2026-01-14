package com.cmsr.onebase.module.bpm.runtime.vo.taskcenter;

import lombok.Data;

/**
 *
 * 概览数据
 *
 * @author liyang
 * @date 2026-01-14
 *
 */
@Data
public class BpmOverviewRespVO {
    /**
     * 待办数量
     */
    private int todoCount;

    /**
     * 已办数量
     */
    private int doneCount;

    /**
     * 抄送数量
     */
    private int ccCount;

    /**
     * 我创建的流程数量
     */
    private int myCreatedCount;
}