package com.cmsr.onebase.module.metadata.core.enums;

public enum ClientTypeEnum {
    /**
     * runtime，build
     */
    RUNTIME("运行时"),
    BUILD("构建时");
    private final String description;
    ClientTypeEnum(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }


}
