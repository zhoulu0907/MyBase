package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.api.enums.VersionStatusEnum;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDefVersionMgtVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmUpdateReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmVersionMgmtPageReqVo;
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
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
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
     * 获取流程版本管理列表
     *
     * @param reqVo
     * @return
     */
    @Override
    public PageResult<BpmDefVersionMgtVO> getVersionMgmtPage(BpmVersionMgmtPageReqVo reqVo) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.and(Compare.EQUAL, FlowDefinition.FORM_PATH, String.valueOf(reqVo.getBusinessId()));
        if (reqVo.getVersionStatus() != null && !reqVo.getVersionStatus().isEmpty()
                && !StringUtils.equalsIgnoreCase("all", reqVo.getVersionStatus())){
            configStore.and(Compare.EQUAL, "is_publish", convertVersionStatusToPublishStatus(reqVo.getVersionStatus()));
        }
        if (reqVo.getVersionAlias() != null && !reqVo.getVersionAlias().isEmpty()) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "ext::json->>'versionAlias'", "%" + reqVo.getVersionAlias() + "%");
            orCondition.or(Compare.EQUAL, "version", reqVo.getVersionAlias());
            configStore.and(orCondition);
        }
        // 排序：设计中>已发布>历史
        String caseOrder = "CASE WHEN is_publish = " + PublishStatus.UNPUBLISHED.getKey() + " THEN 1 " +
                "WHEN is_publish = " + PublishStatus.PUBLISHED.getKey() + " THEN 2 " +
                "WHEN is_publish = " + PublishStatus.EXPIRED.getKey() + " THEN 3 " +
                "ELSE 4 END";
        configStore.order(caseOrder);
        configStore.order(reqVo.getSortType(), Order.TYPE.DESC);
        PageResult<FlowDefinition> definitions = flowDefinitionRepository.findPageWithConditions(configStore, reqVo.getPageNo(), reqVo.getPageSize());
        return buildVersionMgmtPageResult(definitions);
    }
    /**
     * 将 VersionStatusEnum 的 code 转换为 PublishStatus 的 key
     * @param versionStatusCode VersionStatusEnum 的 code
     * @return PublishStatus 的 key
     */
    private Integer convertVersionStatusToPublishStatus(String versionStatusCode) {
        if (versionStatusCode.equals(VersionStatusEnum.PUBLISHED.getCode())) {
            return PublishStatus.PUBLISHED.getKey();
        } else if (versionStatusCode.equals(VersionStatusEnum.DESIGNING.getCode())) {
            return PublishStatus.UNPUBLISHED.getKey();
        } else if (versionStatusCode.equals(VersionStatusEnum.PREVIOUS.getCode())) {
            return PublishStatus.EXPIRED.getKey();
        }
       return null;
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
        if (definition != null) {
            BpmDefinitionExtDTO extDTO = JsonUtils.parseObject(definition.getExt(), BpmDefinitionExtDTO.class);
            extDTO.setVersionAlias(reqVo.getVersionAlias());
            definition.setExt(JsonUtils.toJsonString(extDTO));
            defService.updateById(definition);
        }
    }

    private PageResult<BpmDefVersionMgtVO> buildVersionMgmtPageResult(PageResult<FlowDefinition> pageResult) {
        if (pageResult == null || CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty();
        }
        List<BpmDefVersionMgtVO> voList = new ArrayList<>();
        // 获取创建人和修改人去重后一次性查出名称
        Set<Long> userIds = pageResult.getList().stream()
                .flatMap(definition -> Stream.of(definition.getCreator(), definition.getUpdater()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(userIds);
        if (userResult.isSuccess()) {
            // 用户信息
            Map<Long, AdminUserRespDTO> userMap = userResult.getData().stream()
                    .collect(Collectors.toMap(AdminUserRespDTO::getId, user -> user));
            for (FlowDefinition definition : pageResult.getList()) {
                BpmDefVersionMgtVO vo = new BpmDefVersionMgtVO();
                vo.setId(definition.getId());
                vo.setVersion("V" + definition.getVersion());
                BpmDefinitionExtDTO extDTO = JsonUtils.parseObject(definition.getExt(), BpmDefinitionExtDTO.class);
                vo.setVersionAlias(extDTO.getVersionAlias());
                vo.setVersionStatus(definition.getIsPublish() == PublishStatus.UNPUBLISHED.getKey() ? "设计中" :
                                definition.getIsPublish() == PublishStatus.PUBLISHED.getKey() ? "已发布" :
                                definition.getIsPublish() == PublishStatus.EXPIRED.getKey() ? "历史" : "未知状态");
                vo.setCreateTime(definition.getCreateTime());
                vo.setUpdateTime(definition.getUpdateTime());
                // 创建人
                vo.setCreator(createOperationUser(userMap.get(definition.getCreator())));
                // 修改人
                vo.setUpdater(createOperationUser(userMap.get(definition.getUpdater())));
                voList.add(vo);
            }
        }
        return new PageResult<>(voList, pageResult.getTotal());
    }
    /**
     * 创建操作人信息
     * @param user 用户信息
     * @return OperationUser 对象，如果用户为空则返回 null
     */
    private BpmDefVersionMgtVO.OperationUser createOperationUser(AdminUserRespDTO user) {
        if (user == null) {
            return null;
        }
        BpmDefVersionMgtVO.OperationUser operationUser = new BpmDefVersionMgtVO.OperationUser();
        operationUser.setOperationUserId(user.getId());
        operationUser.setOperationName(user.getNickname());
        operationUser.setOperationUserAvatar(user.getAvatar());
        return operationUser;
    }

}
