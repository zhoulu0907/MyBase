package com.cmsr.onebase.module.bpm.core.validator;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.AppMenuRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.AppPagesetRespDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * BPM应用菜单校验器
 *
 * @author liyang
 * @date 2025-12-03
 */
@Component
public class BpmAppResourceValidator {
    @Resource
    private AppResourceApi appResourceApi;

    /**
     * 校验菜单及页面集是否存在及应用ID是否匹配
     * @param menuDTO 菜单DTO
     * @param applicationId 应用ID（可为空，为空时从ApplicationManager获取）
     */
    public void validateMenuAndPageset(AppMenuRespDTO menuDTO, Long applicationId) {
        Long currAppId = applicationId != null ? applicationId : ApplicationManager.getApplicationId();

        validateMenu(menuDTO, currAppId);
        validatePageset(menuDTO, currAppId);
    }

    /**
     * 验证菜单是否存在及应用ID是否匹配
     *
     * @param menuDTO 菜单DTO
     * @param applicationId 应用ID（可为空，为空时从ApplicationManager获取）
     */
    public void validateMenu(AppMenuRespDTO menuDTO, Long applicationId) {
        Long currAppId = applicationId != null ? applicationId : ApplicationManager.getApplicationId();

        if (menuDTO == null) {
            throw exception(ErrorCodeConstants.MENU_NOT_EXISTS);
        }

        if (!Objects.equals(menuDTO.getApplicationId(), currAppId)) {
            throw exception(ErrorCodeConstants.APPLICATION_ID_MISMATCH);
        }
    }

    /**
     * 校验菜单绑定的页面集类型是否为BPM类型
     *
     * @param menuDTO 菜单DTO
     * @param applicationId 应用ID（可为空，为空时从ApplicationManager获取）
     */
    public void validatePageset(AppMenuRespDTO menuDTO, Long applicationId) {
        Long currAppId = applicationId != null ? applicationId : ApplicationManager.getApplicationId();

        AppPagesetRespDTO pagesetDTO = appResourceApi.getPageSetByMenuUuidAndAppId(menuDTO.getMenuUuid(), currAppId);
        if (pagesetDTO == null) {
            throw exception(ErrorCodeConstants.MENU_NOT_BIND_PAGESET);
        }

        if (!Objects.equals(pagesetDTO.getPageSetType(), BpmConstants.PAGESET_TYPE_BPM)) {
            throw exception(ErrorCodeConstants.UNSUPPORT_PAGESET_TYPE);
        }
    }
}
