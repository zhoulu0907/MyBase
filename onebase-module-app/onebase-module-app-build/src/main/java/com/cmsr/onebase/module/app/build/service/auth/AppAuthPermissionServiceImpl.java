package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.auth.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthOperationEnum;
import com.cmsr.onebase.module.app.core.vo.auth.AuthOperationVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReqVO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.api.validation.MetadataPermitApi;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
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
    private AppAuthViewRepository authEntityRepository;

    @Resource
    private AppAuthPermissionRepository authPermissionRepository;

    @Resource
    private AppAuthFieldRepository authFieldRepository;

    @Resource
    private AppAuthOperationRepository authOperationRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Resource
    private MetadataPermitApi metadataPermitApi;

    @Override
    public AuthDetailFunctionPermissionVO getFunctionPermission(AuthPermissionReqVO reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        appCommonService.validateRoleExist(reqVO.getRoleId());
        appCommonService.validateMenuExist(reqVO.getMenuId());
        //
        AuthDetailFunctionPermissionVO functionPermissionVO = new AuthDetailFunctionPermissionVO();
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO);
        // 页面权限
        if (authPermissionDO != null) {
            functionPermissionVO.setIsPageAllowed(authPermissionDO.getIsPageAllowed());
        }
        // 操作权限
        functionPermissionVO.setAuthOperations(queryAuthOperations(reqVO));
        // 视图权限
        AuthDetailViewVO authDetailViewVO = new AuthDetailViewVO();
        if (authPermissionDO != null) {
            authDetailViewVO.setIsAllViewsAllowed(authPermissionDO.getIsAllViewsAllowed());
        }
        authDetailViewVO.setAuthViews(queryAuthViews(reqVO));
        functionPermissionVO.setAuthEntity(authDetailViewVO);
        return functionPermissionVO;
    }

    @Override
    public AuthDetailDataPermissionVO getDataPermission(AuthPermissionReqVO reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        appCommonService.validateRoleExist(reqVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        //
        AuthDetailDataPermissionVO dataPermissionVO = new AuthDetailDataPermissionVO();
        //数据权限
        List<AuthDataGroupVO> authDataGroupVOS = queryAuthDataGroups(reqVO);
        dataPermissionVO.setAuthDataGroups(authDataGroupVOS);
        //字段辅助信息
        List<AuthFieldInfoVO> authFieldInfoVOS = queryFieldsInfo(menuDO.getEntityId());
        dataPermissionVO.setFieldInfo(authFieldInfoVOS);
        return dataPermissionVO;
    }

    @Override
    public AuthDetailFieldPermissionVO getFieldPermission(AuthPermissionReqVO reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        appCommonService.validateRoleExist(reqVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        //
        AuthDetailFieldPermissionVO fieldPermissionVO = new AuthDetailFieldPermissionVO();
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO);
        if (authPermissionDO != null) {
            fieldPermissionVO.setIsAllFieldsAllowed(authPermissionDO.getIsAllFieldsAllowed());
        }
        fieldPermissionVO.setAuthFields(queryAuthFields(menuDO.getEntityId(), reqVO));
        return fieldPermissionVO;
    }

    @Override
    public void updatePageAllowed(AuthUpdatePageAllowedReqVO reqVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO.getPermissionReq());
        if (authPermissionDO == null) {
            authPermissionDO = new AuthPermissionDO();
            authPermissionDO.setApplicationId(reqVO.getPermissionReq().getApplicationId());
            authPermissionDO.setRoleId(reqVO.getPermissionReq().getRoleId());
            authPermissionDO.setMenuId(reqVO.getPermissionReq().getMenuId());
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
        if (reqVO.getAuthOperation() != null) {
            upsetAuthOperation(reqVO.getPermissionReq(), reqVO.getAuthOperation());
        }
        if (reqVO.getAuthOperations() != null) {
            for (AuthOperationVO authOperationVO : reqVO.getAuthOperations()) {
                upsetAuthOperation(reqVO.getPermissionReq(), authOperationVO);
            }
        }
    }

    private void upsetAuthOperation(AuthPermissionReqVO reqVO, AuthOperationVO operationVO) {
        Long operationId = operationVO.getId();
        AuthOperationDO authOperationDO = null;
        if (operationId != null) {
            authOperationDO = authOperationRepository.findById(operationId);
        }
        if (authOperationDO == null) {
            authOperationDO = authOperationRepository.findByQuery(reqVO, operationVO);
        }
        if (authOperationDO == null) {
            authOperationDO = new AuthOperationDO();
            authOperationDO.setApplicationId(reqVO.getApplicationId());
            authOperationDO.setRoleId(reqVO.getRoleId());
            authOperationDO.setMenuId(reqVO.getMenuId());
            authOperationDO.setOperationCode(operationVO.getOperationCode());
            authOperationDO.setIsAllowed(operationVO.getIsAllowed());
            authOperationRepository.insert(authOperationDO);
        } else {
            authOperationDO.setIsAllowed(operationVO.getIsAllowed());
            authOperationRepository.update(authOperationDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataGroup(AuthUpdateDataGroupReqVO reqVO) {
        Long dataGroupId = reqVO.getAuthDataGroup().getId();
        if (dataGroupId == null) {
            AuthDataGroupDO authDataGroupDO = new AuthDataGroupDO();
            authDataGroupDO.setApplicationId(reqVO.getPermissionReq().getApplicationId());
            authDataGroupDO.setRoleId(reqVO.getPermissionReq().getRoleId());
            authDataGroupDO.setMenuId(reqVO.getPermissionReq().getMenuId());
            BeanUtils.copyProperties(reqVO.getAuthDataGroup(), authDataGroupDO);
            authDataGroupRepository.insert(authDataGroupDO);
            List<AuthDataFilterVO> authDataFilterVOS = formatAuthDataFilterVO(reqVO.getAuthDataGroup().getDataFilters());
            if (CollectionUtils.isNotEmpty(authDataFilterVOS)) {
                List<AuthDataFilterDO> dataFilterDOS = authDataFilterVOS.stream().map(v -> BeanUtils.toBean(v, AuthDataFilterDO.class)).toList();
                authDataFilterRepository.insertBatch(dataFilterDOS);
            }
        } else {
            AuthDataGroupDO authDataGroupDO = authDataGroupRepository.findById(dataGroupId);
            BeanUtils.copyProperties(reqVO.getAuthDataGroup(), authDataGroupDO);
            authDataGroupRepository.update(authDataGroupDO);
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
    @Transactional(rollbackFor = Exception.class)
    public void updateField(AuthUpdateFieldReqVO reqVO) {
        if (reqVO.getIsAllFieldsAllowed() != null) {
            AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(reqVO.getPermissionReq());
            authPermissionDO.setIsAllFieldsAllowed(reqVO.getIsAllFieldsAllowed());
            authPermissionRepository.update(authPermissionDO);
        }
        if (reqVO.getAuthField() != null) {
            AuthFieldVO authField = reqVO.getAuthField();
            upsetAuthField(reqVO, authField);
        }
        if (reqVO.getAuthFields() != null) {
            reqVO.getAuthFields().forEach(v -> upsetAuthField(reqVO, v));
        }
    }

    private void upsetAuthField(AuthUpdateFieldReqVO reqVO, AuthFieldVO authField) {
        AuthFieldDO authFieldDO = null;
        if (authField.getId() != null) {
            authFieldDO = authFieldRepository.findById(authField.getId());
        }
        if (authFieldDO == null) {
            authFieldDO = authFieldRepository.findByQuery(reqVO.getPermissionReq(), authField.getId());
        }
        if (authFieldDO == null) {
            authFieldDO = new AuthFieldDO();
            authFieldDO.setApplicationId(reqVO.getPermissionReq().getApplicationId());
            authFieldDO.setRoleId(reqVO.getPermissionReq().getRoleId());
            authFieldDO.setMenuId(reqVO.getPermissionReq().getMenuId());
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


    private List<AuthOperationVO> queryAuthOperations(AuthPermissionReqVO reqVO) {
        List<AuthOperationEnum.Data> dictDataList = AuthOperationEnum.getData();
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(reqVO);
        List<Pair<AuthOperationEnum.Data, AuthOperationDO>> pairs = AuthUtils.leftOuterJoin(dictDataList, authOperationDOS,
                (dict, authOperationDO) -> dict.getOperation().equalsIgnoreCase(authOperationDO.getOperationCode()));

        return pairs.stream().map(pair -> {
            AuthOperationEnum.Data dictData = pair.getLeft();
            AuthOperationVO authOperationVO = new AuthOperationVO();
            authOperationVO.setOperationCode(dictData.getOperation());
            authOperationVO.setDisplayName(dictData.getLabel());
            AuthOperationDO operationDO = pair.getRight();
            if (operationDO != null) {
                authOperationVO.setId(operationDO.getId());
                authOperationVO.setIsAllowed(operationDO.getIsAllowed());
            }
            return authOperationVO;
        }).toList();
    }

    private List<AuthViewVO> queryAuthViews(AuthPermissionReqVO reqVO) {
        List<AuthViewDO> authViewDOS = authEntityRepository.findByQuery(reqVO);
        return BeanUtils.toBean(authViewDOS, AuthViewVO.class);
    }

    private List<AuthDataGroupVO> queryAuthDataGroups(AuthPermissionReqVO reqVO) {
        List<AuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(reqVO);
        List<AuthDataGroupVO> authDataGroupVOS = BeanUtils.toBean(authDataGroupDOS, AuthDataGroupVO.class);
        for (AuthDataGroupVO authDataGroupVO : authDataGroupVOS) {
            List<AuthDataFilterDO> dataFilterDOS = authDataFilterRepository.findByGroupId(authDataGroupVO.getId());
            List<AuthDataFilterVO> dataFilterVOS = BeanUtils.toBean(dataFilterDOS, AuthDataFilterVO.class);
            List<List<AuthDataFilterVO>> dataFilters = groupAndOrder(dataFilterVOS);
            authDataGroupVO.setDataFilters(dataFilters);
        }
        return authDataGroupVOS;
    }

    private List<AuthFieldInfoVO> queryFieldsInfo(Long entityId) {
        List<EntityFieldRespDTO> entityFieldRespDTOS = getEntityFieldRespDTOS(entityId);
        List<PermitRefOtftRespDTO> permitRefOtftRespDTOS = metadataPermitApi.getPermitRefOtftList();
        List<AuthFieldInfoVO> authFieldInfoVOS = entityFieldRespDTOS.stream().map(field -> {
            AuthFieldInfoVO authFieldInfoVO = new AuthFieldInfoVO();
            authFieldInfoVO.setFieldId(Long.valueOf(field.getId())); //TODO 强转
            authFieldInfoVO.setFieldDisplayName(field.getDisplayName());
            authFieldInfoVO.setFieldType(field.getFieldType());
            authFieldInfoVO.setOperatorMap(queryAuthFieldInfoVOOperator(field.getFieldType(), permitRefOtftRespDTOS));
            return authFieldInfoVO;
        }).toList();
        return authFieldInfoVOS;
    }

    private List<AuthFieldInfoVO.Operator> queryAuthFieldInfoVOOperator(String fieldTypeCode, List<PermitRefOtftRespDTO> permitRefOtftRespDTOS) {
        return permitRefOtftRespDTOS.stream()
                .filter(permitRefOtftRespDTO -> permitRefOtftRespDTO.getFieldTypeCode().equalsIgnoreCase(fieldTypeCode))
                .map(permitRefOtftRespDTO -> {
                    AuthFieldInfoVO.Operator operator = new AuthFieldInfoVO.Operator();
                    operator.setValue(permitRefOtftRespDTO.getOperationTypeCode());
                    operator.setLabel(permitRefOtftRespDTO.getOperationTypeName());
                    return operator;
                })
                .toList();
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

    private List<AuthFieldVO> queryAuthFields(Long entityId, AuthPermissionReqVO reqVO) {
        List<EntityFieldRespDTO> entityFieldRespDTOS = getEntityFieldRespDTOS(entityId);
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(reqVO);
        List<Pair<EntityFieldRespDTO, AuthFieldDO>> pairs = AuthUtils.leftOuterJoin(entityFieldRespDTOS, authFieldDOS,
                (entityFieldRespDTO, authFieldDO) -> Objects.equals(entityFieldRespDTO.getId(), authFieldDO.getFieldId()));
        return pairs.stream().map(pair -> {
            EntityFieldRespDTO entityField = pair.getLeft();
            AuthFieldDO authFieldDO = pair.getRight();
            //
            AuthFieldVO authFieldVO = new AuthFieldVO();
            authFieldVO.setFieldId(entityField.getId());
            authFieldVO.setFieldDisplayName(entityField.getDisplayName());
            if (authFieldDO != null) {
                authFieldVO.setId(authFieldDO.getId());
                authFieldVO.setIsCanRead(authFieldDO.getIsCanRead());
                authFieldVO.setIsCanEdit(authFieldDO.getIsCanEdit());
                authFieldVO.setIsCanDownload(authFieldDO.getIsCanDownload());
            }
            return authFieldVO;
        }).toList();
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
        authDataFilterDO.setFieldId(groupId);
        authDataFilterDO.setConditionGroup(authDataFilterVO.getConditionGroup());
        authDataFilterDO.setConditionOrder(authDataFilterVO.getConditionOrder());
        authDataFilterDO.setFieldId(authDataFilterVO.getFieldId());
        authDataFilterDO.setFieldValueType(authDataFilterVO.getFieldValueType());
        authDataFilterDO.setFieldOperator(authDataFilterVO.getFieldOperator());
        authDataFilterDO.setFieldValue(authDataFilterVO.getFieldValue());
    }

    private List<AuthDataFilterVO> formatAuthDataFilterVO(List<List<AuthDataFilterVO>> authDataFilters) {
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
