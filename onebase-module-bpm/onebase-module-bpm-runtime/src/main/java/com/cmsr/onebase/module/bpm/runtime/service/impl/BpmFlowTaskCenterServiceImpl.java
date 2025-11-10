package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmHisTaskExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmInstanceExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmTaskExtRepository;
import com.cmsr.onebase.module.bpm.core.dto.BpmDoneTaskDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmInstanceDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmTodoTaskDTO;
import com.cmsr.onebase.module.bpm.core.vo.*;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTaskCenterService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ListNodesRespVO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
@Slf4j
public class BpmFlowTaskCenterServiceImpl implements BpmFlowTaskCenterService {


    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    @Resource
    private FlowHisTaskRepository flowHisTaskRepository;

    @Resource
    private FlowTaskRepository flowTaskRepository;

    @Resource
    private BpmTaskExtRepository taskExtRepository;

    @Resource
    private BpmHisTaskExtRepository hisTaskExtRepository;

    @Resource
    private BpmInstanceExtRepository insExtRepository;

    @Resource
    private UserService flowUserservice;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private InsService insService;

    @Resource
    private BpmFlowInsBizExtRepository insBizExtRepository;

    @Resource
    private AdminUserApi adminUserApi;

    private List<String> splitToList(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }

        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private List<String> splitToFlowStatusList(String str) {
        List<String> flowStatusList = splitToList(str);

        if (CollectionUtils.isEmpty(flowStatusList)) {
            return flowStatusList;
        }

        return flowStatusList.stream()
                .filter(s -> {
                    if (BpmBusinessStatusEnum.getByCode(s) == null) {
                        log.warn("忽略不支持的流程状态：{}", s);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private void fillPageNavi(ConfigStore condition, Integer pageNo, Integer pageSize) {
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(pageNo);
        navi.setPageRows(pageSize);
        condition.setPageNavi(navi);
    }

    private void fillInsCondition(ConfigStore condition, BpmInsExtQueryPageVO queryVO) {
        condition.and(Compare.EQUAL, "app_id", queryVO.getAppId());

        // 动态添加其他查询条件
        if (StringUtils.isNotBlank(queryVO.getKeyword())) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "bpm_title", queryVO.getKeyword());
            orCondition.or(Compare.LIKE, "initiator_name", queryVO.getKeyword());
            orCondition.or(Compare.LIKE, "form_summary", queryVO.getKeyword());
            condition.and(orCondition);
        }

        if (StringUtils.isNotBlank(queryVO.getBusinessId())) {
            condition.and(Compare.EQUAL, "binding_view_id", queryVO.getBusinessId());
        }

        // 流程状态条件（支持多个值）
        List<String> flowStatusList = queryVO.getFlowStatusList();
        if (CollectionUtils.isNotEmpty(flowStatusList)) {
            if (flowStatusList.size() == 1) {
                condition.and(Compare.EQUAL, "flow_status", flowStatusList.get(0));
            } else {
                condition.and(Compare.IN, "flow_status", flowStatusList);
            }
        }

        // 节点编码条件（支持多个值）
        List<String> nodeCodeList = queryVO.getNodeCodeList();
        if (CollectionUtils.isNotEmpty(nodeCodeList)) {
            if (nodeCodeList.size() == 1) {
                condition.and(Compare.EQUAL, "node_code", nodeCodeList.get(0));
            } else {
                condition.and(Compare.IN, "node_code", nodeCodeList);
            }
        }
    }

    private void fillTimeRange(ConfigStore condition, String fieldName, LocalDateTime start, LocalDateTime end) {
        if (start != null) {
            condition.and(Compare.GREAT_EQUAL, fieldName, start);
        }

        if (end != null) {
            condition.and(Compare.LESS_EQUAL, fieldName, end);
        }
    }

    private void fillOrder(ConfigStore condition, String filedName, String orderType) {
        if("asc".equals(orderType)){
            condition.order(filedName + " asc");
        } else{
            condition.order(filedName + " desc");
        }
    }

    private ConfigStore buildDynamicCondition(BpmTodoTaskPageReqVO reqVO, String userId) {
        DefaultConfigStore condition = new DefaultConfigStore();

        // 设置分页参数
        fillPageNavi(condition, reqVO.getPageNo(), reqVO.getPageSize());

        // 填充流程实例条件
        fillInsCondition(condition, reqVO);

        // 填充时间范围条件
        fillTimeRange(condition, "submit_time", reqVO.getSubmitTimeStart(), reqVO.getSubmitTimeEnd());

        // 填充处理人条件
        condition.and(Compare.EQUAL, "processed_by", userId);

        // 排序
        fillOrder(condition, "create_time", reqVO.getSortType());

        return condition;
    }

    private ConfigStore buildDynamicCondition(BpmDoneTaskPageReqVO reqVO, String userId) {
        DefaultConfigStore condition = new DefaultConfigStore();

        // 设置分页参数
        fillPageNavi(condition, reqVO.getPageNo(), reqVO.getPageSize());

        // 填充流程实例条件
        fillInsCondition(condition, reqVO);

        // 填充时间范围条件
        fillTimeRange(condition, "submit_time", reqVO.getSubmitTimeStart(), reqVO.getSubmitTimeEnd());

        // 填充处理人条件
        condition.and(Compare.EQUAL, "approver", userId);

        // 排序
        fillOrder(condition, "update_time", reqVO.getSortType());

        return condition;
    }

    private ConfigStore buildDynamicCondition(BpmMyCreatedPageReqVO reqVO, String userId) {
        DefaultConfigStore condition = new DefaultConfigStore();

        // 设置分页参数
        fillPageNavi(condition, reqVO.getPageNo(), reqVO.getPageSize());

        // 填充流程实例条件
        fillInsCondition(condition, reqVO);

        // 填充时间范围条件
        fillTimeRange(condition, "create_time", reqVO.getCreateTimeStart(), reqVO.getCreateTimeEnd());

        // 填充处理人条件
        condition.and(Compare.EQUAL, "creator", Long.valueOf(userId));

        // 排序
        fillOrder(condition, "create_time", reqVO.getSortType());

        return condition;
    }


    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmTodoTaskPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        // 处理节点编码参数
        pageReqVO.setNodeCodeList(splitToList(pageReqVO.getNodeCode()));

        // 处理流程状态参数
        pageReqVO.setFlowStatusList(splitToFlowStatusList(pageReqVO.getFlowStatus()));

        // 构建查询条件
        ConfigStore condition = buildDynamicCondition(pageReqVO, String.valueOf(loginUserId));

        PageResult<BpmTodoTaskDTO> pageResult = taskExtRepository.getTodoTaskPage(condition);
        List<BpmFlowTodoTaskVO> todoTaskList = new ArrayList<>();

        for (BpmTodoTaskDTO flowTaskExt : pageResult.getList()) {
            try {
                BpmFlowTodoTaskVO todoTaskVO = getBpmFlowTodoTaskVO(flowTaskExt);
                todoTaskList.add(todoTaskVO);
            } catch (Exception e) {
                // 记录日志并跳过异常项
                throw new RuntimeException(e);
            }
        }
        // 返回新的PageResult
        return new PageResult<>(todoTaskList, pageResult.getTotal());
    }

    @NotNull
    private static BpmFlowTodoTaskVO getBpmFlowTodoTaskVO(BpmTodoTaskDTO flowTaskExt) {
        BpmFlowTodoTaskVO todoTaskVO = new BpmFlowTodoTaskVO();
        todoTaskVO.setTaskId(flowTaskExt.getId());
        todoTaskVO.setInstanceId(flowTaskExt.getInstanceId());
        todoTaskVO.setFlowStatus(flowTaskExt.getFlowStatus());
        todoTaskVO.setTaskId(flowTaskExt.getId());
        todoTaskVO.setNodeCode(flowTaskExt.getNodeCode());
        todoTaskVO.setProcessTitle(flowTaskExt.getBpmTitle());
        todoTaskVO.setSubmitTime(flowTaskExt.getSubmitTime());
        todoTaskVO.setFormSummary(flowTaskExt.getFormSummary());
        todoTaskVO.setArrivalTime(flowTaskExt.getCreateTime());
        todoTaskVO.setBusinessId(flowTaskExt.getBindingViewId());

        todoTaskVO.setInitiator(new UserBasicInfoVO());
        todoTaskVO.getInitiator().setUserId(flowTaskExt.getInitiatorId());
        todoTaskVO.getInitiator().setName(flowTaskExt.getInitiatorName());
        todoTaskVO.getInitiator().setAvatar(flowTaskExt.getInitiatorAvatar());

        return todoTaskVO;
    }

    /**
     * 获取流程已办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowDoneTaskVO> getDonePage(BpmDoneTaskPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 处理节点编码参数
        pageReqVO.setNodeCodeList(splitToList(pageReqVO.getNodeCode()));

        // 处理流程状态参数
        pageReqVO.setFlowStatusList(splitToFlowStatusList(pageReqVO.getFlowStatus()));

        // 构建查询条件
        ConfigStore condition = buildDynamicCondition(pageReqVO, String.valueOf(loginUserId));

        PageResult<BpmDoneTaskDTO> pageResult = hisTaskExtRepository.getDoneTaskPage(condition);

        List<BpmFlowDoneTaskVO> doneTaskList = new ArrayList<>();
        for (BpmDoneTaskDTO flowHisTaskExt : pageResult.getList()) {
            BpmFlowDoneTaskVO doneTaskVO = new BpmFlowDoneTaskVO();
            doneTaskVO.setTaskId(flowHisTaskExt.getId());
            doneTaskVO.setInstanceId(flowHisTaskExt.getInstanceId());
            doneTaskVO.setProcessTitle(flowHisTaskExt.getBpmTitle());
            doneTaskVO.setFormSummary(flowHisTaskExt.getFormSummary());
            doneTaskVO.setHandleTime(flowHisTaskExt.getUpdateTime());
            doneTaskVO.setTaskStatus(flowHisTaskExt.getTaskFlowStatus());
            doneTaskVO.setBusinessId(flowHisTaskExt.getBindingViewId());

            doneTaskVO.setInitiator(new UserBasicInfoVO());
            doneTaskVO.getInitiator().setUserId(flowHisTaskExt.getInitiatorId());
            doneTaskVO.getInitiator().setName(flowHisTaskExt.getInitiatorName());
            doneTaskVO.getInitiator().setAvatar(flowHisTaskExt.getInitiatorAvatar());

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
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 处理节点编码参数
        pageReqVO.setNodeCodeList(splitToList(pageReqVO.getNodeCode()));

        // 处理流程状态参数
        pageReqVO.setFlowStatusList(splitToFlowStatusList(pageReqVO.getFlowStatus()));

        ConfigStore condition = buildDynamicCondition(pageReqVO, String.valueOf(loginUserId));
        PageResult<BpmInstanceDTO> pageResult = insExtRepository.getMyCreatePage(condition);

        List<BpmMyCreatedVO> list = new ArrayList<>();
        for (BpmInstanceDTO flowInstance : pageResult.getList()) {
            BpmMyCreatedVO bpmMyCreatedVO = new BpmMyCreatedVO();
            bpmMyCreatedVO.setId(flowInstance.getId());
            bpmMyCreatedVO.setProcessTitle(flowInstance.getBpmTitle());
            bpmMyCreatedVO.setFlowStatus(flowInstance.getFlowStatus());
            bpmMyCreatedVO.setFormSummary(flowInstance.getFormSummary());
            bpmMyCreatedVO.setSubmitTime(flowInstance.getSubmitTime());
            bpmMyCreatedVO.setCreateTime(flowInstance.getCreateTime());
            bpmMyCreatedVO.setUpdateTime(flowInstance.getUpdateTime());
            bpmMyCreatedVO.setInstanceId(flowInstance.getId());
            bpmMyCreatedVO.setBusinessId(flowInstance.getBindingViewId());

            //设置当前节点处理人
            List<Task> flowTaskList = taskService.getByInsId(flowInstance.getId());

            if (CollectionUtils.isNotEmpty(flowTaskList)) {
                bpmMyCreatedVO.setTaskId(flowTaskList.get(0).getId());
                List<Long> taskIds = StreamUtils.toList(flowTaskList, Task::getId);
                List<User> userList = flowUserservice.getByAssociateds(taskIds);
                List<Long> processedByIds = userList.stream()
                        .map(user -> Long.valueOf(user.getProcessedBy()))
                        .collect(Collectors.toList());
                CommonResult<List<AdminUserRespDTO>> dtos = adminUserApi.getUserList(processedByIds);
                List<Map<String, Object>> currentNodeHandler = new ArrayList<>();
                dtos.getData().forEach(dto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", dto.getId());
                    map.put("userName", dto.getNickname());
                    map.put("avatar", dto.getAvatar());
                    currentNodeHandler.add( map);
                });
                bpmMyCreatedVO.setCurrentNodeHandler(currentNodeHandler);
            }
            list.add(bpmMyCreatedVO);

        }
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public List<ListNodesRespVO.NodeVO> listNodes(Long instanceId) {
        List<ListNodesRespVO.NodeVO> nodeVOs = new ArrayList<>();

        Instance instance = insService.getById(instanceId);

        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        String defJsonStr = instance.getDefJson();

        DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);
        for (NodeJson nodeJson : defJson.getNodeList()) {
            // 只取中间节点
            if (NodeType.isBetween(nodeJson.getNodeType())) {
                ListNodesRespVO.NodeVO nodeVO = new ListNodesRespVO.NodeVO();
                nodeVO.setNodeCode(nodeJson.getNodeCode());
                nodeVO.setNodeName(nodeJson.getNodeName());

                // 取实际的类型
                BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(nodeJson.getExt(), BaseNodeExtDTO.class);
                nodeVO.setNodeType(nodeExtDTO.getNodeType());

                nodeVOs.add(nodeVO);
            }
        }

        return nodeVOs;
    }
}
