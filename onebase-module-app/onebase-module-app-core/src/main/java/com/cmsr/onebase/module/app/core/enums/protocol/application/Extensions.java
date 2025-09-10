package com.cmsr.onebase.module.app.core.enums.protocol.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Extensions {
    private Hooks hooks;
    private Annotations annotations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hooks {
        private String postInstall;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Annotations {
        private String marketplace;
    }
}