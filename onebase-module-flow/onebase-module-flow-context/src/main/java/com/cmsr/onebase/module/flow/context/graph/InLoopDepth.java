package com.cmsr.onebase.module.flow.context.graph;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/28 17:52
 */
public class InLoopDepth extends HashMap<String, Integer> {

    public static final InLoopDepth EMPTY_LOOP_DEPTH = new InLoopDepth();

    public InLoopDepth() {
    }

    public InLoopDepth(InLoopDepth loopDeepMap) {
        super(loopDeepMap);
    }

    /**
     * 输入表达式 loop_a82a3f9df4a6432a844155a59d6a29ac.46999569445519360
     * 里面存在的Key loop_a82a3f9df4a6432a844155a59d6a29ac
     * 里面存在的Value 1
     *
     * @param exp
     * @return
     */
    public int getLoopDepthValue(String exp) {
        for (String key : this.keySet()) {
            if (StringUtils.equals(exp, key)
                    || StringUtils.startsWith(exp, key + ".")) {
                return this.get(key);
            }
        }
        return -1;
    }
}
