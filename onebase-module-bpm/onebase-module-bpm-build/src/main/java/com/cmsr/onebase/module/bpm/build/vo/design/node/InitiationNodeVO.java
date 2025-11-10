package com.cmsr.onebase.module.bpm.build.vo.design.node;

import com.cmsr.onebase.module.bpm.core.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeDataVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
import lombok.Data;

/**
 * 发起节点配置
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class InitiationNodeVO extends BaseNodeVO {
    /**
     * 节点自定义配置
     */
    private DataVO data;


    /**
     * 发起节点视图
     *
     * @author liyang
     * @date 2025-10-23
     */
    @Data
    public static class DataVO extends BaseNodeDataVO {
        /**
         * 部门配置
         */
        private InitiationNodeExtDTO.DeptConfigDTO deptConfig;
    }
}
