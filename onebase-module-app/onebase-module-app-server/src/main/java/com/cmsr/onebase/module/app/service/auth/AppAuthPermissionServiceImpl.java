package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.auth.dto.AuthPermissionDTO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;
import com.cmsr.onebase.module.app.dal.database.auth.*;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.*;
import com.cmsr.onebase.module.app.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.util.AuthUtils;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.api.validation.MetadataPermitApi;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import com.cmsr.onebase.module.system.api.dict.DictDataApi;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

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
    private AppAuthEntityRepository authEntityRepository;

    @Resource
    private AppAuthPermissionRepository authPermissionRepository;

    @Resource
    private AppAuthFieldRepository authFieldRepository;

    @Resource
    private AppAuthOperationRepository authOperationRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private DictDataApi dictDataApi;

    @Resource
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Resource
    private MetadataPermitApi metadataPermitApi;

    @Override
    public AuthDetailPermissionVO getPermission(AuthPermissionReqVO reqVO) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(reqVO.getApplicationId());
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        List<EntityFieldRespDTO> entityFieldRespDTOS = getEntityFieldRespDTOS(menuDO.getEntityId());
        //
        AuthPermissionDTO permissionDTO = new AuthPermissionDTO();
        permissionDTO.setApplicationId(applicationDO.getId());
        permissionDTO.setMenuId(menuDO.getId());
        permissionDTO.setRoleId(authRoleDO.getId());
        //
        AuthDetailPermissionVO authDetailPermissionVO = new AuthDetailPermissionVO();
        BeanUtils.copyProperties(permissionDTO, authDetailPermissionVO);
        //补充 基本权限
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionDTO);
        if (authPermissionDO != null) {
            authDetailPermissionVO.setPageAllowed(authPermissionDO.getPageAllowed());
        }
        //补充 操作权限
        authDetailPermissionVO.setAuthOperations(queryAuthOperations(permissionDTO));
        //补充 实体权限
        AuthDetailEntityVO authDetailEntityVO = new AuthDetailEntityVO();
        if (authPermissionDO != null) {
            authDetailEntityVO.setAllEntitiesAllowed(authPermissionDO.getAllEntitiesAllowed());
        }
        authDetailEntityVO.setAuthEntities(queryAuthEntities(permissionDTO));
        authDetailPermissionVO.setAuthEntity(authDetailEntityVO);
        //补充 数据权限
        AuthDetailDataGroupVO authDetailDataGroupVO = queryAuthDataGroups(entityFieldRespDTOS, permissionDTO);
        authDetailPermissionVO.setAuthData(authDetailDataGroupVO);
        //补充 字段权限
        AuthDetailFieldVO authDetailFieldVO = new AuthDetailFieldVO();
        if (authPermissionDO != null) {
            authDetailFieldVO.setAllFieldsAllowed(authPermissionDO.getAllFieldsAllowed());
        }
        authDetailFieldVO.setAuthFields(queryAuthFields(entityFieldRespDTOS, permissionDTO));
        authDetailPermissionVO.setAuthField(authDetailFieldVO);
        return authDetailPermissionVO;
    }


    private List<AuthOperationVO> queryAuthOperations(AuthPermissionDTO permissionDTO) {
        List<DictDataRespDTO> dictDataList = dictDataApi.getDictDataList(AuthUtils.AUTH_OPERATION_TABLE_NAME)
                .getData();
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(permissionDTO);
        List<Pair<DictDataRespDTO, AuthOperationDO>> pairs = AuthUtils.leftOuterJoin(dictDataList, authOperationDOS,
                (dict, authOperationDO) -> dict.getValue().equalsIgnoreCase(authOperationDO.getOperationCode()));

        return pairs.stream().map(pair -> {
            DictDataRespDTO dictData = pair.getLeft();
            AuthOperationVO authOperationVO = new AuthOperationVO();
            authOperationVO.setOperationCode(dictData.getValue());
            authOperationVO.setDisplayName(dictData.getLabel());
            AuthOperationDO operationDO = pair.getRight();
            if (operationDO != null) {
                authOperationVO.setId(operationDO.getId());
                authOperationVO.setAllowed(operationDO.getAllowed());
            }
            return authOperationVO;
        }).toList();
    }

    private List<AuthEntityVO> queryAuthEntities(AuthPermissionDTO permissionDTO) {
        List<AuthEntityDO> authEntityDOS = authEntityRepository.findByQuery(permissionDTO);
        return BeanUtils.toBean(authEntityDOS, AuthEntityVO.class);
    }

    private AuthDetailDataGroupVO queryAuthDataGroups(List<EntityFieldRespDTO> entityFieldRespDTOS, AuthPermissionDTO permissionDTO) {
        List<AuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(permissionDTO);
        List<AuthDataGroupVO> authDataGroupVOS = BeanUtils.toBean(authDataGroupDOS, AuthDataGroupVO.class);
        for (AuthDataGroupVO authDataGroupVO : authDataGroupVOS) {
            List<AuthDataFilterDO> dataFilterDOS = authDataFilterRepository.findByGroupId(authDataGroupVO.getId());
            List<AuthDataFilterVO> dataFilterVOS = BeanUtils.toBean(dataFilterDOS, AuthDataFilterVO.class);
            List<List<AuthDataFilterVO>> dataFilters = groupAndOrder(dataFilterVOS);
            authDataGroupVO.setDataFilters(dataFilters);
        }
        AuthDetailDataGroupVO authDetailDataGroupVO = new AuthDetailDataGroupVO();
        authDetailDataGroupVO.setAuthDataGroups(authDataGroupVOS);
        List<AuthFieldInfoVO> authFieldInfoVOS = queryFieldsInfo(entityFieldRespDTOS);
        authDetailDataGroupVO.setFieldInfo(authFieldInfoVOS);
        return authDetailDataGroupVO;
    }

    private List<AuthFieldInfoVO> queryFieldsInfo(List<EntityFieldRespDTO> entityFieldRespDTOS) {
        List<PermitRefOtftRespDTO> permitRefOtftRespDTOS = metadataPermitApi.getPermitRefOtftList().getData();

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
                    operator.setValue(permitRefOtftRespDTO.getValidationCode());
                    operator.setLabel(permitRefOtftRespDTO.getValidationName());
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

    private List<AuthFieldVO> queryAuthFields(List<EntityFieldRespDTO> entityFieldRespDTOS, AuthPermissionDTO permissionDTO) {
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(permissionDTO);
        List<Pair<EntityFieldRespDTO, AuthFieldDO>> pairs = AuthUtils.leftOuterJoin(entityFieldRespDTOS, authFieldDOS,
                (entityFieldRespDTO, authFieldDO) -> Objects.equals(NumberUtils.toLong(entityFieldRespDTO.getId()), authFieldDO.getFieldId())); //TODO 强转
        return pairs.stream().map(pair -> {
            EntityFieldRespDTO entityField = pair.getLeft();
            AuthFieldVO authFieldVO = new AuthFieldVO();
            authFieldVO.setFieldDisplayName(entityField.getFieldName());
            authFieldVO.setFieldDisplayName(entityField.getDisplayName());
            AuthFieldDO authFieldDO = pair.getRight();
            if (authFieldDO != null) {
                authFieldVO.setId(authFieldDO.getId());
                authFieldVO.setCanRead(authFieldDO.getCanRead());
                authFieldVO.setCanEdit(authFieldDO.getCanEdit());
                authFieldVO.setCanDownload(authFieldDO.getCanDownload());
            }
            return authFieldVO;
        }).toList();
    }

    private List<EntityFieldRespDTO> getEntityFieldRespDTOS(Long entityId) {
        EntityFieldQueryReqDTO reqDTO = new EntityFieldQueryReqDTO();
        reqDTO.setEntityId(entityId.toString()); //TODO 强转
        reqDTO.setIsSystemField(1);
        List<EntityFieldRespDTO> entityFieldRespDTOS = metadataEntityFieldApi.getEntityFieldList(reqDTO).getData();
        return entityFieldRespDTOS;
    }

    @Override
    public void updatePermission(AuthDetailPermissionVO detailVO) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(detailVO.getApplicationId());
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(detailVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(detailVO.getMenuId());
        //
        AuthPermissionDTO permissionDTO = new AuthPermissionDTO();
        permissionDTO.setApplicationId(applicationDO.getId());
        permissionDTO.setMenuId(menuDO.getId());
        permissionDTO.setRoleId(authRoleDO.getId());
        //
        AuthPermissionReqVO reqVO = new AuthPermissionReqVO();
        reqVO.setApplicationId(detailVO.getApplicationId());
        reqVO.setRoleId(detailVO.getRoleId());
        reqVO.setMenuId(detailVO.getMenuId());
        //更新 功能权限
        updateAuthPermission(permissionDTO, detailVO);
        updateAuthOperation(permissionDTO, detailVO.getAuthOperations());
        updateAuthEntity(permissionDTO, detailVO.getAuthEntity());
        updateAuthDataGroup(permissionDTO, detailVO.getAuthData().getAuthDataGroups());
        updateAuthField(permissionDTO, detailVO.getAuthField());
    }

    private void updateAuthPermission(AuthPermissionDTO permissionDTO, AuthDetailPermissionVO detailVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionDTO);
        if (authPermissionDO == null) {
            authPermissionDO = new AuthPermissionDO();
            copyFields(permissionDTO, detailVO, authPermissionDO);
            authPermissionRepository.insert(authPermissionDO);
        } else {
            copyFields(permissionDTO, detailVO, authPermissionDO);
            authPermissionRepository.update(authPermissionDO);
        }
    }

    private void copyFields(AuthPermissionDTO permissionDTO, AuthDetailPermissionVO detailVO, AuthPermissionDO authPermissionDO) {
        authPermissionDO.setApplicationId(permissionDTO.getApplicationId());
        authPermissionDO.setRoleId(permissionDTO.getRoleId());
        authPermissionDO.setMenuId(permissionDTO.getMenuId());
        authPermissionDO.setPageAllowed(detailVO.getPageAllowed());
        authPermissionDO.setAllEntitiesAllowed(detailVO.getAuthEntity().getAllEntitiesAllowed());
        authPermissionDO.setAllFieldsAllowed(detailVO.getAuthField().getAllFieldsAllowed());
    }

    private void updateAuthOperation(AuthPermissionDTO permissionDTO, List<AuthOperationVO> authOperationVOS) {
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(permissionDTO);
        List<Pair<AuthOperationDO, AuthOperationVO>> pairs = AuthUtils.fullOuterJoin(authOperationDOS, authOperationVOS,
                (authOperationDO, authOperationVO) -> authOperationDO.getId().equals(authOperationVO.getId())
        );
        for (Pair<AuthOperationDO, AuthOperationVO> pair : pairs) {
            AuthOperationDO authOperationDO = pair.getLeft();
            AuthOperationVO authOperationVO = pair.getRight();
            if (authOperationDO != null && authOperationVO != null) {
                copyFields(permissionDTO, authOperationVO, authOperationDO);
                authOperationRepository.update(authOperationDO);
            } else if (authOperationDO == null && authOperationVO != null) {
                authOperationDO = new AuthOperationDO();
                copyFields(permissionDTO, authOperationVO, authOperationDO);
                authOperationRepository.insert(authOperationDO);
            } else if (authOperationDO != null && authOperationVO == null) {
                authOperationRepository.delete(authOperationDO);
            }
        }
    }

    private void copyFields(AuthPermissionDTO permissionDTO, AuthOperationVO authOperationVO, AuthOperationDO authOperationDO) {
        authOperationDO.setApplicationId(permissionDTO.getApplicationId());
        authOperationDO.setRoleId(permissionDTO.getRoleId());
        authOperationDO.setMenuId(permissionDTO.getMenuId());
        authOperationDO.setOperationCode(authOperationVO.getOperationCode());
        authOperationDO.setAllowed(authOperationVO.getAllowed());
    }

    private void updateAuthEntity(AuthPermissionDTO permissionDTO, AuthDetailEntityVO detailEntityVO) {
        List<AuthEntityVO> authEntityVOS = detailEntityVO.getAuthEntities();
        List<AuthEntityDO> authEntityDOS = authEntityRepository.findByQuery(permissionDTO);
        List<Pair<AuthEntityDO, AuthEntityVO>> pairs = AuthUtils.fullOuterJoin(authEntityDOS, authEntityVOS,
                (authEntityDO, authEntityVO) -> authEntityDO.getId().equals(authEntityVO.getId())
        );
        for (Pair<AuthEntityDO, AuthEntityVO> pair : pairs) {
            AuthEntityDO authEntityDO = pair.getLeft();
            AuthEntityVO authEntityVO = pair.getRight();
            if (authEntityDO != null && authEntityVO != null) {
                copyFields(permissionDTO, authEntityVO, authEntityDO);
                authEntityRepository.update(authEntityDO);
            } else if (authEntityDO == null && authEntityVO != null) {
                authEntityDO = new AuthEntityDO();
                copyFields(permissionDTO, authEntityVO, authEntityDO);
                authEntityRepository.insert(authEntityDO);
            } else if (authEntityDO != null && authEntityVO == null) {
                authEntityRepository.delete(authEntityDO);
            }
        }
    }

    private void copyFields(AuthPermissionDTO permissionDTO, AuthEntityVO authEntityVO, AuthEntityDO authEntityDO) {
        authEntityDO.setApplicationId(permissionDTO.getApplicationId());
        authEntityDO.setRoleId(permissionDTO.getRoleId());
        authEntityDO.setMenuId(permissionDTO.getMenuId());
        authEntityDO.setEntityId(authEntityVO.getEntityId());
        authEntityDO.setAllowed(authEntityVO.getAllowed());
    }

    private void updateAuthDataGroup(AuthPermissionDTO permissionDTO, List<AuthDataGroupVO> authDataGroupVOS) {
        for (int i = 0; i < authDataGroupVOS.size(); i++) {
            authDataGroupVOS.get(i).setGroupOrder(i + 1);
        }
        List<AuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(permissionDTO);
        List<Pair<AuthDataGroupDO, AuthDataGroupVO>> pairs = AuthUtils.fullOuterJoin(authDataGroupDOS, authDataGroupVOS,
                (authDataGroupDO, authDataGroupVO) -> authDataGroupDO.getId().equals(authDataGroupVO.getId())
        );
        for (Pair<AuthDataGroupDO, AuthDataGroupVO> pair : pairs) {
            AuthDataGroupDO authDataGroupDO = pair.getLeft();
            AuthDataGroupVO authDataGroupVO = pair.getRight();
            if (authDataGroupDO != null && authDataGroupVO != null) {
                copyFields(permissionDTO, authDataGroupVO, authDataGroupDO);
                authDataGroupRepository.update(authDataGroupDO);
            } else if (authDataGroupDO == null && authDataGroupVO != null) {
                authDataGroupDO = new AuthDataGroupDO();
                copyFields(permissionDTO, authDataGroupVO, authDataGroupDO);
                authDataGroupRepository.insert(authDataGroupDO);
            } else if (authDataGroupDO != null && authDataGroupVO != null) {
                authDataGroupRepository.delete(authDataGroupDO);
            }
            updateAuthDataFilter(authDataGroupDO.getId(), authDataGroupVO.getDataFilters());
        }
    }

    private void copyFields(AuthPermissionDTO permissionDTO, AuthDataGroupVO authDataGroupVO, AuthDataGroupDO authDataGroupDO) {
        authDataGroupDO.setApplicationId(permissionDTO.getApplicationId());
        authDataGroupDO.setRoleId(permissionDTO.getRoleId());
        authDataGroupDO.setMenuId(permissionDTO.getMenuId());
        authDataGroupDO.setGroupName(authDataGroupVO.getGroupName());
        authDataGroupDO.setGroupOrder(authDataGroupVO.getGroupOrder());
        authDataGroupDO.setDescription(authDataGroupVO.getDescription());
        authDataGroupDO.setScopeFieldId(authDataGroupVO.getScopeFieldId());
        authDataGroupDO.setScopeLevel(authDataGroupVO.getScopeLevel());
        authDataGroupDO.setOperable(authDataGroupVO.getOperable());
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

    private void updateAuthField(AuthPermissionDTO permissionDTO, AuthDetailFieldVO detailFieldVO) {
        List<AuthFieldVO> authFieldVOS = detailFieldVO.getAuthFields();
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(permissionDTO);
        List<Pair<AuthFieldDO, AuthFieldVO>> pairs = AuthUtils.fullOuterJoin(authFieldDOS, authFieldVOS,
                (authFieldDO, authFieldVO) -> authFieldDO.getFieldId().equals(authFieldVO.getFieldId())
        );
        for (Pair<AuthFieldDO, AuthFieldVO> pair : pairs) {
            AuthFieldDO authFieldDO = pair.getLeft();
            AuthFieldVO authFieldVO = pair.getRight();
            if (authFieldDO != null && authFieldVO != null) {
                copyFields(permissionDTO, authFieldVO, authFieldDO);
                authFieldRepository.update(authFieldDO);
            } else if (authFieldDO == null && authFieldVO != null) {
                authFieldDO = new AuthFieldDO();
                copyFields(permissionDTO, authFieldVO, authFieldDO);
                authFieldRepository.insert(authFieldDO);
            } else if (authFieldDO != null && authFieldVO == null) {
                authFieldRepository.delete(authFieldDO);
            }
        }
    }

    private void copyFields(AuthPermissionDTO permissionDTO, AuthFieldVO authFieldVO, AuthFieldDO authFieldDO) {
        authFieldDO.setApplicationId(permissionDTO.getApplicationId());
        authFieldDO.setRoleId(permissionDTO.getRoleId());
        authFieldDO.setMenuId(permissionDTO.getMenuId());
        authFieldDO.setFieldId(authFieldVO.getFieldId());
        authFieldDO.setCanRead(authFieldVO.getCanRead());
        authFieldDO.setCanEdit(authFieldVO.getCanEdit());
        authFieldDO.setCanDownload(authFieldVO.getCanDownload());
    }

}
