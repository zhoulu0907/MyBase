package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;
import com.cmsr.onebase.module.app.dal.database.auth.*;
import com.cmsr.onebase.module.app.dal.dataobject.auth.*;
import com.cmsr.onebase.module.app.util.AuthUtils;
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
    private AppAuthFeatureRepository authFeatureRepository;

    @Resource
    private AppAuthFieldRepository authFieldRepository;

    @Resource
    private AppAuthOperationRepository authOperationRepository;


    @Override
    public AuthPermissionDetailVO getPermission(AuthPermissionReqVO reqVO) {
        AuthPermissionDetailVO authPermissionDetailVO = new AuthPermissionDetailVO();
        //补充 基本权限
        AuthPermissionDO authPermissionDO = authFeatureRepository.findByQuery(reqVO);
        authPermissionDetailVO.setAuthPermission(BeanUtils.toBean(authPermissionDO, AuthPermissionVO.class));
        //补充 操作权限
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(reqVO);
        authPermissionDetailVO.setAuthOperations(BeanUtils.toBean(authOperationDOS, AuthOperationVO.class));
        //补充 实体权限
        List<AuthEntityDO> authEntityDOS = authEntityRepository.findByQuery(reqVO);
        authPermissionDetailVO.setAuthEntities(BeanUtils.toBean(authEntityDOS, AuthEntityVO.class));
        //补充 数据权限
        List<AuthDataGroupVO> authDataGroupVOS = queryAuthDataGroups(reqVO);
        authPermissionDetailVO.setAuthDataGroups(authDataGroupVOS);
        //补充 字段权限
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(reqVO);
        authPermissionDetailVO.setAuthFields(BeanUtils.toBean(authFieldDOS, AuthFieldVO.class));
        return authPermissionDetailVO;
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
        AuthPermissionReqVO reqVO = new AuthPermissionReqVO();
        reqVO.setApplicationId(detailVO.getApplicationId());
        reqVO.setRoleId(detailVO.getRoleId());
        reqVO.setMenuId(detailVO.getMenuId());
        //更新 功能权限
        updateAuthPermission(reqVO, detailVO.getAuthPermission());
        updateAuthOperation(reqVO, detailVO.getAuthOperations());
        updateAuthEntity(reqVO, detailVO.getAuthEntities());
        updateAuthDataGroup(reqVO, detailVO.getAuthDataGroups());
        updateAuthField(reqVO, detailVO.getAuthFields());
    }

    private void updateAuthPermission(AuthPermissionReqVO reqVO, AuthPermissionVO authPermissionVO) {
        AuthPermissionDO authPermissionDO = authFeatureRepository.findByQuery(reqVO);
        if (authPermissionDO == null) {
            authPermissionDO = new AuthPermissionDO();
            authPermissionDO.setApplicationId(reqVO.getApplicationId());
            authPermissionDO.setRoleId(reqVO.getRoleId());
            authPermissionDO.setMenuId(reqVO.getMenuId());
            authPermissionDO.setPageAllowed(authPermissionVO.getPageAllowed());
            authPermissionDO.setAllEntitiesAllowed(authPermissionVO.getAllEntitiesAllowed());
            authFeatureRepository.insert(authPermissionDO);
        } else {
            authPermissionDO.setPageAllowed(authPermissionVO.getPageAllowed());
            authPermissionDO.setAllEntitiesAllowed(authPermissionVO.getAllEntitiesAllowed());
            authFeatureRepository.update(authPermissionDO);
        }
    }

    private void updateAuthOperation(AuthPermissionReqVO reqVO, List<AuthOperationVO> authOperationVOS) {
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(reqVO);
        List<Pair<AuthOperationDO, AuthOperationVO>> pairs = AuthUtils.fullOuterJoin(authOperationDOS, authOperationVOS,
                (authOperationDO, authOperationVO) -> authOperationDO.getId().equals(authOperationVO.getId())
        );
        for (Pair<AuthOperationDO, AuthOperationVO> pair : pairs) {
            AuthOperationDO authOperationDO = pair.getLeft();
            AuthOperationVO authOperationVO = pair.getRight();
            if (authOperationDO != null && authOperationVO != null) {
                authOperationDO.setAllowed(authOperationVO.getAllowed());
                authOperationRepository.update(authOperationDO);
            } else if (authOperationDO == null && authOperationVO != null) {
                authOperationDO = new AuthOperationDO();
                authOperationDO.setApplicationId(reqVO.getApplicationId());
                authOperationDO.setRoleId(reqVO.getRoleId());
                authOperationDO.setMenuId(reqVO.getMenuId());
                authOperationDO.setOperationCode(authOperationVO.getOperationCode());
                authOperationDO.setAllowed(authOperationVO.getAllowed());
                authOperationRepository.insert(authOperationDO);
            } else if (authOperationDO != null && authOperationVO == null) {
                authOperationRepository.delete(authOperationDO);
            }
        }
    }

    private void updateAuthEntity(AuthPermissionReqVO reqVO, List<AuthEntityVO> authEntityVOS) {
        List<AuthEntityDO> authEntityDOS = authEntityRepository.findByQuery(reqVO);
        List<Pair<AuthEntityDO, AuthEntityVO>> pairs = AuthUtils.fullOuterJoin(authEntityDOS, authEntityVOS,
                (authEntityDO, authEntityVO) -> authEntityDO.getId().equals(authEntityVO.getId())
        );
        for (Pair<AuthEntityDO, AuthEntityVO> pair : pairs) {
            AuthEntityDO authEntityDO = pair.getLeft();
            AuthEntityVO authEntityVO = pair.getRight();
            if (authEntityDO != null && authEntityVO != null) {
                authEntityDO.setAllowed(authEntityVO.getAllowed());
                authEntityRepository.update(authEntityDO);
            } else if (authEntityDO == null && authEntityVO != null) {
                authEntityDO = new AuthEntityDO();
                authEntityDO.setApplicationId(reqVO.getApplicationId());
                authEntityDO.setRoleId(reqVO.getRoleId());
                authEntityDO.setMenuId(reqVO.getMenuId());
                authEntityDO.setEntityId(authEntityVO.getEntityId());
                authEntityDO.setAllowed(authEntityVO.getAllowed());
                authEntityRepository.insert(authEntityDO);
            } else if (authEntityDO != null && authEntityVO == null) {
                authEntityRepository.delete(authEntityDO);
            }
        }
    }

    private void updateAuthDataGroup(AuthPermissionReqVO reqVO, List<AuthDataGroupVO> authDataGroupVOS) {
        List<AuthDataGroupDO> authDataGroupDOS = authDataGroupRepository.findByQuery(reqVO);
        List<Pair<AuthDataGroupDO, AuthDataGroupVO>> pairs = AuthUtils.fullOuterJoin(authDataGroupDOS, authDataGroupVOS,
                (authDataGroupDO, authDataGroupVO) -> authDataGroupDO.getId().equals(authDataGroupVO.getId())
        );
        for (Pair<AuthDataGroupDO, AuthDataGroupVO> pair : pairs) {
            AuthDataGroupDO authDataGroupDO = pair.getLeft();
            AuthDataGroupVO authDataGroupVO = pair.getRight();
            if (authDataGroupDO != null && authDataGroupVO != null) {
                authDataGroupDO.setGroupName(authDataGroupVO.getGroupName());
                authDataGroupDO.setDescription(authDataGroupVO.getDescription());
                authDataGroupDO.setScopeFieldId(authDataGroupVO.getScopeFieldId());
                authDataGroupDO.setScopeLevel(authDataGroupVO.getScopeLevel());
                authDataGroupDO.setOperable(authDataGroupVO.getOperable());
                authDataGroupRepository.update(authDataGroupDO);
            } else if (authDataGroupDO == null && authDataGroupVO != null) {
                authDataGroupDO = new AuthDataGroupDO();
                authDataGroupDO.setApplicationId(reqVO.getApplicationId());
                authDataGroupDO.setRoleId(reqVO.getRoleId());
                authDataGroupDO.setMenuId(reqVO.getMenuId());
                authDataGroupDO.setGroupName(authDataGroupVO.getGroupName());
                authDataGroupDO.setGroupName(authDataGroupVO.getGroupName());
                authDataGroupDO.setDescription(authDataGroupVO.getDescription());
                authDataGroupDO.setScopeFieldId(authDataGroupVO.getScopeFieldId());
                authDataGroupDO.setScopeLevel(authDataGroupVO.getScopeLevel());
                authDataGroupDO.setOperable(authDataGroupVO.getOperable());
                authDataGroupRepository.insert(authDataGroupDO);
            } else if (authDataGroupDO != null && authDataGroupVO != null) {
                authDataGroupRepository.delete(authDataGroupDO);
            }
            updateAuthDataFilter(authDataGroupDO.getId(), authDataGroupVO.getDataFilters());
        }
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
                authDataFilterDO.setConditionGroup(authDataFilterVO.getConditionGroup());
                authDataFilterDO.setConditionOrder(authDataFilterVO.getConditionOrder());
                authDataFilterDO.setFieldId(authDataFilterVO.getFieldId());
                authDataFilterDO.setFieldOperator(authDataFilterVO.getFieldOperator());
                authDataFilterDO.setFieldValueType(authDataFilterVO.getFieldValueType());
                authDataFilterDO.setFieldValue(authDataFilterVO.getFieldValue());
                authDataFilterRepository.update(authDataFilterDO);
            } else if (authDataFilterDO == null && authDataFilterVO != null) {
                authDataFilterDO = new AuthDataFilterDO();
                authDataFilterDO.setGroupId(groupId);
                authDataFilterDO.setConditionGroup(authDataFilterVO.getConditionGroup());
                authDataFilterDO.setConditionOrder(authDataFilterVO.getConditionOrder());
                authDataFilterDO.setFieldId(authDataFilterVO.getFieldId());
                authDataFilterDO.setFieldValueType(authDataFilterVO.getFieldValueType());
                authDataFilterDO.setFieldOperator(authDataFilterVO.getFieldOperator());
                authDataFilterDO.setFieldValue(authDataFilterVO.getFieldValue());
                authDataFilterRepository.insert(authDataFilterDO);
            } else if (authDataFilterDO != null && authDataFilterVO == null) {
                authDataFilterRepository.deleteById(authDataFilterDO.getId());
            }
        }
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

    private void updateAuthField(AuthPermissionReqVO reqVO, List<AuthFieldVO> authFieldVOS) {
        List<AuthFieldDO> authFieldDOS = authFieldRepository.findByQuery(reqVO);
        List<Pair<AuthFieldDO, AuthFieldVO>> pairs = AuthUtils.fullOuterJoin(authFieldDOS, authFieldVOS,
                (authFieldDO, authFieldVO) -> authFieldDO.getFieldId().equals(authFieldVO.getFieldId())
        );
        for (Pair<AuthFieldDO, AuthFieldVO> pair : pairs) {
            AuthFieldDO authFieldDO = pair.getLeft();
            AuthFieldVO authFieldVO = pair.getRight();
            if (authFieldDO != null && authFieldVO != null) {
                authFieldDO.setCanRead(authFieldVO.getCanRead());
                authFieldDO.setCanEdit(authFieldVO.getCanEdit());
                authFieldDO.setCanDownload(authFieldVO.getCanDownload());
                authFieldRepository.update(authFieldDO);
            } else if (authFieldDO == null && authFieldVO != null) {
                authFieldDO = new AuthFieldDO();
                authFieldDO.setApplicationId(reqVO.getApplicationId());
                authFieldDO.setRoleId(reqVO.getRoleId());
                authFieldDO.setMenuId(reqVO.getMenuId());
                authFieldRepository.insert(authFieldDO);
            } else if (authFieldDO != null && authFieldVO == null) {
                authFieldRepository.delete(authFieldDO);
            }
        }
    }

}
