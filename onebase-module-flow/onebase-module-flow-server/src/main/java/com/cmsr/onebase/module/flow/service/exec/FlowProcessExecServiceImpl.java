package com.cmsr.onebase.module.flow.service.exec;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.controller.app.exec.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.controller.app.exec.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.controller.app.exec.vo.QueryFormTriggerRespVO;
import com.cmsr.onebase.module.flow.dal.database.FlowProcessTriggerFormRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerFormDO;
import com.cmsr.onebase.module.flow.flow.FlowFilterExecutor;
import com.cmsr.onebase.module.flow.utils.FlowUtils;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:38
 */
@Setter
@Service
public class FlowProcessExecServiceImpl implements FlowProcessExecService {

    @Autowired
    private FlowProcessTriggerFormRepository flowProcessTriggerFormRepository;

    @Autowired
    private FlowExecutor flowExecutor;

    @Autowired
    private FlowFilterExecutor flowFilterExecutor;

    @Override
    public List<QueryFormTriggerRespVO> queryFormTrigger(Long pageId) {
        // 查询符合条件的表单触发配置
        List<FlowProcessTriggerFormDO> formTriggerList = flowProcessTriggerFormRepository.findByPageId(pageId);
        return BeanUtils.toBean(formTriggerList, QueryFormTriggerRespVO.class);
    }


    @Override
    public FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO) {
        //TODO 输入参数要转换为实际对象
        Map<String, Object> inputMap = null;//BeanUtils.toMap(reqVO.getInputParams());
        if (!flowFilterExecutor.filter(reqVO.getProcessId(), inputMap)) {
            return null;
        }
        String chainId = FlowUtils.toFlowChainId(reqVO.getProcessId());
        DefaultContext defaultContext = new DefaultContext();
        defaultContext.setData(FlowUtils.INPUT, inputMap);
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, "", defaultContext);
        DefaultContext resultContext = response.getContextBean(DefaultContext.class);
        return BeanUtils.toBean(resultContext.getDataMap(), FormTriggerRespVO.class);
    }
}
