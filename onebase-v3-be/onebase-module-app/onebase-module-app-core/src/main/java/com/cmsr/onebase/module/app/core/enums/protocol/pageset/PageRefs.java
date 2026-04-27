package com.cmsr.onebase.module.app.core.enums.protocol.pageset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PageRefs
 * @Description 页面引用配置
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRefs {

    /**
     * 列表页面配置
     */
    private List<PageRef> listPages;

    /**
     * 详情页面配置
     */
    private List<PageRef> detailPages;

    /**
     * 编辑页面配置
     */
    private List<PageRef> editPages;
}
