package com.cmsr.onebase.module.build.controller.appresource.vo;

import java.util.List;

import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;

import lombok.Data;

@Data
public class LoadPageSetRespVO {
    private Long id;

    private String mainMetadata;

    private List<PageDTO> pages;
}
