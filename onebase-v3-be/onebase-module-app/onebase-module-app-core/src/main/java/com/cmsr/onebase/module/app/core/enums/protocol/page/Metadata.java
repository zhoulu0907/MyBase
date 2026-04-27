package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName Metadata
 * @Description 页面数据模型，包含主元数据和元数据列表
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {

    /**
     * 主元数据
     */
    private String mainMetadata;

    /**
     * 元数据列表
     */
    private List<String> metadatas;
}
