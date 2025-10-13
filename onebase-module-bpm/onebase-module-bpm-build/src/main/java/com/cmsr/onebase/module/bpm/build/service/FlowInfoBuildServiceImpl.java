package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.bpm.build.vo.FlowDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.FlowInfoReqVO;
import com.cmsr.onebase.module.bpm.build.vo.FlowNodeVO;
import com.cmsr.onebase.module.bpm.build.vo.FlowSkipVO;
import com.cmsr.onebase.module.bpm.enums.NodeType;
import com.cmsr.onebase.module.bpm.enums.SkipType;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowDefinitionDO;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowNodeDO;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowSkipDO;
import com.cmsr.onebase.module.formula.service.FlowDefinitionCoreService;
import com.cmsr.onebase.module.formula.service.FlowNodeCoreService;
import com.cmsr.onebase.module.formula.service.FlowSkipCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.bpm.enums.ErrorCodeConstants.*;


/**
 * 流程信息服务实现类
 */
@Service
@Slf4j
public class FlowInfoBuildServiceImpl implements FlowInfoBuildService {

    @Resource
     private FlowDefinitionCoreService flowDefinitionCoreService;
    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private FlowNodeCoreService flowNodeCoreService;

    @Resource
    private FlowSkipCoreService flowSkipCoreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveFlowInfo(FlowInfoReqVO reqVO) {
        FlowDefinitionVO definition = new FlowDefinitionVO();
        BeanUtils.copyProperties(reqVO, definition);
        Long id = definition.getId();
        // 如果是新增的流程定义
        if (Objects.isNull(id)) {
            definition.setVersion(getNewVersion(definition));
            definition.setId(uidGenerator.getUID());
        }
        // 校验流程定义合法性
        reqVO.setId(definition.getId());//todo 待完善
        checkFlowLegal(reqVO);
        FlowDefinitionDO insertDefinition = BeanUtils.toBean(definition, FlowDefinitionDO.class);

        // 如果是新增的流程定义
        if (Objects.isNull(id)) {
            flowDefinitionCoreService.save(insertDefinition);
        } else {
            //TODO 删除流程定义  及onlyNodeSkip是否保留
//            if (!onlyNodeSkip) {
//                FlowEngine.defService().updateById(definition);
//            }
//            // 删除所有节点和连线
//            FlowEngine.nodeService().remove(FlowEngine.newNode().setDefinitionId(id));
//            FlowEngine.skipService().remove(FlowEngine.newSkip().setDefinitionId(id));
            flowDefinitionCoreService.updateById(insertDefinition);
        }

        // 保存流程节点和跳转
        List<FlowNodeVO> allNodes = reqVO.getNodeList();
        // 所有的流程连线
        List<FlowSkipVO> allSkips = new ArrayList<>();
        for (FlowNodeVO node : allNodes) {
            if (node.getSkipList() != null) {
                allSkips.addAll(node.getSkipList());
            }
        }

        // 保存节点，流程连线，权利人
        List<FlowNodeDO> nodeDOs = allNodes.stream()
                .map(nodeVO -> BeanUtils.toBean(nodeVO, FlowNodeDO.class))
                .collect(Collectors.toList());
        flowNodeCoreService.saveBatch(nodeDOs);

        List<FlowSkipDO> skipDOs = allSkips.stream()
                .map(skipVO -> BeanUtils.toBean(skipVO, FlowSkipDO.class))
                .collect(Collectors.toList());
        flowSkipCoreService.saveBatch(skipDOs);
        return true;
    }
   /**
     * 根据流程定义code列表查询流程定义
     *
     * @param flowCodeList
     * @return
     */
    @Override
    public List<FlowDefinitionDO> queryByCodeList(List<String> flowCodeList) {
        List<FlowDefinitionDO> definitions =flowDefinitionCoreService.queryByCodeList(flowCodeList);
        return definitions;
    }
    /**
     * 根据流程定义id查询流程信息
     *
     * @param flowId
     * @return
     */
    @Override
    public FlowInfoReqVO queryByFlowId(Long flowId) {
        // 查询基础定义信息
        FlowDefinitionDO definition = flowDefinitionCoreService.queryById(flowId);
        if (definition == null) {
            return null;
        }

        FlowInfoReqVO reqVO = BeanUtils.toBean(definition, FlowInfoReqVO.class);
        if (reqVO == null) {
            reqVO = new FlowInfoReqVO();
        }

        // 查询节点和跳转信息
        List<FlowNodeVO> nodeVOs = BeanUtils.toBean(flowNodeCoreService.queryByDefinitionId(flowId), FlowNodeVO.class);
        List<FlowSkipVO> skipVOs = BeanUtils.toBean(flowSkipCoreService.queryByDefinitionId(flowId), FlowSkipVO.class);

        // 构建节点到跳转的映射关系
        Map<String, List<FlowSkipVO>> flowSkipMap = skipVOs.stream()
                .collect(Collectors.groupingBy(FlowSkipVO::getNowNodeCode));
        if (nodeVOs != null) {
            nodeVOs.forEach(node -> node.setSkipList(flowSkipMap.get(node.getNodeCode())));
        }

        // 设置节点列表
        if (reqVO != null && nodeVOs != null) {
            reqVO.setNodeList(nodeVOs);
        }

        return reqVO;
    }
    /**
     * 根据表单id查询流程定义
     *
     * @param formId
     * @return
     */
    @Override
    public List<FlowDefinitionVO> queryByFormId(Long formId) {
        List<FlowDefinitionDO> list =  flowDefinitionCoreService.queryByFormId(formId);
        List<FlowDefinitionVO> definitionVOs = BeanUtils.toBean(list, FlowDefinitionVO.class);
        return definitionVOs;
    }


