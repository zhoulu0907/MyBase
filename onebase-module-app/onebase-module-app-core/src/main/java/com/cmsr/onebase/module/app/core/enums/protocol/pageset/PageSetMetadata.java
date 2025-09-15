package com.cmsr.onebase.module.app.core.enums.protocol.pageset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PageSetMetadata
 * @Description 页面级核心数据定义
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSetMetadata {

    /**
     * 主元数据
     */
    private String mainMetadata;

    /**
     * 元数据列表
     */
    private List<String> metadatas;
}
