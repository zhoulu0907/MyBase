package com.cmsr.onebase.mudule.appresource.enums.protocol.pageset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
    @NotNull(message = "主元数据数据不能为空")
    private String mainMetadata;

    /**
     * 元数据列表
     */
    private List<String> metadatas;
}
