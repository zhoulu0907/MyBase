package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 看板卡片比较模式。
 */
@Getter
@AllArgsConstructor
public enum MetadataCardCompareModeEnum {

    YOY("yoy"),
    MOM("mom");

    private final String code;
}
