package com.cmsr.onebase.module.bpm.core.dto;

import lombok.Data;

/**
 * 页面视图DTO
 *
 * @author liyang
 * @date 2025-11-14
 */
@Data
public class PageViewGroupDTO {
    /**
     * 编辑页面视图
     */
   private PageViewDTO editPageView;

   /**
     * 详情页面视图
     */
   private PageViewDTO detailPageView;
}
