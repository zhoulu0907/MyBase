package com.cmsr.onebase.module.app.core.dto.resource;

import lombok.Data;

@Data
public class CreatePageViewDTO {

    private Long pageSetId;

    private String viewType;

    private String viewName;

}
