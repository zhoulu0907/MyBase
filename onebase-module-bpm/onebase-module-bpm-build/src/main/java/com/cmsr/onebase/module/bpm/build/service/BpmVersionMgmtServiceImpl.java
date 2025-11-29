package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDefVersionMgtVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmUpdateReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmVersionMgmtPageReqVo;
import com.cmsr.onebase.module.bpm.core.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.VersionStatusEnum;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
import static com.cmsr.onebase.module.engine.orm.mybatisflex.entity.table.FlowDefinitionTableDef.FLOW_DEFINITION;

/**
 * 流程设计服务实现类
 *
 * @author liyang
 * @date 2025-10-20
 */
@Service
@Slf4j
public class BpmVersionMgmtServiceImpl implements BpmVersionMgmtService {

    @Resource(name = "bpmDefService")
    private DefService defService;

    @Resource(name = "bpmNodeService")
    private NodeService nodeService;

    @Resource(name = "bpmSkipService")
    private SkipService skipService;

    @Resource(name = "bpmInsService")
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
        // 设置默认值？todo：抛出异常
        if (!Objects.equals(reqVo.getSortType(), "update_time") && !Objects.equals(reqVo.getSortType(), "create_time")) {
            reqVo.setSortType("update_time");
        }

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowDefinition::getFormPath, String.valueOf(reqVo.getBusinessId()));

        if (StringUtils.isNotBlank(reqVo.getVersionStatus())) {
            VersionStatusEnum versionStatusEnum = VersionStatusEnum.getByCode(reqVo.getVersionStatus());

            if (versionStatusEnum != null && versionStatusEnum.toPublishStatus() != null) {
                queryWrapper.eq(FlowDefinition::getIsPublish, versionStatusEnum.toPublishStatus().getKey());
            } else {
                throw exception(ErrorCodeConstants.UNKNOWN_VERSION_STATUS);
            }
        }

        String versionAlias = reqVo.getVersionAlias();

        if (StringUtils.isNotBlank(reqVo.getVersionAlias())) {
            String versionKeyWord = versionAlias;

            // 去除首字母的V用于版本搜索
            if (Character.toLowerCase(versionAlias.charAt(0)) == 'v') {
                versionKeyWord = versionAlias.substring(1);
            }

            QueryCondition orCondition = QueryCondition.createEmpty();
            orCondition.or(QueryMethods.column("ext::json->>'versionAlias").like(reqVo.getVersionAlias()));
            orCondition.or(FLOW_DEFINITION.VERSION.like(versionKeyWord));

            queryWrapper.and(orCondition);
        }

        // 排序：设计中>已发布>历史
        String caseOrder = "CASE WHEN is_publish = " + PublishStatus.UNPUBLISHED.getKey() + " THEN 1 " +
                "WHEN is_publish = " + PublishStatus.PUBLISHED.getKey() + " THEN 2 " +
                "WHEN is_publish = " + PublishStatus.EXPIRED.getKey() + " THEN 3 " +
                "ELSE 4 END";
        queryWrapper.orderBy(caseOrder);
        queryWrapper.orderBy(QueryMethods.column(reqVo.getSortType()), false);

        Page<FlowDefinition> pageResult = flowDefinitionRepository.page(Page.of(reqVo.getPageNo(), reqVo.getPageSize()), queryWrapper);
        return buildVersionMgmtPageResult(pageResult);
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

    private PageResult<BpmDefVersionMgtVO> buildVersionMgmtPageResult(Page<FlowDefinition> pageResult) {
        if (pageResult == null || CollUtil.isEmpty(pageResult.getRecords())) {
            return PageResult.empty();
        }
        List<BpmDefVersionMgtVO> voList = new ArrayList<>();
        // 获取创建人和修改人去重后一次性查出名称
        Set<Long> userIds = pageResult.getRecords().stream()
                .flatMap(definition -> Stream.of(definition.getCreator(), definition.getUpdater()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(userIds);
        if (userResult.isSuccess()) {
            // 用户信息
            Map<Long, AdminUserRespDTO> userMap = userResult.getData().stream()
                    .collect(Collectors.toMap(AdminUserRespDTO::getId, user -> user));
            for (FlowDefinition definition : pageResult.getRecords()) {
                BpmDefVersionMgtVO vo = new BpmDefVersionMgtVO();
                vo.setId(definition.getId());
                vo.setVersion("V" + definition.getVersion());
                BpmDefinitionExtDTO extDTO = JsonUtils.parseObject(definition.getExt(), BpmDefinitionExtDTO.class);
                vo.setVersionAlias(extDTO.getVersionAlias());
                VersionStatusEnum versionStatusEnum = VersionStatusEnum.toVersionStatusEnum(definition.getIsPublish());
                vo.setVersionStatus(versionStatusEnum.getName());
                vo.setCreateTime(definition.getCreateTime());
                vo.setUpdateTime(definition.getUpdateTime());
                // 创建人
                vo.setCreator(createOperationUser(userMap.get(definition.getCreator())));
                // 修改人
                vo.setUpdater(createOperationUser(userMap.get(definition.getUpdater())));
                voList.add(vo);
            }
        }
        return new PageResult<>(voList, pageResult.getTotalRow());
    }
    /**
     * 创建操作人信息
     * @param user 用户信息
     * @return OperationUser 对象，如果用户为空则返回 null
     */
    private UserBasicInfoVO createOperationUser(AdminUserRespDTO user) {
        if (user == null) {
            return null;
        }
        UserBasicInfoVO operationUser = new UserBasicInfoVO();
        operationUser.setUserId(user.getId());
        operationUser.setName(user.getNickname());
        operationUser.setAvatar(user.getAvatar());
        return operationUser;
    }

}
