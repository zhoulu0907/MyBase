package com.cmsr.onebase.module.app.core.enums.protocol.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationSpec {
    private String description;
    private String icon;
    private String entrypoint;

    private ResourceGroups resourceGroups;

    private ResourceResolution resourceResolution;

    private ImportStrategy importStrategy;

    private Extensions extensions;
}
