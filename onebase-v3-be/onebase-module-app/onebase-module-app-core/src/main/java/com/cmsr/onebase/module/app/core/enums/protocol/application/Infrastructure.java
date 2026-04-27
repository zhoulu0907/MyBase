package com.cmsr.onebase.module.app.core.enums.protocol.application;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Infrastructure {
    private String description;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Resource {
        private String type;
        private String name;
        private String version;
        private String description;
    }

    private List<Resource> resources;
}