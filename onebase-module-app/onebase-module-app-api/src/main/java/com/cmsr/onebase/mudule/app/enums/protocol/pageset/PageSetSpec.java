package com.cmsr.onebase.mudule.app.enums.protocol.pageset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;



/**
 * @ClassName PageSetSpec
 * @Description 页面集合规格定义，包含页面级核心数据和页面引用配置
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSetSpec {

    /**
     * 页面级核心数据
     */
    private PageSetMetadata metadata;

    /**
     * 页面引用配置
     */
    @NotNull(message = "页面引用配置不能为空")
    private PageRefs pageRefs;
}
