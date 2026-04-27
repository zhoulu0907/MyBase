package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthViewRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.*;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:57
 */
@Setter
@Service
public class AppAuthPermissionServiceImpl implements AppAuthPermissionService {

    @Autowired
    private AppAuthDataGroupRepository authDataGroupRepository;

    @Autowired
    private AppAuthPermissionRepository authPermissionRepository;

    @Autowired
    private AppAuthFieldRepository authFieldRepository;

    @Autowired
    private AppAuthViewRepository authViewRepository;

    @Autowired
    private AppPageSetRepository appPageSetRepository;

    @Autowired
    private AppPageRepository appPageRepository;

    @Autowired
    private AppCommonService appCommonService;

    @Autowired
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Override
    public AuthDetailFunctionPermissionVO getFunctionPermission(AuthPermissionReq reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        reqVO.setRoleUuid(appAuthRoleDO.getRoleUuid());
        reqVO.setMenuUuid(appMenuDO.getMenuUuid());
        //
        AuthDetailFunctionPermissionVO functionPermissionVO = new AuthDetailFunctionPermissionVO();
        AppAuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createDefaultAuthPermissionDO(reqVO);
        }
        // 页面权限
        functionPermissionVO.setIsPageAllowed(authPermissionDO.getIsPageAllowed());
        // 操作权限
        List<String> operationTags = JsonUtils.parseArray(authPermissionDO.getOperationTags(), String.class);
        functionPermissionVO.setAuthOperationTags(operationTags);
        // 视图权限
        AuthDetailViewVO authDetailViewVO = new AuthDetailViewVO();
        authDetailViewVO.setIsAllViewsAllowed(authPermissionDO.getIsAllViewsAllowed());
        authDetailViewVO.setAuthViews(queryAuthViews(reqVO.getMenuId(), reqVO));
        functionPermissionVO.setAuthViewVO(authDetailViewVO);

