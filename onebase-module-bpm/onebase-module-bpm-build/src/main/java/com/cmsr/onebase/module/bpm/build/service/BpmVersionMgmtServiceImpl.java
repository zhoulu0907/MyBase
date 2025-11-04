package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDefVersionMgtVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmGetReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmUpdateReqVo;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.SkipService;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程设计服务实现类
 *
 * @author liyang
 * @date 2025-10-20
 */
@Service
@Slf4j
public class BpmVersionMgmtServiceImpl implements BpmVersionMgmtService {

    @Resource
    private DefService defService;

    @Resource
    private NodeService nodeService;

    @Resource
    private SkipService skipService;

    @Resource
    private InsService insService;

    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 可对指定的流程版本进行删除，但已发布版本及含有尚未完结的历史版本流程无法删除。
     *
     * @param reqVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(BpmDeleteReqVo reqVo) {
        List<Long> ids = new ArrayList<>();
        ids.add(reqVo.getId());

        // 校验流程是否存在
        Definition existDef = defService.getById(String.valueOf(reqVo.getId()));

        if (existDef == null) {
            return;
        }

        // 已发布流程无法删除
        if (existDef.getIsPublish().equals(PublishStatus.PUBLISHED.getKey())) {
            throw exception(ErrorCodeConstants.DELETE_FLOW_FAILED_FOR_PUBLISHED);
        }

        // 包含历史未完结的历史版本无法删除
        List<Instance> instanceList = insService.getByDefId(reqVo.getId());

        if (CollUtil.isNotEmpty(instanceList)) {
            for (Instance instance : instanceList) {
                if (!instance.getFlowStatus().equals(FlowStatus.FINISHED.getKey())) {
                    throw exception(ErrorCodeConstants.DELETE_FLOW_FAILED_FOR_INS_NOT_FINISHED);
                }
            }
        }

        // 删除流程节点和跳转
        nodeService.deleteNodeByDefIds(ids);
        skipService.deleteSkipByDefIds(ids);

        boolean success = defService.removeById(reqVo.getId());

        if (!success) {
            throw exception(ErrorCodeConstants.DELETE_FLOW_FAILED);
        }
    }
    /**
     * 根据业务ID获取流程版本信息
     *
     * @param reqVo
     * @return
     */
    @Override
    public List<BpmDefVersionMgtVO> getByBusinessId(BpmGetReqVo reqVo) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.and(Compare.EQUAL, FlowDefinition.FORM_PATH, String.valueOf(reqVo.getBusinessId()));
        // 排序：设计中>已发布>历史
        String caseOrder = "CASE WHEN is_publish = 0 THEN 1 " +
                "WHEN is_publish = 1 THEN 2 " +
                "WHEN is_publish = 9 THEN 3 " +
                "ELSE 4 END";
        configStore.order(caseOrder);
        List<FlowDefinition> definitions = flowDefinitionRepository.findAllByConfig(configStore);
        return convertToVo(definitions);
    }
    /**
     * 更新流程版本备注
     *
     * @param reqVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVersionAliasById(BpmUpdateReqVo reqVo) {
        Definition definition = defService.getById(String.valueOf(reqVo.getId()));
        if(definition!=null){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                BpmDefinitionExtDTO extDTO = objectMapper.readValue(definition.getExt(), BpmDefinitionExtDTO.class);
                extDTO.setVersionAlias(reqVo.getVersionAlias());
                definition.setExt(JsonUtils.toJsonString(extDTO));
                defService.updateById(definition);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<BpmDefVersionMgtVO> convertToVo(List<FlowDefinition> definitions) {
        List<BpmDefVersionMgtVO> voList = new ArrayList<>();
        if (CollUtil.isNotEmpty(definitions)) {
            try {
                // 获取创建人和修改人去重后一次性查出名称
                Set<Long> userIds = definitions.stream()
                        .flatMap(definition -> Stream.of(definition.getCreator(), definition.getUpdater()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(userIds);
                if (userResult.isSuccess()) {
                    // 用户信息
                    Map<Long, AdminUserRespDTO> userMap = userResult.getData().stream()
                            .collect(Collectors.toMap(AdminUserRespDTO::getId, user -> user));
                    for (FlowDefinition definition : definitions) {
                        BpmDefVersionMgtVO vo = new BpmDefVersionMgtVO();
                        vo.setId(definition.getId());
                        vo.setVersion("V"+definition.getVersion());
                        ObjectMapper objectMapper = new ObjectMapper();
                        BpmDefinitionExtDTO extDTO = objectMapper.readValue(definition.getExt(), BpmDefinitionExtDTO.class);
                        vo.setVersionAlias(extDTO.getVersionAlias());
                        vo.setVersionStatus(definition.getIsPublish() == 0 ? "设计中" :
                                            definition.getIsPublish() == 1 ? "已发布" :
                                            definition.getIsPublish() == 9 ? "历史" : "未知状态");
                        vo.setCreateTime(definition.getCreateTime());
                        vo.setUpdateTime(definition.getUpdateTime());
                        // 创建人
                        AdminUserRespDTO creatorUser = userMap.get(definition.getCreator());
                        if (creatorUser != null) {
                            BpmDefVersionMgtVO.OperationUser creator = new BpmDefVersionMgtVO.OperationUser();
                            creator.setOperationUserId(creatorUser.getId());
                            creator.setOperationName(creatorUser.getNickname());
                            creator.setOperationUserAvatar(creatorUser.getAvatar());
                            vo.setCreator( creator);
                        }
                        // 修改人
                        AdminUserRespDTO updaterUser = userMap.get(definition.getUpdater());
                        if (updaterUser != null) {
                            BpmDefVersionMgtVO.OperationUser updater = new BpmDefVersionMgtVO.OperationUser();
                            updater.setOperationUserId(creatorUser.getId());
                            updater.setOperationName(creatorUser.getNickname());
                            updater.setOperationUserAvatar(creatorUser.getAvatar());
                            vo.setUpdater(updater);
                        }
                        voList.add(vo);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return voList;
    }


}
