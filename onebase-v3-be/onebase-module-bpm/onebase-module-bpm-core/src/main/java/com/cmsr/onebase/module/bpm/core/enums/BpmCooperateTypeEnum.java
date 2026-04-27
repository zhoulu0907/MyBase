/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  基于warmflow的CooperateType枚举扩展
 *
 * @author liyang
 * @date 2025-11-13
 */
@Getter
@AllArgsConstructor
public enum BpmCooperateTypeEnum {

    /**
     * 默认
     */
    APPROVAL(1, "无"),

    /**
     * 转办
     */
    TRANSFER(2, "转办"),

    /**
     * 委派
     */
    DEPUTE(3, "委派"),

    /**
     * 会签
     */
    COUNTERSIGN(4, "会签"),

    /**
     * 票签
     */
    VOTE(5, "票签"),

    /**
     * 加签
     */
    ADD_SIGNATURE(6, "加签"),

    /**
     * 减签
     */
    REDUCTION_SIGNATURE(7, "减签"),

    // =================== 自定义枚举 =====================

    /**
     *  代理
     */
    AGENT(100, "代理"),
    ;

    /**
     * 节点编码
     */
    private final Integer code;

    /**
     * 节点名称
     */
    private final String name;

    /**
     * 根据编码获取协作类型
     *
     * @param code 协作类型编码
     * @return BpmCooperateTypeEnum
     */
    public static BpmCooperateTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (BpmCooperateTypeEnum cooperateType : values()) {
            if (cooperateType.getCode().equals(code)) {
                return cooperateType;
            }
        }

        return null;
    }
}
