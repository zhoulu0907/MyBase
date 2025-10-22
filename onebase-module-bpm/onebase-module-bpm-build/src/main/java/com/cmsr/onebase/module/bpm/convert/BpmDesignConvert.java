package com.cmsr.onebase.module.bpm.convert;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import org.dromara.warm.flow.core.dto.DefJson;

/**
 * @author liyang
 * @date 2025-10-24
 */
public interface BpmDesignConvert {
    /**
     * 将流程定义JSON转换为流程设计视图对象
     *
     * @param defJson 流程定义JSON
     * @return 流程设计视图对象
     */
    BpmDesignVO toFlowDesignVO(DefJson defJson);

    /**
     * 将流程设计视图对象转换为流程定义JSON
     *
     * @param bpmDesignVO 流程设计视图对象
     * @return 流程定义JSON
     */
    DefJson toDefJson(BpmDesignVO bpmDesignVO);
}
