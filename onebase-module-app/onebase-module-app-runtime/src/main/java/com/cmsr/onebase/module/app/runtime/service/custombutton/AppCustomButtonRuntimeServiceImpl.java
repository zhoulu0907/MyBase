package com.cmsr.onebase.module.app.runtime.service.custombutton;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonActionFlowRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonExecDetailRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonExecLogRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.*;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.enums.custombutton.AppCustomButtonErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonActionTypeEnum;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonExecStatusEnum;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonOperationScopeEnum;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonStatusEnum;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthSecurityRoleProvider;
import com.cmsr.onebase.module.app.runtime.vo.custombutton.*;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Validated
public class AppCustomButtonRuntimeServiceImpl implements AppCustomButtonRuntimeService {

    @Resource
    private AppPageSetRepository pageSetRepository;

    @Resource
    private AppCustomButtonRepository customButtonRepository;

    @Resource
    private AppCustomButtonActionFlowRepository actionFlowRepository;

    @Resource
    private AppCustomButtonExecLogRepository execLogRepository;

    @Resource
    private AppCustomButtonExecDetailRepository execDetailRepository;

    @Resource
    private AppAuthSecurityRoleProvider authSecurityRoleProvider;

    @Resource
    private AppAuthPermissionRepository authPermissionRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Override
    public List<RuntimeCustomButtonRespVO> listAvailable(RuntimeCustomButtonListReqVO reqVO) {
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(reqVO.getPageSetId());
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_PAGESET_NOT_EXISTS);
        }
        String operationScope = Boolean.TRUE.equals(reqVO.getBatch())
                ? CustomButtonOperationScopeEnum.BATCH.getCode()
                : CustomButtonOperationScopeEnum.SINGLE.getCode();
        List<AppCustomButtonDO> buttonDOS = customButtonRepository
                .findEnabledByPageSetUuidAndScope(pageSetDO.getPageSetUuid(), operationScope);
        Set<String> allowedCodes = resolveAllowedButtonCodes(reqVO.getMenuId());
        List<RuntimeCustomButtonRespVO> result = new ArrayList<>();
        for (AppCustomButtonDO buttonDO : buttonDOS) {
            if (allowedCodes != null && !allowedCodes.contains(buttonDO.getButtonCode())) {
                continue;
            }
            RuntimeCustomButtonRespVO item = new RuntimeCustomButtonRespVO();
            item.setButtonCode(buttonDO.getButtonCode());
            item.setButtonName(buttonDO.getButtonName());
            item.setButtonDesc(buttonDO.getButtonDesc());
            item.setActionType(buttonDO.getActionType());
            item.setOperationScope(buttonDO.getOperationScope());
            item.setStyleType(buttonDO.getStyleType());
            item.setColorHex(buttonDO.getColorHex());
            item.setIconCode(buttonDO.getIconCode());
            result.add(item);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuntimeCustomButtonExecuteRespVO execute(RuntimeCustomButtonExecuteReqVO reqVO) {
        AppCustomButtonDO buttonDO = customButtonRepository.findByButtonCode(reqVO.getButtonCode());
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        if (!CustomButtonStatusEnum.ENABLE.getCode().equalsIgnoreCase(buttonDO.getStatus())) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_DISABLED);
        }
        validateButtonPermission(reqVO.getMenuId(), buttonDO.getButtonCode());

        LocalDateTime startTime = LocalDateTime.now();
        AppCustomButtonExecLogDO logDO = buildExecLog(buttonDO, reqVO.getMenuId(), reqVO.getPageSetId(), reqVO.getPageId(),
                reqVO.getRecordId(), null, reqVO.getActionPayload(), CustomButtonExecStatusEnum.RUNNING.getCode());
        logDO.setStartTime(startTime);
        execLogRepository.save(logDO);

        RuntimeCustomButtonExecuteRespVO respVO = new RuntimeCustomButtonExecuteRespVO();
        respVO.setExecLogId(logDO.getId());
        try {
            String message = doExecuteAction(buttonDO, reqVO.getRecordId());
            LocalDateTime endTime = LocalDateTime.now();
            logDO.setExecStatus(CustomButtonExecStatusEnum.SUCCESS.getCode());
            logDO.setResponseSnapshot(message);
            logDO.setEndTime(endTime);
            logDO.setDurationMs(java.time.Duration.between(startTime, endTime).toMillis());
            execLogRepository.updateById(logDO);
            respVO.setSuccess(true);
            respVO.setMessage(message);
            return respVO;
        } catch (Exception ex) {
            LocalDateTime endTime = LocalDateTime.now();
            logDO.setExecStatus(CustomButtonExecStatusEnum.FAILED.getCode());
            logDO.setErrorCode("EXEC_FAIL");
            logDO.setErrorMessage(ex.getMessage());
            logDO.setEndTime(endTime);
            logDO.setDurationMs(java.time.Duration.between(startTime, endTime).toMillis());
            execLogRepository.updateById(logDO);
            throw ex;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuntimeCustomButtonBatchExecuteRespVO batchExecute(RuntimeCustomButtonBatchExecuteReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getRecordIds())) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_BATCH_RECORDS_EMPTY);
        }
        AppCustomButtonDO buttonDO = customButtonRepository.findByButtonCode(reqVO.getButtonCode());
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        if (!CustomButtonOperationScopeEnum.BATCH.getCode().equalsIgnoreCase(buttonDO.getOperationScope())) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_SCOPE_INVALID);
        }
        validateButtonPermission(reqVO.getMenuId(), buttonDO.getButtonCode());
        String batchNo = UuidUtils.getUuid();
        LocalDateTime startTime = LocalDateTime.now();
        AppCustomButtonExecLogDO logDO = buildExecLog(buttonDO, reqVO.getMenuId(), reqVO.getPageSetId(), reqVO.getPageId(),
                null, batchNo, JsonUtils.toJsonString(reqVO.getRecordIds()), CustomButtonExecStatusEnum.RUNNING.getCode());
        logDO.setStartTime(startTime);
        execLogRepository.save(logDO);

        int successCount = 0;
        int failCount = 0;
        List<AppCustomButtonExecDetailDO> details = new ArrayList<>();
        for (String recordId : reqVO.getRecordIds()) {
            AppCustomButtonExecDetailDO detailDO = new AppCustomButtonExecDetailDO();
            detailDO.setExecLogId(logDO.getId());
            detailDO.setBatchNo(batchNo);
            detailDO.setRecordId(recordId);
            detailDO.setApplicationId(logDO.getApplicationId());
            try {
                String result = doExecuteAction(buttonDO, recordId);
                detailDO.setExecStatus(CustomButtonExecStatusEnum.SUCCESS.getCode());
                detailDO.setResultSnapshot(result);
                successCount++;
            } catch (Exception ex) {
                detailDO.setExecStatus(CustomButtonExecStatusEnum.FAILED.getCode());
                detailDO.setErrorCode("EXEC_FAIL");
                detailDO.setErrorMessage(ex.getMessage());
                failCount++;
            }
            details.add(detailDO);
        }
        if (CollectionUtils.isNotEmpty(details)) {
            execDetailRepository.saveBatch(details);
        }
        LocalDateTime endTime = LocalDateTime.now();
        logDO.setExecStatus(failCount > 0 ? CustomButtonExecStatusEnum.FAILED.getCode() : CustomButtonExecStatusEnum.SUCCESS.getCode());
        logDO.setResponseSnapshot(String.format("success=%d, fail=%d", successCount, failCount));
        logDO.setEndTime(endTime);
        logDO.setDurationMs(java.time.Duration.between(startTime, endTime).toMillis());
        execLogRepository.updateById(logDO);

        RuntimeCustomButtonBatchExecuteRespVO respVO = new RuntimeCustomButtonBatchExecuteRespVO();
        respVO.setExecLogId(logDO.getId());
        respVO.setBatchNo(batchNo);
        respVO.setSuccessCount(successCount);
        respVO.setFailCount(failCount);
        return respVO;
    }

    @Override
    public RuntimeCustomButtonExecLogRespVO getExecLog(Long execLogId) {
        AppCustomButtonExecLogDO logDO = execLogRepository.getByExecLogId(execLogId);
        if (logDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        RuntimeCustomButtonExecLogRespVO respVO = new RuntimeCustomButtonExecLogRespVO();
        respVO.setId(logDO.getId());
        respVO.setButtonCode(logDO.getButtonCode());
        respVO.setButtonName(logDO.getButtonName());
        respVO.setActionType(logDO.getActionType());
        respVO.setExecStatus(logDO.getExecStatus());
        respVO.setErrorCode(logDO.getErrorCode());
        respVO.setErrorMessage(logDO.getErrorMessage());
        respVO.setBatchNo(logDO.getBatchNo());
        respVO.setStartTime(logDO.getStartTime());
        respVO.setEndTime(logDO.getEndTime());
        respVO.setDurationMs(logDO.getDurationMs());
        List<AppCustomButtonExecDetailDO> detailDOS = execDetailRepository.findByExecLogId(execLogId);
        List<RuntimeCustomButtonExecLogRespVO.Detail> details = new ArrayList<>();
        for (AppCustomButtonExecDetailDO detailDO : detailDOS) {
            RuntimeCustomButtonExecLogRespVO.Detail detail = new RuntimeCustomButtonExecLogRespVO.Detail();
            detail.setRecordId(detailDO.getRecordId());
            detail.setExecStatus(detailDO.getExecStatus());
            detail.setErrorCode(detailDO.getErrorCode());
            detail.setErrorMessage(detailDO.getErrorMessage());
            details.add(detail);
        }
        respVO.setDetails(details);
        return respVO;
    }

    private void validateButtonPermission(Long menuId, String buttonCode) {
        if (menuId == null) {
            return;
        }
        Set<String> allowedCodes = resolveAllowedButtonCodes(menuId);
        if (allowedCodes != null && (CollectionUtils.isEmpty(allowedCodes) || !allowedCodes.contains(buttonCode))) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_PERMISSION_DENIED);
        }
    }

    private Set<String> resolveAllowedButtonCodes(Long menuId) {
        if (menuId == null) {
            return null;
        }
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long applicationId = ApplicationManager.getRequiredApplicationId();
        UserRoleDTO userRoleDTO = authSecurityRoleProvider.findUserRoleByApplication(userId, applicationId);
        if (userRoleDTO == null || CollectionUtils.isEmpty(userRoleDTO.getRoleUuids())) {
            return Collections.emptySet();
        }
        if (userRoleDTO.isAdminRole()) {
            return null;
        }
        AppMenuDO appMenuDO = appMenuRepository.getById(menuId);
        if (appMenuDO == null) {
            return Collections.emptySet();
        }
        List<AppAuthPermissionDO> permissionDOS = authPermissionRepository.findByAppIdAndRoleIdsAndMenuId(
                applicationId, userRoleDTO.getRoleUuids(), appMenuDO.getMenuUuid());
        Set<String> operationCodes = new HashSet<>();
        for (AppAuthPermissionDO permissionDO : permissionDOS) {
            if (StringUtils.isBlank(permissionDO.getOperationTags())) {
                continue;
            }
            List<String> tags = JsonUtils.parseArray(permissionDO.getOperationTags(), String.class);
            operationCodes.addAll(tags);
        }
        return operationCodes;
    }

    private AppCustomButtonExecLogDO buildExecLog(AppCustomButtonDO buttonDO, Long menuId, Long pageSetId, Long pageId,
                                                  String recordId, String batchNo, String requestSnapshot, String status) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        AppCustomButtonExecLogDO logDO = new AppCustomButtonExecLogDO();
        logDO.setApplicationId(ApplicationManager.getRequiredApplicationId());
        logDO.setButtonUuid(buttonDO.getButtonUuid());
        logDO.setButtonCode(buttonDO.getButtonCode());
        logDO.setButtonName(buttonDO.getButtonName());
        logDO.setActionType(buttonDO.getActionType());
        logDO.setOperatorUserId(loginUser == null ? null : loginUser.getId());
        logDO.setOperatorDeptId(SecurityFrameworkUtils.getLoginUserDeptId());
        logDO.setMenuUuid(resolveMenuUuid(menuId, buttonDO));
        logDO.setPageSetUuid(buttonDO.getPageSetUuid());
        logDO.setPageUuid(pageId == null ? null : String.valueOf(pageId));
        logDO.setRecordId(recordId);
        logDO.setBatchNo(batchNo);
        logDO.setOperationScope(buttonDO.getOperationScope());
        logDO.setExecStatus(status);
        logDO.setRequestSnapshot(requestSnapshot);
        return logDO;
    }

    private String doExecuteAction(AppCustomButtonDO buttonDO, String recordId) {
        String actionType = buttonDO.getActionType();
        if (CustomButtonActionTypeEnum.TRIGGER_FLOW.getCode().equalsIgnoreCase(actionType)) {
            AppCustomButtonActionFlowDO flowDO = actionFlowRepository.findByButtonUuid(buttonDO.getButtonUuid());
            if (flowDO == null || flowDO.getFlowProcessId() == null) {
                throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_FLOW_CONFIG_REQUIRED);
            }
            return String.format("已受理自动化流触发请求，流程ID=%s，记录ID=%s", flowDO.getFlowProcessId(), recordId);
        }
        if (CustomButtonActionTypeEnum.UPDATE_FORM.getCode().equalsIgnoreCase(actionType)) {
            return String.format("已受理修改当前表单请求，记录ID=%s", recordId);
        }
        if (CustomButtonActionTypeEnum.CREATE_RELATED_RECORD.getCode().equalsIgnoreCase(actionType)) {
            return String.format("已生成关联表单预填充上下文，记录ID=%s", recordId);
        }
        if (CustomButtonActionTypeEnum.OPEN_PAGE.getCode().equalsIgnoreCase(actionType)) {
            return String.format("已生成页面跳转参数，记录ID=%s", recordId);
        }
        throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_ACTION_TYPE_INVALID);
    }

    private String resolveMenuUuid(Long menuId, AppCustomButtonDO buttonDO) {
        if (menuId == null) {
            return buttonDO.getMenuUuid();
        }
        AppMenuDO menuDO = appMenuRepository.getById(menuId);
        return menuDO == null ? buttonDO.getMenuUuid() : menuDO.getMenuUuid();
    }
}
