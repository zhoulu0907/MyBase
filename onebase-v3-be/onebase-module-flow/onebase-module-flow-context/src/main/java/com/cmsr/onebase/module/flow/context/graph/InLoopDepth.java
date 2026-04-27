package com.cmsr.onebase.module.flow.context.graph;

import java.io.Serializable;
import java.util.HashMap;

/**
 * https://liteflow.cc/pages/5f971f/#%E7%94%A8%E6%B3%95
 * 多层嵌套循环中获取下标
 * <p>
 * Loop是对列表进行迭代处理，因此，Loop组件指定了循环的对象（迭代处理的对象）
 * 根据LiteFlow的文档，“多层嵌套循环中获取下标”，要获取当前组件相对Loop组件的“深度”，通过这个值，在运行过程中可获取当前执行的下标（第几次循环）
 * <p>
 * 每个节点都有一个 InLoopDepth 对象。
 * Key是循环节点的Id Key。
 * Value是当前组件相对Loop组件的“深度”。
 * <p>
 * 处理的代码在：
 * com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder#addJsonGraphLoopVariable(com.cmsr.onebase.module.flow.context.graph.JsonGraph)
 *
 * @Author：huangjie
 * @Date：2025/9/28 17:52
 */
public class InLoopDepth extends HashMap<String, Integer> implements Serializable {

    public static final InLoopDepth EMPTY_LOOP_DEPTH = new InLoopDepth();

    public InLoopDepth() {
    }

    public InLoopDepth(InLoopDepth loopDeepMap) {
        super(loopDeepMap);
    }

//    /**
//     * 输入参数： loop_a82a3f9df4a6432a844155a59d6a29ac.46999569445519360
//     *
//     * 截取第一个部分，判断是不是循环节点的变量（迭代要处理的变量），如果是则返回深度值，否则返回-1
//     * 任何一个Loop里面的节点，在使用Loop节点指定的循环变量（循环对象），要通过这个值，去获取当前迭代的下标（第几次）
//     * @param exp
//     * @return
//     */
//    public int getLoopDepthValue(String exp) {
//        for (String key : this.keySet()) {
//            if (StringUtils.equals(exp, key)
//                    || StringUtils.startsWith(exp, key + ".")) {
//                return this.get(key);
//            }
//        }
//        return -1;
//    }
}
