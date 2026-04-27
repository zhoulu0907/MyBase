package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName PageData
 * @Description 页面数据，包含初始化数据和运行时数据
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageData {

    /**
     * 页面初始化数据
     */
    private Map<String, Object> initData;

    /**
     * 页面过程中动态数据
     */
    private Map<String, Object> runtimeData;
}
