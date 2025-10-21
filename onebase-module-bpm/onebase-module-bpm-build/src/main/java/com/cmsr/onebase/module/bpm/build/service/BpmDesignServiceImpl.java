package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import com.cmsr.onebase.module.bpm.convert.BpmDesignConvert;
import com.cmsr.onebase.module.bpm.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.utils.ExceptionUtil;
import org.springframework.stereotype.Service;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程设计服务实现类
 *
 * @author liyang
 * @date 2025-10-20
 */
@Service
@Slf4j
public class BpmDesignServiceImpl implements BpmDesignService {

    @Resource
    private DefService defService;

    @Resource
    private BpmDesignConvert bpmDesignConvert;

    @Override
    public Long save(BpmDesignVO flowDesignVO) {
        // 默认为流程版本 + 版本号拼接
        if (flowDesignVO.getVersionAlias() == null) {
            flowDesignVO.setVersionAlias("流程版本" + flowDesignVO.getVersion());
        }

        DefJson defJson = bpmDesignConvert.toDefJson(flowDesignVO);

        try {
            defService.saveDef(defJson, true);
        } catch (Exception e) {
            log.error("保存流程失败：{}", ExceptionUtil.getExceptionMessage(e));
            throw exception(ErrorCodeConstants.SAVE_FLOW_FAILED);
        }

        return defJson.getId();
    }

    @Override
    public BpmDesignVO queryById(Long id) {
        // 流程不存在时，直接查询defJson结构会报错，先查Definition表
        Definition definition = defService.getById(id);

        if (definition == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_EXISTS);
        }

        DefJson defJson;

        try {
            // 获取defJson结构
            defJson = defService.queryDesign(id);
        } catch (Exception e) {
            log.error("查询流程失败：{}", ExceptionUtil.getExceptionMessage(e));
            throw exception(ErrorCodeConstants.QUERY_FLOW_FAILED);
        }

        return bpmDesignConvert.toFlowDesignVO(defJson);
    }

    @Override
    public void delete(BpmDeleteReqVo reqVo) {
        defService.removeById(reqVo.getId());
    }
}
