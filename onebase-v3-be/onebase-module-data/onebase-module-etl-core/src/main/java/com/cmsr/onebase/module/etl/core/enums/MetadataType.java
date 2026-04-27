package com.cmsr.onebase.module.etl.core.enums;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import lombok.Getter;

@Getter
public enum MetadataType {
    TABLE("table", true),
    VIEW("view", false);

    private String value;
    private boolean writeable;

    MetadataType(String value, boolean writable) {
        this.value = value;
        this.writeable = writable;
    }

    public static MetadataType getType(String value) {
        for (MetadataType type : MetadataType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.ILLEGAL_METADATA_TYPE);
    }

    public static boolean writeable(String metadataType) {
        return getType(metadataType).isWriteable();
    }
}
