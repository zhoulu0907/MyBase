package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;
import com.cmsr.onebase.module.app.dal.database.auth.*;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.*;
import com.cmsr.onebase.module.app.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.util.AuthUtils;
import com.cmsr.onebase.module.system.api.dict.DictDataApi;
import jakarta.annotation.Resource;
import lombok.Setter;
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

    @Override
    public AuthPermissionDetailVO getPermission(AuthPermissionReqVO reqVO) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(reqVO.getApplicationId());
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(reqVO.getMenuId());
        //
        AuthPermissionDTO permissionDTO = new AuthPermissionDTO();
        permissionDTO.setApplicationId(applicationDO.getId());
        permissionDTO.setApplicationCode(applicationDO.getAppCode());
        permissionDTO.setMenuId(menuDO.getId());
        permissionDTO.setMenuCode(menuDO.getMenuCode());
        permissionDTO.setRoleId(authRoleDO.getId());
        permissionDTO.setRoleCode(authRoleDO.getRoleCode());
        //
        AuthPermissionDetailVO authPermissionDetailVO = new AuthPermissionDetailVO();
        BeanUtils.copyProperties(reqVO, authPermissionDetailVO);
        BeanUtils.copyProperties(permissionDTO, authPermissionDetailVO);
        //补充 基本权限
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionDTO);
        AuthPermissionVO authPermissionVO = BeanUtils.toBean(authPermissionDO, AuthPermissionVO.class);
        authPermissionVO = formatAuthPermissionVO(authPermissionVO);
        authPermissionDetailVO.setAuthPermission(authPermissionVO);
        //补充 操作权限
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(permissionDTO);
        List<AuthOperationVO> authOperationVOS = BeanUtils.toBean(authOperationDOS, AuthOperationVO.class);
        authOperationVOS = formatAuthOperations(authOperationVOS);
        authPermissionDetailVO.setAuthOperations(authOperationVOS);
        //补充 实体权限
        List<AuthEntityDO> authEntityDOS = authEntityRepository.findByQuery(permissionDTO);
        authPermissionDetailVO.setAuthEntities(BeanUtils.toBean(authEntityDOS, AuthEntityVO.class));
        //补充 数据权限
        List<AuthDataGroupVO> authDataGroupVOS = queryAuthDataGroups(permissionDTO);
        authPermissionDetailVO.setAuthDataGroups(authDataGroupVOS);
        //补充 字段权限
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(permissionDTO);
        authPermissionDetailVO.setAuthFields(BeanUtils.toBean(authFieldDOS, AuthFieldVO.class));
        return authPermissionDetailVO;
    }

    private AuthPermissionVO formatAuthPermissionVO(AuthPermissionVO authPermissionVO) {
        if (authPermissionVO == null) {
            authPermissionVO = new AuthPermissionVO();
            authPermissionVO.setPageAllowed(true);
            authPermissionVO.setAllEntitiesAllowed(true);
            authPermissionVO.setAllFieldsAllowed(true);
        }
        return authPermissionVO;
    }

    private List<AuthOperationVO> formatAuthOperations(List<AuthOperationVO> authOperationVOS) {
        List<AuthOperationVO> result = new ArrayList<>();
        List<DictDataRespDTO> dictDatas = dictDataApi.getDictDataList("app_auth_operation")
                .getData();
        List<Pair<DictDataRespDTO, AuthOperationVO>> pairs = AuthUtils.leftOuterJoin(dictDatas, authOperationVOS, (dict, authOperationVO) ->
                dict.getValue().equalsIgnoreCase(authOperationVO.getOperationCode()));
        for (Pair<DictDataRespDTO, AuthOperationVO> pair : pairs) {
            DictDataRespDTO dict = pair.getLeft();
            AuthOperationVO authOperationVO = pair.getRight();
            if (authOperationVO == null) {
                authOperationVO = new AuthOperationVO();
                authOperationVO.setOperationCode(dict.getValue());
                authOperationVO.setOperationDisplayName(dict.getLabel());
                authOperationVO.setAllowed(false);
            } else {
                authOperationVO.setOperationDisplayName(dict.getLabel());
            }
            result.add(authOperationVO);
        }
        return result;
    }


    private List<AuthDataGroupVO> queryAuthDataGroups(AuthPermissionDTO permissionDTO) {
        List<AuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(permissionDTO);
        List<AuthDataGroupVO> authDataGroupVOS = BeanUtils.toBean(authDataGroupDOS, AuthDataGroupVO.class);
        for (AuthDataGroupVO authDataGroupVO : authDataGroupVOS) {
            List<AuthDataFilterDO> dataFilterDOS = authDataFilterRepository.findByGroupCode(authDataGroupVO.getGroupCode());
            List<AuthDataFilterVO> dataFilterVOS = BeanUtils.toBean(dataFilterDOS, AuthDataFilterVO.class);
            List<List<AuthDataFilterVO>> dataFilters = groupAndOrder(dataFilterVOS);
            authDataGroupVO.setDataFilters(dataFilters);
        }
        return authDataGroupVOS;
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

    @Override
    public void updatePermission(AuthPermissionDetailVO detailVO) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(detailVO.getApplicationId());
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(detailVO.getRoleId());
        MenuDO menuDO = appCommonService.validateMenuExist(detailVO.getMenuId());
        //
        AuthPermissionDTO permissionDTO = new AuthPermissionDTO();
        permissionDTO.setApplicationId(applicationDO.getId());
        permissionDTO.setApplicationCode(applicationDO.getAppCode());
        permissionDTO.setMenuId(menuDO.getId());
        permissionDTO.setMenuCode(menuDO.getMenuCode());
        permissionDTO.setRoleId(authRoleDO.getId());
        permissionDTO.setRoleCode(authRoleDO.getRoleCode());
        //
        AuthPermissionReqVO reqVO = new AuthPermissionReqVO();
        reqVO.setApplicationId(detailVO.getApplicationId());
        reqVO.setRoleId(detailVO.getRoleId());
        reqVO.setMenuId(detailVO.getMenuId());
        //更新 功能权限
        updateAuthPermission(permissionDTO, detailVO.getAuthPermission());
        updateAuthOperation(permissionDTO, detailVO.getAuthOperations());
        updateAuthEntity(permissionDTO, detailVO.getAuthEntities());
        updateAuthDataGroup(permissionDTO, detailVO.getAuthDataGroups());
        updateAuthField(permissionDTO, detailVO.getAuthFields());
    }

    private void updateAuthPermission(AuthPermissionDTO permissionDTO, AuthPermissionVO authPermissionVO) {
        AuthPermissionDO authPermissionDO = authPermissionRepository.findByQuery(permissionDTO);
        if (authPermissionDO == null) {
            authPermissionDO = new AuthPermissionDO();
            copyFields(permissionDTO, authPermissionVO, authPermissionDO);
            authPermissionRepository.insert(authPermissionDO);
        } else {
            copyFields(permissionDTO, authPermissionVO, authPermissionDO);
            authPermissionRepository.update(authPermissionDO);
        }
    }

    private void copyFields(AuthPermissionDTO permissionDTO, AuthPermissionVO authPermissionVO, AuthPermissionDO authPermissionDO) {
        authPermissionDO.setApplicationCode(permissionDTO.getApplicationCode());
        authPermissionDO.setRoleCode(permissionDTO.getRoleCode());
        authPermissionDO.setMenuCode(permissionDTO.getMenuCode());
        authPermissionDO.setPageAllowed(authPermissionVO.getPageAllowed());
        authPermissionDO.setAllEntitiesAllowed(authPermissionVO.getAllEntitiesAllowed());
        authPermissionDO.setAllFieldsAllowed(authPermissionVO.getAllFieldsAllowed());
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
        authOperationDO.setApplicationCode(permissionDTO.getApplicationCode());
        authOperationDO.setRoleCode(permissionDTO.getRoleCode());
        authOperationDO.setMenuCode(permissionDTO.getMenuCode());
        authOperationDO.setOperationCode(authOperationVO.getOperationCode());
        authOperationDO.setAllowed(authOperationVO.getAllowed());
    }

    private void updateAuthEntity(AuthPermissionDTO permissionDTO, List<AuthEntityVO> authEntityVOS) {
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
        authEntityDO.setApplicationCode(permissionDTO.getApplicationCode());
        authEntityDO.setRoleCode(permissionDTO.getRoleCode());
        authEntityDO.setMenuCode(permissionDTO.getMenuCode());
        authEntityDO.setEntityCode(authEntityVO.getEntityCode());
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
            updateAuthDataFilter(authDataGroupDO.getGroupCode(), authDataGroupVO.getDataFilters());
        }
    }

    private void copyFields(AuthPermissionDTO permissionDTO, AuthDataGroupVO authDataGroupVO, AuthDataGroupDO authDataGroupDO) {
        authDataGroupDO.setApplicationCode(permissionDTO.getApplicationCode());
        authDataGroupDO.setRoleCode(permissionDTO.getRoleCode());
        authDataGroupDO.setMenuCode(permissionDTO.getMenuCode());
        authDataGroupDO.setGroupName(authDataGroupVO.getGroupName());
        authDataGroupDO.setGroupOrder(authDataGroupVO.getGroupOrder());
        authDataGroupDO.setDescription(authDataGroupVO.getDescription());
        authDataGroupDO.setScopeFieldCode(authDataGroupVO.getScopeFieldCode());
        authDataGroupDO.setScopeLevel(authDataGroupVO.getScopeLevel());
        authDataGroupDO.setOperable(authDataGroupVO.getOperable());
    }

    private void updateAuthDataFilter(String groupCode, List<List<AuthDataFilterVO>> authDataFilters) {
        if (authDataFilters == null || authDataFilters.isEmpty()) {
            authDataFilterRepository.deleteByGroupCode(groupCode);
        }
        List<AuthDataFilterDO> authDataFilterDOS = authDataFilterRepository.findByGroupCode(groupCode);
        List<AuthDataFilterVO> authDataFilterVOS = formatAuthDataFilterVO(authDataFilters);
        List<Pair<AuthDataFilterDO, AuthDataFilterVO>> pairs = AuthUtils.fullOuterJoin(authDataFilterDOS, authDataFilterVOS,
                (authDataFilterDO, authDataFilterVO) -> authDataFilterDO.getId().equals(authDataFilterVO.getId())
        );
        for (Pair<AuthDataFilterDO, AuthDataFilterVO> pair : pairs) {
            AuthDataFilterDO authDataFilterDO = pair.getLeft();
            AuthDataFilterVO authDataFilterVO = pair.getRight();
            if (authDataFilterDO != null && authDataFilterVO != null) {
                copyFields(groupCode, authDataFilterVO, authDataFilterDO);
                authDataFilterRepository.update(authDataFilterDO);
            } else if (authDataFilterDO == null && authDataFilterVO != null) {
                authDataFilterDO = new AuthDataFilterDO();
                copyFields(groupCode, authDataFilterVO, authDataFilterDO);
                authDataFilterRepository.insert(authDataFilterDO);
            } else if (authDataFilterDO != null && authDataFilterVO == null) {
                authDataFilterRepository.deleteById(authDataFilterDO.getId());
            }
        }
    }

    private void copyFields(String groupCode, AuthDataFilterVO authDataFilterVO, AuthDataFilterDO authDataFilterDO) {
        authDataFilterDO.setGroupCode(groupCode);
        authDataFilterDO.setConditionGroup(authDataFilterVO.getConditionGroup());
        authDataFilterDO.setConditionOrder(authDataFilterVO.getConditionOrder());
        authDataFilterDO.setFieldCode(authDataFilterVO.getFieldCode());
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

    private void updateAuthField(AuthPermissionDTO permissionDTO, List<AuthFieldVO> authFieldVOS) {
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(permissionDTO);
        List<Pair<AuthFieldDO, AuthFieldVO>> pairs = AuthUtils.fullOuterJoin(authFieldDOS, authFieldVOS,
                (authFieldDO, authFieldVO) -> authFieldDO.getFieldCode().equals(authFieldVO.getFieldCode())
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
        authFieldDO.setApplicationCode(permissionDTO.getApplicationCode());
        authFieldDO.setRoleCode(permissionDTO.getRoleCode());
        authFieldDO.setMenuCode(permissionDTO.getMenuCode());
        authFieldDO.setFieldCode(authFieldVO.getFieldCode());
        authFieldDO.setCanRead(authFieldVO.getCanRead());
        authFieldDO.setCanEdit(authFieldVO.getCanEdit());
        authFieldDO.setCanDownload(authFieldVO.getCanDownload());
    }

}
