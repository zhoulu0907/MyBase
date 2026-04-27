package com.cmsr.onebase.module.app.api.appresource.dto;

import lombok.Data;

/**
 * @author liyang
 */
@Data
public class PageRespDTO {
    private Long id;

    private String pageUuid;

    private String pageName;

    private String pageType;

    private Integer editViewMode;

    private Integer detailViewMode;

    private Integer isDefaultEditViewMode;

    private Integer isDefaultDetailViewMode;

    private Integer isLatestUpdated;

    private Boolean created;

    private String interactionRules;
}