        return functionPermissionVO;
    }


    @Override
    public AuthDetailDataPermissionVO getDataPermission(AuthPermissionReq reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        reqVO.setRoleUuid(appAuthRoleDO.getRoleUuid());
        reqVO.setRoleCode(appAuthRoleDO.getRoleCode());
        reqVO.setRoleType(appAuthRoleDO.getRoleType());
        //
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        reqVO.setMenuUuid(appMenuDO.getMenuUuid());
        //
        AuthDetailDataPermissionVO dataPermissionVO = new AuthDetailDataPermissionVO();
        //数据权限
        List<AppAuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(reqVO);
        if (CollectionUtils.isEmpty(authDataGroupDOS)) {
            AppAuthDataGroupDO authDataGroupDO = AuthDefaultFactory.createDefaultAuthDataGroupDO(reqVO);
            authDataGroupRepository.save(authDataGroupDO);
            authDataGroupDOS = List.of(authDataGroupDO);
        }
        List<AuthDataGroupVO> authDataGroupVOS = authDataGroupDOS.stream().map(authDataGroupDO -> {
            AuthDataGroupVO authDataGroupVO = BeanUtils.toBean(authDataGroupDO, AuthDataGroupVO.class);

            if (StringUtils.isNotBlank(authDataGroupDO.getScopeTags())) {
                authDataGroupVO.setScopeTags(JsonUtils.parseArray(authDataGroupDO.getScopeTags(), String.class));
            } else {
                authDataGroupVO.setScopeTags(Collections.emptyList());
            }
            if (StringUtils.isNotBlank(authDataGroupDO.getDataFilter())) {
                authDataGroupVO.setDataFilters(JsonUtils.parseObject(authDataGroupDO.getDataFilter(), new TypeReference<List<List<AuthDataFilterVO>>>() {
                }));
            } else {
                authDataGroupVO.setDataFilters(Collections.emptyList());
            }
            if (StringUtils.isNotBlank(authDataGroupDO.getOperationTags())) {
                authDataGroupVO.setOperationTags(JsonUtils.parseArray(authDataGroupDO.getOperationTags(), String.class));
            } else {
                authDataGroupVO.setOperationTags(Collections.emptyList());
            }
            return authDataGroupVO;
        }).toList();
        dataPermissionVO.setAuthDataGroups(authDataGroupVOS);
        return dataPermissionVO;
    }

    @Override
    public AuthDetailFieldPermissionVO getFieldPermission(AuthPermissionReq reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        reqVO.setRoleUuid(appAuthRoleDO.getRoleUuid());
        reqVO.setMenuUuid(appMenuDO.getMenuUuid());
        //
        String entityUuid = appMenuDO.getEntityUuid();
        //
        AuthDetailFieldPermissionVO fieldPermissionVO = new AuthDetailFieldPermissionVO();
        AppAuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createDefaultAuthPermissionDO(reqVO);
        }
        fieldPermissionVO.setIsAllFieldsAllowed(authPermissionDO.getIsAllFieldsAllowed());
        Pair<List<AuthFieldVO>, List<AuthFieldVO>> fieldDOPair = queryAuthFields(entityUuid, reqVO);
        fieldPermissionVO.setAuthFieldsRD(fieldDOPair.getLeft());
        fieldPermissionVO.setAuthFieldsDL(fieldDOPair.getRight());
        return fieldPermissionVO;
    }

    @Override
    public void updatePageAllowed(AuthUpdatePageAllowedReqVO reqVO) {
        //
        AuthPermissionReq permissionReq = reqVO.getPermissionReq();
        appCommonService.validateApplicationExist(permissionReq.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(permissionReq.getRoleId());
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(permissionReq.getMenuId());
        permissionReq.setRoleUuid(appAuthRoleDO.getRoleUuid());
        permissionReq.setMenuUuid(appMenuDO.getMenuUuid());
        //
        AppAuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionReq);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createDefaultAuthPermissionDO(permissionReq);
            authPermissionDO.setIsPageAllowed(reqVO.getIsPageAllowed());
            authPermissionRepository.save(authPermissionDO);
        } else {
            authPermissionDO.setIsPageAllowed(reqVO.getIsPageAllowed());
            authPermissionRepository.updateById(authPermissionDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOperation(AuthUpdateOperationReqVO reqVO) {
        //
        AuthPermissionReq permissionReq = reqVO.getPermissionReq();
        appCommonService.validateApplicationExist(permissionReq.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(permissionReq.getRoleId());
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(permissionReq.getMenuId());
        permissionReq.setRoleUuid(appAuthRoleDO.getRoleUuid());
        permissionReq.setMenuUuid(appMenuDO.getMenuUuid());
        //
        AppAuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionReq);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createDefaultAuthPermissionDO(permissionReq);
            authPermissionDO.setOperationTags(JsonUtils.toJsonString(reqVO.getOperationTags()));
            authPermissionRepository.save(authPermissionDO);
        } else {
            authPermissionDO.setOperationTags(JsonUtils.toJsonString(reqVO.getOperationTags()));
            authPermissionRepository.updateById(authPermissionDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataGroup(AuthUpdateDataGroupReqVO reqVO) {
        //
        AuthPermissionReq permissionReq = reqVO.getPermissionReq();
        appCommonService.validateApplicationExist(permissionReq.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(permissionReq.getRoleId());
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(permissionReq.getMenuId());
        permissionReq.setRoleUuid(appAuthRoleDO.getRoleUuid());
        permissionReq.setMenuUuid(appMenuDO.getMenuUuid());
        //
        Long dataGroupId = reqVO.getAuthDataGroup().getId();
        if (dataGroupId == null || dataGroupId <= 0) {
            AppAuthDataGroupDO authDataGroupDO = new AppAuthDataGroupDO();
            authDataGroupDO.setApplicationId(permissionReq.getApplicationId());
            authDataGroupDO.setRoleUuid(permissionReq.getRoleUuid());
            authDataGroupDO.setMenuUuid(permissionReq.getMenuUuid());
            BeanUtils.copyProperties(reqVO.getAuthDataGroup(), authDataGroupDO);
            if (reqVO.getAuthDataGroup().getScopeTags() != null) {
                authDataGroupDO.setScopeTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getScopeTags()));
            }
            if (reqVO.getAuthDataGroup().getOperationTags() != null) {
                authDataGroupDO.setOperationTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getOperationTags()));
            }
            if (reqVO.getAuthDataGroup().getDataFilters() != null) {
                authDataGroupDO.setDataFilter(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getDataFilters()));
            }
            if (!reqVO.getAuthDataGroup().getScopeTags().contains("customCondition")) {
                authDataGroupDO.setScopeFieldUuid(null);
                authDataGroupDO.setScopeLevel(null);
                authDataGroupDO.setScopeValue(null);
            }
            authDataGroupRepository.save(authDataGroupDO);
        } else {
            AppAuthDataGroupDO authDataGroupDO = authDataGroupRepository.getById(dataGroupId);
            BeanUtils.copyProperties(reqVO.getAuthDataGroup(), authDataGroupDO);
            if (reqVO.getAuthDataGroup().getScopeTags() != null) {
                authDataGroupDO.setScopeTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getScopeTags()));
            }
            if (reqVO.getAuthDataGroup().getOperationTags() != null) {
                authDataGroupDO.setOperationTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getOperationTags()));
            }
            if (reqVO.getAuthDataGroup().getDataFilters() != null) {
                authDataGroupDO.setDataFilter(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getDataFilters()));
            }
            if (!reqVO.getAuthDataGroup().getScopeTags().contains("customCondition")) {
                authDataGroupDO.setScopeFieldUuid(null);
                authDataGroupDO.setScopeLevel(null);
                authDataGroupDO.setScopeValue(null);
            }
            authDataGroupRepository.updateById(authDataGroupDO, false);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataGroup(Long id) {
        AppAuthDataGroupDO dataGroupDO = authDataGroupRepository.getById(id);
        authDataGroupRepository.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateField(AuthUpdateFieldReqVO reqVO) {
        //
        AuthPermissionReq permissionReq = reqVO.getPermissionReq();
        appCommonService.validateApplicationExist(permissionReq.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(permissionReq.getRoleId());
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(permissionReq.getMenuId());
        permissionReq.setRoleUuid(appAuthRoleDO.getRoleUuid());
        permissionReq.setMenuUuid(appMenuDO.getMenuUuid());
        //
        AppAuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionReq);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createDefaultAuthPermissionDO(permissionReq);
            authPermissionDO.setIsAllFieldsAllowed(reqVO.getIsAllFieldsAllowed());
            authPermissionRepository.save(authPermissionDO);
        } else {
            authPermissionDO.setIsAllFieldsAllowed(reqVO.getIsAllFieldsAllowed());
            authPermissionRepository.updateById(authPermissionDO);
        }
        if (authPermissionDO != null && reqVO.getIsAllFieldsAllowed() == 1) {
            authFieldRepository.deleteByQueryRequest(permissionReq);
        }
        if (authPermissionDO != null && reqVO.getIsAllFieldsAllowed() == 0 && CollectionUtils.isNotEmpty(reqVO.getAuthFields())) {
            for (AuthFieldVO authField : reqVO.getAuthFields()) {
                upsetAuthField(permissionReq, authField);
            }
        }
    }

    private void upsetAuthField(AuthPermissionReq permissionReq, AuthFieldVO authFieldVO) {
        AppAuthFieldDO authFieldDO = null;
        if (authFieldVO.getId() != null) {
            authFieldDO = authFieldRepository.getById(authFieldVO.getId());
        }
        if (authFieldDO == null) {
            authFieldDO = new AppAuthFieldDO();
            authFieldDO.setApplicationId(permissionReq.getApplicationId());
            authFieldDO.setRoleUuid(permissionReq.getRoleUuid());
            authFieldDO.setMenuUuid(permissionReq.getMenuUuid());
            authFieldDO.setFieldUuid(authFieldVO.getFieldUuid());
            authFieldDO.setIsCanRead(authFieldVO.getIsCanRead());
            authFieldDO.setIsCanEdit(authFieldVO.getIsCanEdit());
            authFieldDO.setIsCanDownload(authFieldVO.getIsCanDownload());
            authFieldRepository.save(authFieldDO);
        } else {
            authFieldDO.setIsCanRead(authFieldVO.getIsCanRead());
            authFieldDO.setIsCanEdit(authFieldVO.getIsCanEdit());
            authFieldDO.setIsCanDownload(authFieldVO.getIsCanDownload());
            authFieldRepository.updateById(authFieldDO);
        }
    }

    @Override
    public void updateView(AuthUpdateViewReqVO reqVO) {
        //
        AuthPermissionReq permissionReq = reqVO.getPermissionReq();
        appCommonService.validateApplicationExist(permissionReq.getApplicationId());
        AppAuthRoleDO appAuthRoleDO = appCommonService.validateRoleExist(permissionReq.getRoleId());
        AppMenuDO appMenuDO = appCommonService.validateMenuExist(permissionReq.getMenuId());
        permissionReq.setRoleUuid(appAuthRoleDO.getRoleUuid());
        permissionReq.setMenuUuid(appMenuDO.getMenuUuid());
        //
        AppAuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionReq);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createDefaultAuthPermissionDO(permissionReq);
            authPermissionDO.setIsAllViewsAllowed(reqVO.getIsAllViewsAllowed());
            authPermissionRepository.save(authPermissionDO);
        } else {
            authPermissionDO.setIsAllViewsAllowed(reqVO.getIsAllViewsAllowed());
            authPermissionRepository.updateById(authPermissionDO);
        }
        if (authPermissionDO != null && reqVO.getIsAllViewsAllowed() == 1) {
            authViewRepository.deleteByQuery(permissionReq);
        }
        if (authPermissionDO != null && reqVO.getIsAllViewsAllowed() == 0 && CollectionUtils.isNotEmpty(reqVO.getAuthViews())) {
            for (AuthViewVO authView : reqVO.getAuthViews()) {
                upsetViewField(permissionReq, authView);
            }
        }
    }


    private void upsetViewField(AuthPermissionReq permissionReq, AuthViewVO authViewVO) {
        AppAuthViewDO authViewDO = null;
        if (authViewVO.getId() != null) {
            authViewDO = authViewRepository.getById(authViewVO.getId());
        } else if (authViewVO.getViewUuid() != null) {
            authViewDO = authViewRepository.findByAppIdAndUuid(permissionReq.getApplicationId(), authViewVO.getViewUuid());
        }
        if (authViewDO == null) {
            authViewDO = new AppAuthViewDO();
            authViewDO.setApplicationId(permissionReq.getApplicationId());
            authViewDO.setRoleUuid(permissionReq.getRoleUuid());
            authViewDO.setMenuUuid(permissionReq.getMenuUuid());
            authViewDO.setViewUuid(authViewVO.getViewUuid());
            authViewDO.setIsAllowed(authViewVO.getIsAllowed());
            authViewRepository.save(authViewDO);
        } else {
            authViewDO.setViewUuid(authViewVO.getViewUuid());
            authViewDO.setIsAllowed(authViewVO.getIsAllowed());
            authViewRepository.updateById(authViewDO);
        }
    }


    private List<AuthViewVO> queryAuthViews(Long menuId, AuthPermissionReq reqVO) {
        List<AppAuthViewDO> viewDOS = authViewRepository.findByQuery(reqVO);
        List<AppResourcePageDO> pages = appPageRepository.findPagesByMenuId(menuId);
        List<Pair<AppResourcePageDO, AppAuthViewDO>> pairs =
                AuthUtils.leftOuterJoin(pages, viewDOS, (page, view) -> page.getPageUuid().equals(view.getViewUuid()));
        return pairs.stream().map(pair -> {
            AppResourcePageDO pageDO = pair.getLeft();
            AppAuthViewDO viewDO = pair.getRight();
            AuthViewVO authViewVO = new AuthViewVO();
            authViewVO.setViewUuid(pageDO.getPageUuid());
            authViewVO.setViewDisplayName(pageDO.getPageName());
            if (viewDO != null) {
                authViewVO.setIsAllowed(viewDO.getIsAllowed());
            }
            return authViewVO;
        }).toList();
    }

    private Pair<List<AuthFieldVO>, List<AuthFieldVO>> queryAuthFields(String entityUuid, AuthPermissionReq reqVO) {
        List<SemanticFieldSchemaDTO> entityFieldRespDTOS = getEntityFieldRespDTOS(entityUuid);
        List<AppAuthFieldDO> authFieldDOS = authFieldRepository.findByQueryRequest(reqVO);
        List<Pair<SemanticFieldSchemaDTO, AppAuthFieldDO>> pairs = AuthUtils.leftOuterJoin(entityFieldRespDTOS, authFieldDOS,
                (entityFieldRespDTO, authFieldDO) -> Objects.equals(entityFieldRespDTO.getFieldUuid(), authFieldDO.getFieldUuid()));
        List<AuthFieldVO> fieldVOS = pairs.stream().map(pair -> {
            SemanticFieldSchemaDTO entityField = pair.getLeft();
            AppAuthFieldDO authFieldDO = pair.getRight();
            //
            AuthFieldVO authFieldVO = new AuthFieldVO();
            authFieldVO.setFieldUuid(entityField.getFieldUuid());
            authFieldVO.setFieldType(entityField.getFieldType());
            authFieldVO.setFieldDisplayName(entityField.getDisplayName());
            if (authFieldDO != null) {
                authFieldVO.setId(authFieldDO.getId());
                authFieldVO.setIsCanRead(authFieldDO.getIsCanRead());
                authFieldVO.setIsCanEdit(authFieldDO.getIsCanEdit());
                authFieldVO.setIsCanDownload(authFieldDO.getIsCanDownload());
            }
            return authFieldVO;
        }).toList();

        List<AuthFieldVO> authFieldsRD = fieldVOS.stream().filter(fieldVO ->
                !StringUtils.equalsIgnoreCase(fieldVO.getFieldType(), "FILE")).toList();

        List<AuthFieldVO> authFieldsDL = fieldVOS.stream().filter(fieldVO ->
                StringUtils.equalsIgnoreCase(fieldVO.getFieldType(), "FILE")).toList();
        return Pair.of(authFieldsRD, authFieldsDL);
    }

    private List<SemanticFieldSchemaDTO> getEntityFieldRespDTOS(String entityUuid) {
        SemanticEntitySchemaDTO semanticEntitySchemaDTO = semanticDynamicDataApi.buildEntitySchemaByUuid(entityUuid);
        return semanticEntitySchemaDTO.getFields();

    }


}
