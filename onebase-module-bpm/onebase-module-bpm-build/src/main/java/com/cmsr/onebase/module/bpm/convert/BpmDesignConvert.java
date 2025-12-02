package com.cmsr.onebase.module.bpm.convert;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignRespVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignSaveReqVO;
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
    BpmDesignRespVO toDesignRespVO(DefJson defJson);

    /**
     * 将流程设计视图对象转换为流程定义JSON
     *
     * @param bpmDesignVO 流程设计视图对象
     * @return 流程定义JSON
     */
    DefJson toDefJson(BpmDesignSaveReqVO bpmDesignVO);

    /**
     * 复制通用字段数据，如version
     *
     * @param destDefJson 目标流程定义JSON
     * @param sourceDefJson 源流程定义JSON
     *
     * @return 流程定义JSON
     */
    void copyCommonField(DefJson destDefJson, DefJson sourceDefJson);
}
