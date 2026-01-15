package com.cmsr.onebase.module.app.core.dto.resource;

import java.util.List;

import lombok.Data;

@Data
public class PageDTO {
    private Long id;

    private String pageUuid;

    private String pageSetUuid;

    private String pageName;

    private String pageType;

    private Integer editViewMode;

    private Integer detailViewMode;

    private Integer isDefaultEditViewMode;

    private Integer isDefaultDetailViewMode;

    private Integer isLatestUpdated;

    private List<ComponentDTO> components;

    private Boolean created;

    private String interactionRules;

    private String projectName;

    private String indexImage;

}
