package com.cmsr.onebase.framework.orm;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;

/**
 * @Author：huangjie
 * @Date：2025/12/22 10:44
 */
public class PageUtils {

    public static <T> com.mybatisflex.core.paginate.Page<T> toFlexPage(PageParam pageParam) {
        return new com.mybatisflex.core.paginate.Page<>(pageParam.getPageNo(), pageParam.getPageSize());
    }

    public static <T> PageResult<T> toPageResult(com.mybatisflex.core.paginate.Page<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }
}