    /**
     * 获取最新的版本号
     *
     * @param definition
     * @return
     */
    private String getNewVersion(FlowDefinitionVO definition) {
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        List<FlowDefinitionDO> definitions = queryByCodeList(flowCodeList);
        int highestVersion = 0;
        String latestNonPositiveVersion = null;
        long latestTimestamp = Long.MIN_VALUE;

        for (FlowDefinitionDO otherDef : definitions) {
            if (definition.getFlowCode().equals(otherDef.getFlowCode())) {
                try {
                    int version = Integer.parseInt(otherDef.getVersion());
                    if (version > highestVersion) {
                        highestVersion = version;
                    }
                } catch (NumberFormatException e) {
                    long timestamp = otherDef.getCreateTime().getLong(ChronoField.INSTANT_SECONDS);
                    if (timestamp > latestTimestamp) {
                        latestTimestamp = timestamp;
                        latestNonPositiveVersion = otherDef.getVersion();
                    }
                }
            }
        }

        String version = "1";
        if (highestVersion > 0) {
            version = String.valueOf(highestVersion + 1);
        } else if (latestNonPositiveVersion != null) {
            version = latestNonPositiveVersion + "_1";
        }

        return version;
    }
    /**
     * 校验流程定义合法性
     *
     * @param reqVO
     */
    private void checkFlowLegal(FlowInfoReqVO reqVO) {
        FlowDefinitionVO definition = new FlowDefinitionVO();
        BeanUtils.copyProperties(reqVO, definition);
        String flowName = definition.getFlowName();
        if(StringUtils.isEmpty(definition.getFlowCode())){
            log.error("【" + definition.getFlowName() + "】"+FLOW_NOT_EXISTS.getMsg());
            throw exception(FLOW_NOT_EXISTS);
        }
        // 节点校验
        List<FlowNodeVO> allNodes = reqVO.getNodeList();
        List<FlowSkipVO> allSkips = new ArrayList<>();
        for (FlowNodeVO node : allNodes) {
            if (node.getSkipList() != null) {
                allSkips.addAll(node.getSkipList());
            }
        }
        Map<String, List<FlowSkipVO>> skipMap =
        allSkips.stream().filter(Objects::nonNull)
                .collect(Collectors.groupingBy(FlowSkipVO::getNowNodeCode, LinkedHashMap::new, Collectors.toList()));
        allNodes.forEach(node -> {
            node.setSkipList(skipMap.get(node.getNodeCode()));
            skipMap.remove(node.getNodeCode());
        });
        if(!CollectionUtils.isEmpty(skipMap)){
            log.error("【" + flowName + "】"+FLOW_HAVE_USELESS_SKIP.getMsg());
            throw exception(FLOW_HAVE_USELESS_SKIP);
        }
        // 每一个流程的开始节点个数
        Set<String> nodeCodeSet = new HashSet<>();
        // 便利一个流程中的各个节点
        int startNum = 0;
        for (FlowNodeVO node : allNodes) {
            initNodeAndCondition(node, definition.getId(), definition.getVersion());
            startNum = checkStartAndSame(node, startNum, flowName, nodeCodeSet);
        }
        if(startNum == 0){
            log.error("【" + flowName + "】"+LOST_START_NODE.getMsg());
            throw exception(LOST_START_NODE);
        }
        // 校验跳转节点的合法性
        checkSkipNode(allSkips);
        // 校验所有目标节点是否都存在
        validaIsExistDestNode(allSkips, nodeCodeSet);
    }

