package com.cmsr.onebase.framework.common.anyline.web;

import lombok.Data;

import java.util.List;

/**
 * @ClassName PageResponse
 * @Description 分页结果对象，包含分页数据和元信息
 * @Author mickey
 * @Date 2025/7/7 16:04
 */


/**
 * 分页结果包装类
 *
 * @param <T> 实体类型
 */
@Data
public class PageResult<T> {
    private final List<T> content;
    private final int pageIndex;
    private final int pageSize;
    private final long total;


    public PageResult(List<T> content, int pageIndex, int pageSize, long total) {
        this.content = content;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) total / pageSize);
    }

    public boolean hasNext() {
        return pageIndex < getTotalPages();
    }

    public boolean hasPrevious() {
        return pageIndex > 1;
    }
}