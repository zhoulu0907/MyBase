package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.warm.flow.core.enums.PublishStatus;

/**
 * 流程版本状态枚举
 *
 * @author liyang
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum VersionStatusEnum {

    /**
     * 编辑
     */
    PUBLISHED("published", "已发布"),

    /**
     * 设计中
     */
    DESIGNING("designing", "设计中"),

    /**
     * 历史
     */
    PREVIOUS("previous", "历史");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static VersionStatusEnum getByCode(String code) {
        for (VersionStatusEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static VersionStatusEnum toVersionStatusEnum(Integer code) {
        if (code == null) {
            return null;
        }

        if (code.equals(PublishStatus.PUBLISHED.getKey())) {
            return VersionStatusEnum.PUBLISHED;
        } else if (code.equals(PublishStatus.UNPUBLISHED.getKey())) {
            return VersionStatusEnum.DESIGNING;
        } else if (code.equals(PublishStatus.EXPIRED.getKey())) {
            return VersionStatusEnum.PREVIOUS;
        }

        return null;
    }

    public PublishStatus toPublishStatus() {
        switch (this) {
            case PUBLISHED:
                return PublishStatus.PUBLISHED;
            case DESIGNING:
                return PublishStatus.UNPUBLISHED;
            case PREVIOUS:
                return PublishStatus.EXPIRED;
            default:
                return null;
        }
    }
}
