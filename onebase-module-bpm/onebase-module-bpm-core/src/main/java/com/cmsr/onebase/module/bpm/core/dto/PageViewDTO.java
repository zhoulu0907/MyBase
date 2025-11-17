package com.cmsr.onebase.module.bpm.core.dto;

import lombok.Data;

/**
 * 页面视图DTO
 *
 * @author liyang
 * @date 2025-11-14
 */
@Data
public class PageViewDTO {
    /**
     * 视图ID
     */
    private Long viewId;

    /**
     * 视图名称
     */
    private String viewName;

    /**
     * 视图模式
     */
    private String viewMode;
}
