package com.cmsr.onebase.module.bpm.core.dto.node;

import lombok.Data;

import java.util.List;

/**
 * 节点权限标签 替代warm-flow原有的格式user:1@@user:2格式
 *
 * @author liyang
 * @data 2025-10-24
 */
@Data
public class NodePermFlagDTO {
    /**
     * 用户ID列表
     */
   private List<Long> userIds;

   /**
   * 角色ID列表
   */
   private List<Long> roleIds;
}
