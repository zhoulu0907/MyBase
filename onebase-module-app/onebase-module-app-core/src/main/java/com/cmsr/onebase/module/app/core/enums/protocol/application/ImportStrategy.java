package com.cmsr.onebase.module.app.core.enums.protocol.application;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportStrategy {
    private String conflict;

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class OverwriteAllowedType {
        private String type;
    }

    private List<OverwriteAllowedType> overwriteAllowed;
}
