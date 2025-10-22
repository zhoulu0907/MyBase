package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.FlowInstanceDO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTodoRuntimeService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
public class BpmFlowTodoRuntimeServiceImpl implements BpmFlowTodoRuntimeService {
    @Resource
    FlowInstanceRepository flowInstanceRepository;

    @Resource
    FlowHisTaskRepository flowHisTaskRepository;
    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmFlowTodoTaskPageReqVO pageReqVO) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        PageResult<FlowInstance> pageResult = flowInstanceRepository.getTodoTaskPage(pageReqVO, userId);
        List<BpmFlowTodoTaskVO> todoTaskList = new ArrayList<>();

        for (FlowInstance flowInstance : pageResult.getList()) {
            try {
                // 创建BpmFlowTodoTaskVO实例
                BpmFlowTodoTaskVO todoTaskVO = new BpmFlowTodoTaskVO();

                // 复制BpmFlowInstanceVO的属性
                BeanUtils.copyProperties(flowInstance,todoTaskVO);
                // 解析ext字段中的JSON数据
                if (StringUtils.isNotBlank(flowInstance.getExt())) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode extNode = objectMapper.readTree(flowInstance.getExt());
                    // 提取processInfo对象
                    if (extNode.has("processInfo")) {
                        JsonNode processInfoNode = extNode.get("processInfo");
                        // 设置BpmFlowTodoTaskVO特有字段
                        if (processInfoNode.has("processTitle")) {
                            todoTaskVO.setProcessTitle(processInfoNode.get("processTitle").asText());
                        }
                        if (processInfoNode.has("initiator")) {
                            todoTaskVO.setInitiator(processInfoNode.get("initiator").asText());
                        }
                        if (processInfoNode.has("formSummary")) {
                            todoTaskVO.setFormSummary(processInfoNode.get("formSummary").asText());
                        }
                        // 时间字段转换
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                        if (processInfoNode.has("submitTime")) {
                            String submitTimeStr = processInfoNode.get("submitTime").asText();
                            if (StringUtils.isNotBlank(submitTimeStr)) {
                                todoTaskVO.setStartTime(LocalDateTime.parse(submitTimeStr, formatter));
                            }
                        }

                        if (processInfoNode.has("createTime")) {
                            String createTimeStr = processInfoNode.get("createTime").asText();
                            if (StringUtils.isNotBlank(createTimeStr)) {
                                todoTaskVO.setArrivalTime(LocalDateTime.parse(createTimeStr, formatter));
                            }
                        }
                    }
                }
                todoTaskList.add(todoTaskVO);
            } catch (Exception e) {
                // 记录日志并跳过异常项
                throw new RuntimeException(e);
            }
        }
        // 返回新的PageResult
        return new PageResult<>(todoTaskList, pageResult.getTotal());
    }
    /**
     * 获取流程已办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowDoneTaskVO> getTodoPage(BpmFlowDoneTaskPageReqVO pageReqVO) {
        PageResult<FlowHisTask> pageResult = flowHisTaskRepository.getProcessedTaskPage(pageReqVO, WebFrameworkUtils.getLoginUserId());
        List<BpmFlowDoneTaskVO> doneTaskList = new ArrayList<>();
        for (FlowHisTask flowHisTask : pageResult.getList()){
            BpmFlowDoneTaskVO doneTaskVO = new BpmFlowDoneTaskVO();
            if (StringUtils.isNotBlank(flowHisTask.getExt())){
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode extNode = objectMapper.readTree(flowHisTask.getExt());
                    if (extNode.has("processInfo")){
                        JsonNode processInfoNode = extNode.get("processInfo");
                        if (processInfoNode.has("processTitle")) {
                            doneTaskVO.setProcessTitle(processInfoNode.get("processTitle").asText());
                        }
                        if (processInfoNode.has("initiator")) {
                            doneTaskVO.setInitiator(processInfoNode.get("initiator").asText());
                        }
                        if (processInfoNode.has("formSummary")) {
                            doneTaskVO.setFormSummary(processInfoNode.get("formSummary").asText());
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                doneTaskVO.setId(flowHisTask.getId());
                doneTaskVO.setNodeName(flowHisTask.getNodeName());
                doneTaskVO.setMessage(flowHisTask.getMessage());
                doneTaskVO.setHandleTime(flowHisTask.getUpdateTime());
                doneTaskVO.setHandleOperation(flowHisTask.getSkipType());
            }
            doneTaskList.add(doneTaskVO);
        }
        return new PageResult<>(doneTaskList, pageResult.getTotal());
    }
    /**
     * 获取我创建的流程分页
     *
     * @param pageReqVO
     * @return
     */
    public PageResult<BpmMyCreatedVO> getMyCreatedPage(BpmMyCreatedPageReqVO pageReqVO) {
        PageResult<FlowInstanceDO> pageResult = flowInstanceRepository.getMyCreatedPage(pageReqVO, WebFrameworkUtils.getLoginUserId());
        List<BpmMyCreatedVO>   list  = new ArrayList<>();
        for (FlowInstanceDO flowInstanceDO : pageResult.getList()){
            BpmMyCreatedVO bpmMyCreatedVO = new BpmMyCreatedVO();
            // 复制BpmFlowInstanceVO的属性
            BeanUtils.copyProperties(flowInstanceDO,bpmMyCreatedVO);
            if (StringUtils.isNotBlank(flowInstanceDO.getExt())){
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode extNode = objectMapper.readTree(flowInstanceDO.getExt());
                    if (extNode.has("processInfo")){
                        JsonNode processInfoNode = extNode.get("processInfo");
                        if (processInfoNode.has("processTitle")) {
                            bpmMyCreatedVO.setProcessTitle(processInfoNode.get("processTitle").asText());
                        }

                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
        }
            bpmMyCreatedVO.setCurrentNodeHandler(flowInstanceDO.getCurrentNodeHandler());
            list.add(bpmMyCreatedVO);

        }
        return new PageResult<>(list, pageResult.getTotal());
    }
}
