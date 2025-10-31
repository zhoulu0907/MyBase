package com.cmsr.onebase.module.bpm.api.dto.node.base;

import com.cmsr.onebase.module.bpm.api.dto.node.GlobalConfigExtDTO;
import lombok.Data;
/**
 * 全局设置
 *
 * @author liyang
 * @date 2025/10/24
 */
@Data
public class GlobalConfigDTO {
    /**
     * 是否使用全局配置
     */
    private Boolean useGlobalConfig;
    /**
     * 全局配置
     */
    private GlobalConfigExtDTO globalConfig;
}
