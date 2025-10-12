package com.cmsr.onebase.module.app.core.dto.appresource;

import java.util.List;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class PageDTO {
    private Long id;

    private String pageName;

    private String pageType;

    private Boolean editViewMode;

    private Boolean detailViewMode;

    private Boolean isDefaultEditViewMode;

    private Boolean isDefaultDetailViewMode;

    private List<ComponentDTO> components;

    private Boolean created;
}
