package com.cmsr.onebase.module.app.api.appresource.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageDTO {
    private String pageCode;

    private String pageName;

    private String pageType;

    private List<ComponentDTO> components;
}
