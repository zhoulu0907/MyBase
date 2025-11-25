package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthViewRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthViewDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import com.cmsr.onebase.module.app.core.provider.AppCacheProvider;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

    @Resource
    private AppAuthDataGroupRepository authDataGroupRepository;

    @Resource
    private AppAuthPermissionRepository authPermissionRepository;

    @Resource
    private AppAuthFieldRepository authFieldRepository;

    @Resource
    private AppAuthViewRepository authViewRepository;

    @Resource
    private AppPageSetRepository appPageSetRepository;

    @Resource
    private AppPageSetPageRepository appPageSetPageRepository;

    @Resource
    private AppPageRepository appPageRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Resource
    private AppCacheProvider appCacheProvider;

    @Override
    public AuthDetailFunctionPermissionVO getFunctionPermission(AuthPermissionReq reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        appCommonService.validateRoleExist(reqVO.getRoleId());
        appCommonService.validateMenuExist(reqVO.getMenuId());
        //
        AuthDetailFunctionPermissionVO functionPermissionVO = new AuthDetailFunctionPermissionVO();
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createAuthPermissionDO(reqVO);
        }
        // 页面权限
        functionPermissionVO.setIsPageAllowed(authPermissionDO.getIsPageAllowed());
        // 操作权限
        List<String> operationTags = JsonUtils.parseObject(authPermissionDO.getOperationTags(), new TypeReference<List<String>>() {
        });
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
        appCommonService.validateRoleExist(reqVO.getRoleId());
        //
        AuthDetailDataPermissionVO dataPermissionVO = new AuthDetailDataPermissionVO();
        //数据权限
        List<AuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(reqVO);
        if (CollectionUtils.isEmpty(authDataGroupDOS)) {
            AuthDataGroupDO authDataGroupDO = AuthDefaultFactory.createAuthDataGroupDO(reqVO);
            authDataGroupRepository.insert(authDataGroupDO);
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
        appCommonService.validateRoleExist(reqVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        Long entityId = menuDO.getEntityId();
        //
        AuthDetailFieldPermissionVO fieldPermissionVO = new AuthDetailFieldPermissionVO();
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO);
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createAuthPermissionDO(reqVO);
        }
        fieldPermissionVO.setIsAllFieldsAllowed(authPermissionDO.getIsAllFieldsAllowed());
        Pair<List<AuthFieldVO>, List<AuthFieldVO>> fieldDOPair = queryAuthFields(entityId, reqVO);
        fieldPermissionVO.setAuthFieldsRD(fieldDOPair.getLeft());
        fieldPermissionVO.setAuthFieldsDL(fieldDOPair.getRight());
        return fieldPermissionVO;
    }

    @Override
    public void updatePageAllowed(AuthUpdatePageAllowedReqVO reqVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO.getPermissionReq());
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createAuthPermissionDO(reqVO.getPermissionReq());
            authPermissionDO.setIsPageAllowed(reqVO.getIsPageAllowed());
            authPermissionRepository.insert(authPermissionDO);
        } else {
            authPermissionDO.setIsPageAllowed(reqVO.getIsPageAllowed());
            authPermissionRepository.update(authPermissionDO);
        }
        appCacheProvider.roleMenuChanged(reqVO.getPermissionReq());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOperation(AuthUpdateOperationReqVO reqVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO.getPermissionReq());
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createAuthPermissionDO(reqVO.getPermissionReq());
            authPermissionDO.setOperationTags(JsonUtils.toJsonString(reqVO.getOperationTags()));
            authPermissionRepository.insert(authPermissionDO);
        } else {
            authPermissionDO.setOperationTags(JsonUtils.toJsonString(reqVO.getOperationTags()));
            authPermissionRepository.update(authPermissionDO);
        }
        appCacheProvider.roleMenuChanged(reqVO.getPermissionReq());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataGroup(AuthUpdateDataGroupReqVO reqVO) {
        Long dataGroupId = reqVO.getAuthDataGroup().getId();
        if (dataGroupId == null || dataGroupId <= 0) {
            AuthDataGroupDO authDataGroupDO = new AuthDataGroupDO();
            authDataGroupDO.setApplicationId(reqVO.getPermissionReq().getApplicationId());
            authDataGroupDO.setRoleId(reqVO.getPermissionReq().getRoleId());
            authDataGroupDO.setMenuId(reqVO.getPermissionReq().getMenuId());
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
                authDataGroupDO.setScopeFieldId(null);
                authDataGroupDO.setScopeLevel(null);
                authDataGroupDO.setScopeValue(null);
            }
            authDataGroupRepository.insert(authDataGroupDO);
        } else {
            AuthDataGroupDO authDataGroupDO = authDataGroupRepository.findById(dataGroupId);
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
                authDataGroupDO.setScopeFieldId(null);
                authDataGroupDO.setScopeLevel(null);
                authDataGroupDO.setScopeValue(null);
            }
            authDataGroupRepository.updateAuthDataGroup(authDataGroupDO);
        }
        appCacheProvider.roleMenuChanged(reqVO.getPermissionReq());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataGroup(Long id) {
        AuthDataGroupDO dataGroupDO = authDataGroupRepository.findById(id);
        authDataGroupRepository.deleteById(id);
        appCacheProvider.roleMenuChanged(dataGroupDO.getApplicationId(), dataGroupDO.getRoleId(), dataGroupDO.getMenuId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateField(AuthUpdateFieldReqVO reqVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO.getPermissionReq());
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createAuthPermissionDO(reqVO.getPermissionReq());
            authPermissionDO.setIsAllFieldsAllowed(reqVO.getIsAllFieldsAllowed());
            authPermissionRepository.insert(authPermissionDO);
        } else {
            authPermissionDO.setIsAllFieldsAllowed(reqVO.getIsAllFieldsAllowed());
            authPermissionRepository.update(authPermissionDO);
        }
        if (authPermissionDO != null && reqVO.getIsAllFieldsAllowed() == 1) {
            authFieldRepository.deleteByQuery(reqVO.getPermissionReq());
        }
        if (authPermissionDO != null && reqVO.getIsAllFieldsAllowed() == 0 && CollectionUtils.isNotEmpty(reqVO.getAuthFields())) {
            for (AuthFieldVO authField : reqVO.getAuthFields()) {
                upsetAuthField(reqVO.getPermissionReq(), authField);
            }
        }
        appCacheProvider.roleMenuChanged(reqVO.getPermissionReq());
    }

    private void upsetAuthField(AuthPermissionReq permissionReq, AuthFieldVO authFieldVO) {
        AuthFieldDO authFieldDO = null;
        if (authFieldVO.getId() != null) {
            authFieldDO = authFieldRepository.findById(authFieldVO.getId());
        }
        if (authFieldDO == null) {
            authFieldDO = authFieldRepository.findByQuery(permissionReq, authFieldVO.getFieldId());
        }
        if (authFieldDO == null) {
            authFieldDO = new AuthFieldDO();
            authFieldDO.setApplicationId(permissionReq.getApplicationId());
            authFieldDO.setRoleId(permissionReq.getRoleId());
            authFieldDO.setMenuId(permissionReq.getMenuId());
            authFieldDO.setFieldId(authFieldVO.getFieldId());
            authFieldDO.setIsCanRead(authFieldVO.getIsCanRead());
            authFieldDO.setIsCanEdit(authFieldVO.getIsCanEdit());
            authFieldDO.setIsCanDownload(authFieldVO.getIsCanDownload());
            authFieldRepository.insert(authFieldDO);
        } else {
            authFieldDO.setIsCanRead(authFieldVO.getIsCanRead());
            authFieldDO.setIsCanEdit(authFieldVO.getIsCanEdit());
            authFieldDO.setIsCanDownload(authFieldVO.getIsCanDownload());
            authFieldRepository.update(authFieldDO);
        }
    }

    @Override
    public void updateView(AuthUpdateViewReqVO reqVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO.getPermissionReq());
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createAuthPermissionDO(reqVO.getPermissionReq());
            authPermissionDO.setIsAllViewsAllowed(reqVO.getIsAllViewsAllowed());
            authPermissionRepository.insert(authPermissionDO);
        } else {
            authPermissionDO.setIsAllViewsAllowed(reqVO.getIsAllViewsAllowed());
            authPermissionRepository.update(authPermissionDO);
        }
        if (authPermissionDO != null && reqVO.getIsAllViewsAllowed() == 1) {
            authViewRepository.deleteByQuery(reqVO.getPermissionReq());
        }
        if (authPermissionDO != null && reqVO.getIsAllViewsAllowed() == 0 && CollectionUtils.isNotEmpty(reqVO.getAuthViews())) {
            for (AuthViewVO authView : reqVO.getAuthViews()) {
                upsetViewField(reqVO.getPermissionReq(), authView);
            }
        }
        appCacheProvider.roleMenuChanged(reqVO.getPermissionReq());
    }


    private void upsetViewField(AuthPermissionReq permissionReq, AuthViewVO authViewVO) {
        AuthViewDO authViewDO = null;
        if (authViewVO.getId() != null) {
            authViewDO = authViewRepository.findById(authViewVO.getId());
        }
        if (authViewDO == null) {
            authViewDO = authViewRepository.findByQuery(permissionReq, authViewVO.getViewId());
        }
        if (authViewDO == null) {
            authViewDO = new AuthViewDO();
            authViewDO.setApplicationId(permissionReq.getApplicationId());
            authViewDO.setRoleId(permissionReq.getRoleId());
            authViewDO.setMenuId(permissionReq.getMenuId());
            authViewDO.setViewId(authViewVO.getViewId());
            authViewDO.setIsAllowed(authViewVO.getIsAllowed());
            authViewRepository.insert(authViewDO);
        } else {
            authViewDO.setViewId(authViewVO.getViewId());
            authViewDO.setIsAllowed(authViewVO.getIsAllowed());
            authViewRepository.update(authViewDO);
        }
    }


    private List<AuthViewVO> queryAuthViews(Long menuId, AuthPermissionReq reqVO) {
        List<AuthViewDO> viewDOS = authViewRepository.findByQuery(reqVO);
        List<PageDO> pages = queryAlFormPages(menuId);
        List<Pair<PageDO, AuthViewDO>> pairs = AuthUtils.leftOuterJoin(pages, viewDOS, (page, view) -> page.getId().equals(view.getViewId()));
        return pairs.stream().map(pair -> {
            PageDO pageDO = pair.getLeft();
            AuthViewDO viewDO = pair.getRight();
            AuthViewVO authViewVO = new AuthViewVO();
            authViewVO.setViewId(pageDO.getId());
            authViewVO.setViewDisplayName(pageDO.getPageName());
            if (viewDO != null) {
                authViewVO.setIsAllowed(viewDO.getIsAllowed());
            }
            return authViewVO;
        }).toList();
    }


    private List<PageDO> queryAlFormPages(Long menuId) {
        List<PageSetDO> pageSetDOS = appPageSetRepository.findByMenuIds(List.of(menuId));
        if (CollectionUtils.isEmpty(pageSetDOS)) {
            return Collections.emptyList();
        }
        List<Long> pageSetIds = pageSetDOS.stream().map(pageSetDO -> pageSetDO.getId()).toList();
        List<PageSetPageDO> pageSetPageDOS = appPageSetPageRepository.findByPageSetIds(pageSetIds);
        if (CollectionUtils.isEmpty(pageSetPageDOS)) {
            return Collections.emptyList();
        }
        List<Long> pageIds = pageSetPageDOS.stream().map(pageSetPageDO -> pageSetPageDO.getPageId()).toList();
        List<PageDO> pageDOS = appPageRepository.listByIds(pageIds);
        if (CollectionUtils.isEmpty(pageDOS)) {
            return Collections.emptyList();
        }
        return pageDOS.stream().filter(pageDO -> "form".equals(pageDO.getPageType())).toList();
    }

    private Pair<List<AuthFieldVO>, List<AuthFieldVO>> queryAuthFields(Long entityId, AuthPermissionReq reqVO) {
        List<EntityFieldRespDTO> entityFieldRespDTOS = getEntityFieldRespDTOS(entityId);
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(reqVO);
        List<Pair<EntityFieldRespDTO, AuthFieldDO>> pairs = AuthUtils.leftOuterJoin(entityFieldRespDTOS, authFieldDOS,
                (entityFieldRespDTO, authFieldDO) -> Objects.equals(entityFieldRespDTO.getId(), authFieldDO.getFieldId()));
        List<AuthFieldVO> fieldVOS = pairs.stream().map(pair -> {
            EntityFieldRespDTO entityField = pair.getLeft();
            AuthFieldDO authFieldDO = pair.getRight();
            //
            AuthFieldVO authFieldVO = new AuthFieldVO();
            authFieldVO.setFieldId(entityField.getId());
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

    private List<EntityFieldRespDTO> getEntityFieldRespDTOS(Long entityId) {
        EntityFieldQueryReqDTO reqDTO = new EntityFieldQueryReqDTO();
        reqDTO.setEntityId(entityId);
        reqDTO.setIsSystemField(0);
        List<EntityFieldRespDTO> entityFieldRespDTOS = metadataEntityFieldApi.getEntityFieldList(reqDTO);
        return entityFieldRespDTOS;
    }


}