    /**
     * 读取工作节点和跳转条件
     *
     * @param node         node
     * @param definitionId definitionId
     * @param version      version
     */
    private  void initNodeAndCondition(FlowNodeVO node, Long definitionId, String version) {
        String nodeName = node.getNodeName();
        String nodeCode = node.getNodeCode();
        List<FlowSkipVO> skipList = node.getSkipList();
        if (!NodeType.isEnd(node.getNodeType())) {
           if(CollectionUtils.isEmpty(skipList)){
               log.error("【" + nodeName + "】"+MUST_SKIP.getMsg());
               throw exception(MUST_SKIP);
           }

        }
        if(StringUtils.isEmpty(nodeCode)){
            log.error("【" + nodeName + "】"+LOST_NODE_CODE.getMsg());
            throw exception(LOST_NODE_CODE);
        }

        node.setVersion(version);
        node.setDefinitionId(definitionId);

        // 中间节点的集合， 跳转类型和目标节点不能重复
        Set<String> betweenSet = new HashSet<>();
        // 网关的集合 跳转条件和下目标节点不能重复
        Set<String> gateWaySet = new HashSet<>();
        int skipNum = 0;
        // 遍历节点下的跳转条件
        if (CollectionUtils.isEmpty(skipList)) {
            return;
        }
        for (FlowSkipVO skip : skipList) {
            if (NodeType.isStart(node.getNodeType())) {
                skipNum++;
                if(skipNum>1){
                    log.error("【" + nodeName + "】" + MUL_START_SKIP.getMsg());
                    throw exception(MUL_START_SKIP);
                }
            }
            if(StringUtils.isEmpty(skip.getNextNodeCode())){
                log.error("【" + nodeName + "】" + LOST_DEST_NODE.getMsg());
                throw exception(LOST_DEST_NODE);
            }
            // 流程id
            skip.setDefinitionId(definitionId);
            skip.setNowNodeType(node.getNodeType());
            if (NodeType.isGateWaySerial(node.getNodeType())) {
                String target = skip.getSkipCondition() + ":" + skip.getNextNodeCode();
                if (!CollectionUtils.isEmpty(gateWaySet) && gateWaySet.contains(target)){
                    log.error("【" + nodeName + "】" + SAME_CONDITION_NODE.getMsg());
                    throw exception(SAME_CONDITION_NODE);
                }
                gateWaySet.add(target);
            } else if (NodeType.isGateWayParallel(node.getNodeType())) {
                String target = skip.getNextNodeCode();
                if (!CollectionUtils.isEmpty(gateWaySet) && gateWaySet.contains(target)){
                    log.error("【" + nodeName + "】" + SAME_DEST_NODE.getMsg());
                    throw exception(SAME_DEST_NODE);
                }
                gateWaySet.add(target);
            } else {
                String value = skip.getSkipType() + ":" + skip.getNextNodeCode();
                if (!CollectionUtils.isEmpty(betweenSet) && gateWaySet.contains(value)){
                    log.error("【" + nodeName + "】" + SAME_CONDITION_VALUE.getMsg());
                    throw exception(SAME_CONDITION_VALUE);
                }
                betweenSet.add(value);
            }
        }
    }
    /**
     * 检查开始节点个数和nodeCode是否重复
     *
     * @param node      node
     * @param startNum  startNum
     * @param flowName  flowName
     * @param nodeCodeSet nodeCodeSet
     * @return startNum
     */
    public  int checkStartAndSame(FlowNodeVO node, int startNum, String flowName, Set<String> nodeCodeSet) {
        if (NodeType.isStart(node.getNodeType())) {
            startNum++;
            if(startNum>1){
                log.error("【" + flowName + "】" + MUL_START_NODE.getMsg());
                throw exception(MUL_START_NODE);
            }
        }
        // 保证不存在重复的nodeCode
        if (!CollectionUtils.isEmpty(nodeCodeSet) && nodeCodeSet.contains(node.getNodeCode())){
            log.error("【" + flowName + "】" + SAME_NODE_CODE.getMsg());
            throw exception(SAME_NODE_CODE);
        }
        nodeCodeSet.add(node.getNodeCode());
        return startNum;
    }

    /**
     * 校验跳转节点的合法性
     *
     * @param allSkips
     */
    public  void checkSkipNode(List<FlowSkipVO> allSkips) {
        Map<String, List<FlowSkipVO>> allSkipMap =
                allSkips.stream().filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(FlowSkipVO::getNowNodeCode, LinkedHashMap::new, Collectors.toList()));
        // 不可同时通过或者退回到多个中间节点，必须先流转到网关节点
        allSkipMap.forEach((key, values) -> {
            AtomicInteger passNum = new AtomicInteger();
            AtomicInteger rejectNum = new AtomicInteger();
            for (FlowSkipVO value : values) {
                if (NodeType.isBetween(value.getNowNodeType()) && NodeType.isBetween(value.getNextNodeType())) {
                    if (SkipType.isPass(value.getSkipType())) {
                        passNum.getAndIncrement();
                    } else {
                        rejectNum.getAndIncrement();
                    }
                }
            }

            if(passNum.get() > 1 || rejectNum.get() > 1){
                log.error( MUL_SKIP_BETWEEN.getMsg());
                throw exception(MUL_SKIP_BETWEEN);
            }
        });
    }
    /**
     * 校验所有的目标节点是否存在
     *
     * @param allSkips
     * @param nodeCodeSet
     */
    public  void validaIsExistDestNode(List<FlowSkipVO> allSkips, Set<String> nodeCodeSet) {
        for (FlowSkipVO allSkip : allSkips) {
            String nextNodeCode = allSkip.getNextNodeCode();
            if (!CollectionUtils.isEmpty(nodeCodeSet) && !nodeCodeSet.contains(nextNodeCode)){
                log.error("【" + nextNodeCode + "】" + LOST_DEST_NODE.getMsg());
                throw exception(NULL_NODE_CODE);
            }
        }
    }
}

