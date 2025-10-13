
package com.cmsr.onebase.module.bpm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 节点类型
 *
 */
@AllArgsConstructor
@Getter
public enum NodeType {
    /**
     * 开始节点
     */
    START(0, "start"),
    /**
     * 中间节点
     */
    BETWEEN(1, "between"),
    /**
     * 结束节点
     */
    END(2, "end"),

    /**
     * 互斥网关
     */
    SERIAL(3, "serial"),

    /**
     * 并行网关
     */
    PARALLEL(4, "parallel");

    private final Integer key;
    private final String value;

    public static Integer getKeyByValue(String value) {
        for (NodeType item : NodeType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer Key) {
        for (NodeType item : NodeType.values()) {
            if (item.getKey().equals(Key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static NodeType getByKey(Integer key) {
        for (NodeType item : NodeType.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断是否开始节点
     *
     * @param Key
     * @return
     */
    public static Boolean isStart(Integer Key) {
        return !Objects.isNull(Key) && (NodeType.START.getKey().equals(Key));
    }

    /**
     * 判断是否中间节点
     *
     * @param Key
     * @return
     */
    public static Boolean isBetween(Integer Key) {
        return !Objects.isNull(Key) && (NodeType.BETWEEN.getKey().equals(Key));
    }

    /**
     * 判断是否结束节点
     *
     * @param Key
     * @return
     */
    public static Boolean isEnd(Integer Key) {
        return !Objects.isNull(Key) && (NodeType.END.getKey().equals(Key));
    }

    /**
     * 判断是否网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWay(Integer Key) {
        return !Objects.isNull(Key) && (NodeType.SERIAL.getKey().equals(Key)
            || NodeType.PARALLEL.getKey().equals(Key));
    }

    /**
     * 判断是否互斥网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWaySerial(Integer Key) {
        return !Objects.isNull(Key) && NodeType.SERIAL.getKey().equals(Key);
    }

    /**
     * 判断是否并行网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWayParallel(Integer Key) {
        return !Objects.isNull(Key) && NodeType.PARALLEL.getKey().equals(Key);
    }

}
