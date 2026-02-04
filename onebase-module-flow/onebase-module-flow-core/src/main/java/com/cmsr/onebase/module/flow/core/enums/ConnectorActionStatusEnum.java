package com.cmsr.onebase.module.flow.core.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 连接器动作状态枚举
 * <p>
 * 状态值定义：
 * <ul>
 *   <li>1 - 已发布 (published)</li>
 *   <li>2 - 已下架 (offline)</li>
 * </ul>
 *
 * @author onebase
 * @since 2026-02-04
 */
@Getter
public enum ConnectorActionStatusEnum {

    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),

    /**
     * 已下架
     */
    OFFLINE(2, "已下架");

    private final Integer code;
    private final String desc;

    ConnectorActionStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举实例，不存在时返回 null
     */
    public static ConnectorActionStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ConnectorActionStatusEnum status : values()) {
            if (Objects.equals(status.code, code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据字符串状态码获取枚举
     *
     * @param codeStr 字符串状态码
     * @return 枚举实例，不存在时返回 null
     */
    public static ConnectorActionStatusEnum fromString(String codeStr) {
        if (codeStr == null) {
            return null;
        }
        try {
            Integer code = Integer.parseInt(codeStr);
            return fromCode(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 判断是否为已发布状态
     *
     * @param code 状态码
     * @return true=已发布
     */
    public static boolean isPublished(Integer code) {
        return Objects.equals(PUBLISHED.code, code);
    }

    /**
     * 判断是否为已下架状态
     *
     * @param code 状态码
     * @return true=已下架
     */
    public static boolean isOffline(Integer code) {
        return Objects.equals(OFFLINE.code, code);
    }

    /**
     * 判断字符串状态码是否为已发布
     *
     * @param codeStr 字符串状态码
     * @return true=已发布
     */
    public static boolean isPublished(String codeStr) {
        ConnectorActionStatusEnum status = fromString(codeStr);
        return status == PUBLISHED;
    }

    /**
     * 判断字符串状态码是否为已下架
     *
     * @param codeStr 字符串状态码
     * @return true=已下架
     */
    public static boolean isOffline(String codeStr) {
        ConnectorActionStatusEnum status = fromString(codeStr);
        return status == OFFLINE;
    }

    /**
     * 获取状态码的字符串形式
     *
     * @return 状态码字符串
     */
    public String getCodeAsString() {
        return String.valueOf(code);
    }
}