package com.cmsr.onebase.module.app.controller.admin.appresource.vo;

import java.util.List;

import com.cmsr.onebase.module.app.api.appresource.dto.PageDTO;

import lombok.Data;

@Data
public class LoadPageSetRespVO {
    private String pageSetCode;

    private List<PageDTO> pages;
}
