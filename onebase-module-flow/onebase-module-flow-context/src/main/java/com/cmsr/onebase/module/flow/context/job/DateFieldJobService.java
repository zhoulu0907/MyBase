package com.cmsr.onebase.module.flow.context.job;

/**
 * @Author：huangjie
 * @Date：2025/10/15 9:57
 */
public interface DateFieldJobService {

    String KEY_PREFIX_FLD = "flow:job:service:dlf";

    String call(Long processId);

}
