package com.cmsr.onebase.module.flow.core.flow;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 *
 * 节点分一般和可恢复两者。
 *
 *
 * 恢复执行算法：
 *
 * context里面包含下面两个变量
 * previousNodeTag     恢复执行节点ID
 * ----------------------------------------------------------------------------
 * 初始执行逻辑：
 * previousNodeTag = null
 * 一般节点 判断 previousNodeTag 为 null。正常执行该节点（isAccess返回true）
 * 可恢复节点 方法判断 previousNodeTag 为 null，isAccess返回true，正常执行该节点。（isAccess返回true，afterProcess。1执行该节点，2缓存执行状态，3setIsEnd为 true，4返回结果）
 *
 *
 *
 * ----------------------------------------------------------------------------
 * 恢复执行逻辑：
 * previousNodeTag = 恢复执行节点ID
 *
 * 恢复节点前的节点：
 * 节点 判断 previousNodeTag不为 null 且 previousNodeTag 为 不是自己的ID。跳过该节点执行（isAccess 返回false）。
 *
 * 恢复节点：
 * 恢复节点 方法判断 previousNodeTag 为 自己的ID，isAccess返回false。跳过执行且重置状态。（在isAccess执行该节点，previousNodeTag = null）
 *
 * 恢复节点后的节点：
 * 同“初始执行逻辑”
 *
 *
 * 总结：
 * 一般节点分：正常执行，跳过执行。
 * 可恢复节点分：正常执行，跳过执行，跳过且重置状态。
 *
 * @Author：huangjie
 * @Date：2025/9/5 16:12
 */
@Data
public class ExecuteContext {

    // 上次执行结束节点
    private Optional<String> previousNodeTag;

    // 本次执行结束节点
    private Optional<String> currentEndNodeTag;

    public boolean equalsPreviousNodeTag(String tag) {
        return this.getPreviousNodeTag().filter(t -> StringUtils.equals(tag, t)).isPresent();
    }

    public void restPreviousNodeTag() {
        this.previousNodeTag = Optional.empty();
    }

}
