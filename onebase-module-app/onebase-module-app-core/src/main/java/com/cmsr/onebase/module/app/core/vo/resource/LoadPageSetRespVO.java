package com.cmsr.onebase.module.app.core.vo.resource;

import java.util.List;

import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;

import lombok.Data;

@Data
public class LoadPageSetRespVO {
    private Long id;

    private String mainMetadata;

    private Integer pageSetType;

    private List<PageDTO> pages;
}
