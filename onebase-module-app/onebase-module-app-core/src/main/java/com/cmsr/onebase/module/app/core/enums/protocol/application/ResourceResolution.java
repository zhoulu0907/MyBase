package com.cmsr.onebase.module.app.core.enums.protocol.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceResolution {
    private String[] groupOrder;
    private CrossGroupDependencies crossGroupDependencies;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrossGroupDependencies {
        private Boolean allow;

        private String[] requiredGroups;
    }
}