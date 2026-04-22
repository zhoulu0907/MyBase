package com.cmsr.onebase.module.app.build.service.custombutton;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.build.vo.custombutton.*;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonActionFlowRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonActionFlowDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.enums.custombutton.AppCustomButtonErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonActionTypeEnum;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonOperationScopeEnum;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonStatusEnum;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class AppCustomButtonServiceImpl implements AppCustomButtonService {

    @Resource
    private AppCustomButtonRepository customButtonRepository;

    @Resource
    private AppCustomButtonActionFlowRepository actionFlowRepository;

    @Resource
    private AppPageSetRepository pageSetRepository;

    @Override
    public CustomButtonPageRespVO page(CustomButtonPageReqVO reqVO) {
        AppResourcePagesetDO pageSetDO = getAndValidatePageSet(reqVO.getPageSetId());
        List<AppCustomButtonDO> buttonDOS = customButtonRepository.findByPageSetUuid(pageSetDO.getPageSetUuid());
        List<CustomButtonListItemRespVO> items = new ArrayList<>();
        for (AppCustomButtonDO buttonDO : buttonDOS) {
            CustomButtonListItemRespVO item = new CustomButtonListItemRespVO();
            item.setId(buttonDO.getId());
            item.setButtonCode(buttonDO.getButtonCode());
            item.setButtonName(buttonDO.getButtonName());
            item.setOperationScope(buttonDO.getOperationScope());
            item.setActionType(buttonDO.getActionType());
            item.setStatus(buttonDO.getStatus());
            item.setSortNo(buttonDO.getSortNo());
            items.add(item);
        }
        CustomButtonPageRespVO respVO = new CustomButtonPageRespVO();
        respVO.setList(items);
        return respVO;
    }

    @Override
    public CustomButtonDetailRespVO get(Long id) {
        AppCustomButtonDO buttonDO = customButtonRepository.getById(id);
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        CustomButtonDetailRespVO respVO = new CustomButtonDetailRespVO();
        respVO.setId(buttonDO.getId());
        respVO.setButtonCode(buttonDO.getButtonCode());
        respVO.setPageId(safeLong(buttonDO.getPageUuid()));
        AppResourcePagesetDO pageSetDO = pageSetRepository.findByUuidInApplication(
                ApplicationManager.getRequiredApplicationId(), buttonDO.getPageSetUuid());
        if (pageSetDO != null) {
            respVO.setPageSetId(pageSetDO.getId());
        }
        respVO.setButtonName(buttonDO.getButtonName());
        respVO.setButtonDesc(buttonDO.getButtonDesc());
        respVO.setShowDesc(buttonDO.getShowDesc());
        respVO.setStyleType(buttonDO.getStyleType());
        respVO.setColorHex(buttonDO.getColorHex());
        respVO.setColorAlpha(buttonDO.getColorAlpha());
        respVO.setIconCode(buttonDO.getIconCode());
        respVO.setOperationScope(buttonDO.getOperationScope());
        respVO.setShowInForm(buttonDO.getShowInForm());
        respVO.setShowInRowAction(buttonDO.getShowInRowAction());
        respVO.setShowInBatchAction(buttonDO.getShowInBatchAction());
        respVO.setActionType(buttonDO.getActionType());
        respVO.setSortNo(buttonDO.getSortNo());
        respVO.setStatus(buttonDO.getStatus());
        if (CustomButtonActionTypeEnum.TRIGGER_FLOW.getCode().equalsIgnoreCase(buttonDO.getActionType())) {
            AppCustomButtonActionFlowDO flowDO = actionFlowRepository.findByButtonUuid(buttonDO.getButtonUuid());
            if (flowDO != null) {
                CustomButtonFlowActionReqVO flowReqVO = new CustomButtonFlowActionReqVO();
                flowReqVO.setFlowProcessId(flowDO.getFlowProcessId());
                flowReqVO.setFlowProcessUuid(flowDO.getFlowProcessUuid());
                flowReqVO.setConfirmRequired(flowDO.getConfirmRequired());
                flowReqVO.setConfirmText(flowDO.getConfirmText());
                respVO.setFlowAction(flowReqVO);
            }
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CustomButtonSaveReqVO reqVO) {
        AppResourcePagesetDO pageSetDO = getAndValidatePageSet(reqVO.getPageSetId());
        validateSaveReq(reqVO, pageSetDO.getPageSetUuid(), null);
        Long currentCount = customButtonRepository.countByPageSetUuid(pageSetDO.getPageSetUuid());
        if (currentCount != null && currentCount >= 10L) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_COUNT_EXCEED_LIMIT);
        }
        AppCustomButtonDO buttonDO = new AppCustomButtonDO();
        buttonDO.setButtonUuid(UuidUtils.getUuid());
        buttonDO.setButtonCode(generateButtonCode());
        buttonDO.setMenuUuid(pageSetDO.getMenuUuid());
        buttonDO.setPageSetUuid(pageSetDO.getPageSetUuid());
        buttonDO.setPageUuid(reqVO.getPageId() == null ? null : String.valueOf(reqVO.getPageId()));
        applySaveFields(buttonDO, reqVO);
        customButtonRepository.save(buttonDO);
        upsertFlowConfig(buttonDO, reqVO);
        return buttonDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(CustomButtonSaveReqVO reqVO) {
        if (reqVO.getId() == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        AppCustomButtonDO buttonDO = customButtonRepository.getById(reqVO.getId());
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        AppResourcePagesetDO pageSetDO = getAndValidatePageSet(reqVO.getPageSetId());
        validateSaveReq(reqVO, pageSetDO.getPageSetUuid(), reqVO.getId());
        buttonDO.setMenuUuid(pageSetDO.getMenuUuid());
        buttonDO.setPageSetUuid(pageSetDO.getPageSetUuid());
        buttonDO.setPageUuid(reqVO.getPageId() == null ? null : String.valueOf(reqVO.getPageId()));
        applySaveFields(buttonDO, reqVO);
        customButtonRepository.updateById(buttonDO);
        upsertFlowConfig(buttonDO, reqVO);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        AppCustomButtonDO buttonDO = customButtonRepository.getById(id);
        if (buttonDO == null) {
            return true;
        }
        actionFlowRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        customButtonRepository.removeById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(CustomButtonStatusReqVO reqVO) {
        AppCustomButtonDO buttonDO = customButtonRepository.getById(reqVO.getId());
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        buttonDO.setStatus(reqVO.getStatus());
        customButtonRepository.updateById(buttonDO);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sort(CustomButtonSortReqVO reqVO) {
        getAndValidatePageSet(reqVO.getPageSetId());
        if (CollectionUtils.isEmpty(reqVO.getItems())) {
            return true;
        }
        for (CustomButtonSortReqVO.Item item : reqVO.getItems()) {
            AppCustomButtonDO buttonDO = customButtonRepository.getById(item.getId());
            if (buttonDO == null) {
                continue;
            }
            buttonDO.setSortNo(item.getSortNo());
            customButtonRepository.updateById(buttonDO);
        }
        return true;
    }

    private void validateSaveReq(CustomButtonSaveReqVO reqVO, String pageSetUuid, Long excludeId) {
        if (!CustomButtonActionTypeEnum.exists(reqVO.getActionType())) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_ACTION_TYPE_INVALID);
        }
        if (!CustomButtonOperationScopeEnum.exists(reqVO.getOperationScope())) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_SCOPE_INVALID);
        }
        if (customButtonRepository.existsByPageSetUuidAndButtonName(pageSetUuid, reqVO.getButtonName(), excludeId)) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NAME_DUPLICATE);
        }
        if (CustomButtonActionTypeEnum.TRIGGER_FLOW.getCode().equalsIgnoreCase(reqVO.getActionType())) {
            if (reqVO.getFlowAction() == null || reqVO.getFlowAction().getFlowProcessId() == null) {
                throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_FLOW_CONFIG_REQUIRED);
            }
        }
    }

    private void applySaveFields(AppCustomButtonDO buttonDO, CustomButtonSaveReqVO reqVO) {
        buttonDO.setButtonName(reqVO.getButtonName());
        buttonDO.setButtonDesc(reqVO.getButtonDesc());
        buttonDO.setShowDesc(reqVO.getShowDesc() == null ? 1 : reqVO.getShowDesc());
        buttonDO.setStyleType(reqVO.getStyleType());
        buttonDO.setColorHex(reqVO.getColorHex());
        buttonDO.setColorAlpha(reqVO.getColorAlpha());
        buttonDO.setIconCode(reqVO.getIconCode());
        buttonDO.setOperationScope(reqVO.getOperationScope());
        buttonDO.setShowInForm(reqVO.getShowInForm() == null ? 0 : reqVO.getShowInForm());
        buttonDO.setShowInRowAction(reqVO.getShowInRowAction() == null ? 0 : reqVO.getShowInRowAction());
        buttonDO.setShowInBatchAction(reqVO.getShowInBatchAction() == null ? 0 : reqVO.getShowInBatchAction());
        buttonDO.setActionType(reqVO.getActionType());
        buttonDO.setSortNo(reqVO.getSortNo() == null ? 0 : reqVO.getSortNo());
        buttonDO.setStatus(StringUtils.isBlank(reqVO.getStatus()) ? CustomButtonStatusEnum.ENABLE.getCode() : reqVO.getStatus());
    }

    private void upsertFlowConfig(AppCustomButtonDO buttonDO, CustomButtonSaveReqVO reqVO) {
        if (!CustomButtonActionTypeEnum.TRIGGER_FLOW.getCode().equalsIgnoreCase(buttonDO.getActionType())) {
            actionFlowRepository.removeByButtonUuid(buttonDO.getButtonUuid());
            return;
        }
        CustomButtonFlowActionReqVO flowReqVO = reqVO.getFlowAction();
        if (flowReqVO == null) {
            return;
        }
        AppCustomButtonActionFlowDO flowDO = actionFlowRepository.findByButtonUuid(buttonDO.getButtonUuid());
        if (flowDO == null) {
            flowDO = new AppCustomButtonActionFlowDO();
            flowDO.setButtonUuid(buttonDO.getButtonUuid());
            flowDO.setApplicationId(buttonDO.getApplicationId());
        }
        flowDO.setFlowProcessId(flowReqVO.getFlowProcessId());
        flowDO.setFlowProcessUuid(flowReqVO.getFlowProcessUuid());
        flowDO.setConfirmRequired(flowReqVO.getConfirmRequired() == null ? 1 : flowReqVO.getConfirmRequired());
        flowDO.setConfirmText(flowReqVO.getConfirmText());
        if (flowDO.getId() == null) {
            actionFlowRepository.save(flowDO);
        } else {
            actionFlowRepository.updateById(flowDO);
        }
    }

    private AppResourcePagesetDO getAndValidatePageSet(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(pageSetId);
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_PAGESET_NOT_EXISTS);
        }
        return pageSetDO;
    }

    private Long safeLong(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception ex) {
            return null;
        }
    }

    private String generateButtonCode() {
        String uuid = UuidUtils.getUuid().replace("-", "");
        int codeLength = Math.min(16, uuid.length());
        return "cb_" + uuid.substring(0, codeLength);
    }
}
