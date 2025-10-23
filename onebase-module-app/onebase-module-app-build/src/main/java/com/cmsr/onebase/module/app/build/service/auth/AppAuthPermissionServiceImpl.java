package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import com.cmsr.onebase.module.app.core.enums.auth.AuthPermissionScopeEnum;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReqVO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:57
 */
@Setter
@Service
public class AppAuthPermissionServiceImpl implements AppAuthPermissionService {

    @Resource
    private AppAuthDataFilterRepository authDataFilterRepository;

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

    @Override
    public AuthDetailFunctionPermissionVO getFunctionPermission(AuthPermissionReqVO reqVO) {
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
    public AuthDetailDataPermissionVO getDataPermission(AuthPermissionReqVO reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        appCommonService.validateRoleExist(reqVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        Long entityId = menuDO.getEntityId();

        //
        AuthDetailDataPermissionVO dataPermissionVO = new AuthDetailDataPermissionVO();
        //数据权限


        List<AuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(reqVO);
        if (CollectionUtils.isEmpty(authDataGroupDOS)) {
            authDataGroupDOS = AuthDefaultFactory.createListAuthDataGroupDOList(reqVO);
        }
        List<AuthDataGroupVO> authDataGroupVOS = authDataGroupDOS.stream().map(authDataGroupDO -> {
            AuthDataGroupVO authDataGroupVO = BeanUtils.toBean(authDataGroupDO, AuthDataGroupVO.class);
            authDataGroupVO.setScopeTags(JsonUtils.parseObject(authDataGroupDO.getScopeTags(), new TypeReference<List<String>>() {
            }));
            authDataGroupVO.setOperationTags(JsonUtils.parseObject(authDataGroupDO.getOperationTags(), new TypeReference<List<String>>() {
            }));
            return authDataGroupVO;
        }).toList();

        for (AuthDataGroupVO authDataGroupVO : authDataGroupVOS) {
            List<AuthDataFilterDO> dataFilterDOS = authDataFilterRepository.findByGroupId(authDataGroupVO.getId());
            List<AuthDataFilterVO> dataFilterVOS = BeanUtils.toBean(dataFilterDOS, AuthDataFilterVO.class);
            List<List<AuthDataFilterVO>> dataFilters = groupAndOrder(dataFilterVOS);
            authDataGroupVO.setDataFilters(dataFilters);
        }
        dataPermissionVO.setAuthDataGroups(authDataGroupVOS);
        dataPermissionVO.setScopeFields(queryScopeFields(entityId));
        dataPermissionVO.setDataFilterFields(queryDataFilterFields(entityId));
        //补充全部的字段名称属性
        return dataPermissionVO;
    }

    private List<EntityFieldVO> queryScopeFields(Long entityId) {
        EntityFieldQueryReqDTO reqDTO = new EntityFieldQueryReqDTO();
        reqDTO.setEntityId(entityId);
        reqDTO.setIsPerson(NumberUtils.INTEGER_ONE);
        List<EntityFieldRespDTO> entityFieldsByIds = metadataEntityFieldApi.getEntityFieldList(reqDTO);
        return entityFieldsByIds.stream().map(entityFieldRespDTO -> {
            EntityFieldVO entityFieldVO = new EntityFieldVO();
            entityFieldVO.setId(entityFieldRespDTO.getId());
            entityFieldVO.setDisplayName(entityFieldRespDTO.getDisplayName());
            return entityFieldVO;
        }).collect(Collectors.toList());
    }

    private List<EntityFieldVO> queryDataFilterFields(Long entityId) {
        EntityFieldQueryReqDTO reqDTO = new EntityFieldQueryReqDTO();
        reqDTO.setEntityId(entityId);
        reqDTO.setIsSystemField(NumberUtils.INTEGER_ZERO);
        List<EntityFieldRespDTO> entityFieldsByIds = metadataEntityFieldApi.getEntityFieldList(reqDTO);
        return entityFieldsByIds.stream().map(entityFieldRespDTO -> {
            EntityFieldVO entityFieldVO = new EntityFieldVO();
            entityFieldVO.setId(entityFieldRespDTO.getId());
            entityFieldVO.setDisplayName(entityFieldRespDTO.getDisplayName());
            return entityFieldVO;
        }).collect(Collectors.toList());
    }


    @Override
    public AuthDetailFieldPermissionVO getFieldPermission(AuthPermissionReqVO reqVO) {
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
            authDataGroupDO.setScopeTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getScopeTags()));
            authDataGroupDO.setOperationTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getOperationTags()));
            authDataGroupRepository.insert(authDataGroupDO);
            //
            updateAuthDataFilter(dataGroupId, reqVO.getAuthDataGroup().getDataFilters());
        } else {
            AuthDataGroupDO authDataGroupDO = authDataGroupRepository.findById(dataGroupId);
            BeanUtils.copyProperties(reqVO.getAuthDataGroup(), authDataGroupDO);
            authDataGroupDO.setScopeTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getScopeTags()));
            authDataGroupDO.setOperationTags(JsonUtils.toJsonString(reqVO.getAuthDataGroup().getOperationTags()));
            authDataGroupRepository.update(authDataGroupDO);
            //
            updateAuthDataFilter(dataGroupId, reqVO.getAuthDataGroup().getDataFilters());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataGroup(Long id) {
        authDataGroupRepository.deleteById(id);
        authDataFilterRepository.deleteByGroupId(id);
    }

    @Override
    public List<AuthPermissionScope> getPermissionScope() {
        return Arrays.stream(AuthPermissionScopeEnum.values()).map(v -> {
            AuthPermissionScope scope = new AuthPermissionScope();
            scope.setLabel(v.getLabel());
            scope.setValue(v.getCode());
            return scope;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateField(AuthUpdateFieldReqVO reqVO) {
        // IsAllFieldsAllowed 1 所有字段内容可操作   0 自定义权限
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
    }

    private void upsetAuthField(AuthPermissionReqVO permissionReq, AuthFieldVO authField) {
        AuthFieldDO authFieldDO = null;
        if (authField.getId() != null) {
            authFieldDO = authFieldRepository.findById(authField.getId());
        }
        if (authFieldDO == null) {
            authFieldDO = authFieldRepository.findByQuery(permissionReq, authField.getId());
        }
        if (authFieldDO == null) {
            authFieldDO = new AuthFieldDO();
            authFieldDO.setApplicationId(permissionReq.getApplicationId());
            authFieldDO.setRoleId(permissionReq.getRoleId());
            authFieldDO.setMenuId(permissionReq.getMenuId());
            authFieldDO.setFieldId(authField.getFieldId());
            authFieldDO.setIsCanRead(authField.getIsCanRead());
            authFieldDO.setIsCanEdit(authField.getIsCanEdit());
            authFieldDO.setIsCanDownload(authField.getIsCanDownload());
            authFieldRepository.insert(authFieldDO);
        } else {
            authFieldDO.setFieldId(authField.getFieldId());
            authFieldDO.setIsCanRead(authField.getIsCanRead());
            authFieldDO.setIsCanEdit(authField.getIsCanEdit());
            authFieldDO.setIsCanDownload(authField.getIsCanDownload());
            authFieldRepository.update(authFieldDO);
        }
    }

    @Override
    public void updateView(AuthUpdateViewReqVO reqVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO.getPermissionReq());
        if (authPermissionDO == null) {
            authPermissionDO = AuthDefaultFactory.createAuthPermissionDO(reqVO.getPermissionReq());
            authPermissionDO.setIsAllFieldsAllowed(reqVO.getIsAllViewsAllowed());
            authPermissionRepository.insert(authPermissionDO);
        } else {
            authPermissionDO.setIsAllFieldsAllowed(reqVO.getIsAllViewsAllowed());
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
    }


    private void upsetViewField(AuthPermissionReqVO permissionReq, AuthViewVO authViewVO) {
        AuthViewDO authViewDO = null;
        if (authViewVO.getId() != null) {
            authViewDO = authViewRepository.findById(authViewVO.getId());
        }
        if (authViewDO == null) {
            authViewDO = authViewRepository.findByQuery(permissionReq, authViewVO.getId());
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


    private List<AuthViewVO> queryAuthViews(Long menuId, AuthPermissionReqVO reqVO) {
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
        List<PageSetDO> pageSetDOS = appPageSetRepository.findByMenuId(List.of(menuId));
        if (CollectionUtils.isEmpty(pageSetDOS)) {
            return Collections.emptyList();
        }
        List<Long> pageSetIds = pageSetDOS.stream().map(pageSetDO -> pageSetDO.getId()).toList();
        List<PageSetPageDO> pageSetPageDOS = appPageSetPageRepository.findByPageSetIds(pageSetIds);
        if (CollectionUtils.isEmpty(pageSetPageDOS)) {
            return Collections.emptyList();
        }
        List<Long> pageIds = pageSetPageDOS.stream().map(pageSetPageDO -> pageSetPageDO.getPageId()).toList();
        List<PageDO> pageDOS = appPageRepository.findByPageIds(pageIds);
        if (CollectionUtils.isEmpty(pageDOS)) {
            return Collections.emptyList();
        }
        return pageDOS.stream().filter(pageDO -> "form".equals(pageDO.getPageType())).toList();
    }

    /**
     * 根据 conditionGroup的值分组，值相同的在一组，conditionGroup的值小的在前面
     * 每个组再根据 conditionOrder 排序，conditionOrder的值小的在前面
     *
     * @param dataFilterVOS
     * @return
     */
    private List<List<AuthDataFilterVO>> groupAndOrder(List<AuthDataFilterVO> dataFilterVOS) {
        if (dataFilterVOS == null || dataFilterVOS.isEmpty()) {
            return new ArrayList<>();
        }
        return dataFilterVOS.stream()
                .sorted(Comparator.comparing(AuthDataFilterVO::getConditionGroup)
                        .thenComparing(AuthDataFilterVO::getConditionOrder))
                .collect(Collectors.groupingBy(
                        AuthDataFilterVO::getConditionGroup,
                        LinkedHashMap::new,
                        Collectors.toList()))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    private Pair<List<AuthFieldVO>, List<AuthFieldVO>> queryAuthFields(Long entityId, AuthPermissionReqVO reqVO) {
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


    private void updateAuthDataFilter(Long groupId, List<List<AuthDataFilterVO>> authDataFilters) {
        if (authDataFilters == null || authDataFilters.isEmpty()) {
            authDataFilterRepository.deleteByGroupId(groupId);
        }
        List<AuthDataFilterDO> authDataFilterDOS = authDataFilterRepository.findByGroupId(groupId);
        List<AuthDataFilterVO> authDataFilterVOS = formatAuthDataFilterVO(authDataFilters);
        List<Pair<AuthDataFilterDO, AuthDataFilterVO>> pairs = AuthUtils.fullOuterJoin(authDataFilterDOS, authDataFilterVOS,
                (authDataFilterDO, authDataFilterVO) -> authDataFilterDO.getId().equals(authDataFilterVO.getId())
        );
        for (Pair<AuthDataFilterDO, AuthDataFilterVO> pair : pairs) {
            AuthDataFilterDO authDataFilterDO = pair.getLeft();
            AuthDataFilterVO authDataFilterVO = pair.getRight();
            if (authDataFilterDO != null && authDataFilterVO != null) {
                copyFields(groupId, authDataFilterVO, authDataFilterDO);
                authDataFilterRepository.update(authDataFilterDO);
            } else if (authDataFilterDO == null && authDataFilterVO != null) {
                authDataFilterDO = new AuthDataFilterDO();
                copyFields(groupId, authDataFilterVO, authDataFilterDO);
                authDataFilterRepository.insert(authDataFilterDO);
            } else if (authDataFilterDO != null && authDataFilterVO == null) {
                authDataFilterRepository.deleteById(authDataFilterDO.getId());
            }
        }
    }

    private void copyFields(Long groupId, AuthDataFilterVO authDataFilterVO, AuthDataFilterDO authDataFilterDO) {
        authDataFilterDO.setGroupId(groupId);
        authDataFilterDO.setConditionGroup(authDataFilterVO.getConditionGroup());
        authDataFilterDO.setConditionOrder(authDataFilterVO.getConditionOrder());
        authDataFilterDO.setFieldId(authDataFilterVO.getFieldId());
        authDataFilterDO.setFieldValueType(authDataFilterVO.getFieldValueType());
        authDataFilterDO.setFieldOperator(authDataFilterVO.getFieldOperator());
        authDataFilterDO.setFieldValue(authDataFilterVO.getFieldValue());
    }

    private List<AuthDataFilterVO> formatAuthDataFilterVO(List<List<AuthDataFilterVO>> authDataFilters) {
        if (CollectionUtils.isEmpty(authDataFilters)) {
            return Collections.emptyList();
        }
        for (int i = 0; i < authDataFilters.size(); i++) {
            List<AuthDataFilterVO> authDataFilterVOS = authDataFilters.get(i);
            for (int j = 0; j < authDataFilterVOS.size(); j++) {
                AuthDataFilterVO authDataFilterVO = authDataFilterVOS.get(j);
                authDataFilterVO.setConditionGroup(i + 1);
                authDataFilterVO.setConditionOrder(j + 1);
            }
        }
        return authDataFilters.stream().flatMap(Collection::stream).toList();
    }


}
