package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;
import com.cmsr.onebase.module.app.dal.database.auth.*;
import com.cmsr.onebase.module.app.dal.dataobject.auth.*;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
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
    public AuthPermissionVO getPermission(AuthPermissionReqVO reqVO) {
        AuthPermissionVO authPermissionVO = new AuthPermissionVO();
        //补充 功能权限
        AuthFeatureDO authFeatureDO = authFeatureRepository.findByQuery(reqVO);
        authPermissionVO.setAuthFeature(BeanUtils.toBean(authFeatureDO, AuthFeatureVO.class));
        //补充 操作权限
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(reqVO);
        authPermissionVO.setAuthOperations(BeanUtils.toBean(authOperationDOS, AuthOperationVO.class));
        //补充 实体权限
        List<AuthEntityDO> authEntityDOS = authEntityRepository.findByQuery(reqVO);
        authPermissionVO.setAuthEntities(BeanUtils.toBean(authEntityDOS, AuthEntityVO.class));
        //补充 数据权限
        List<AuthDataGroupVO> authDataGroupVOS = getAuthDataGroups(reqVO);
        authPermissionVO.setAuthDataGroups(authDataGroupVOS);
        //补充 字段权限
        List<AuthFieldVO> authFieldVOS = authFieldRepository.findByQuery(reqVO);
        authPermissionVO.setAuthFields(authFieldVOS);
        return authPermissionVO;
    }

    private List<AuthDataGroupVO> getAuthDataGroups(AuthPermissionReqVO reqVO) {
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
    public void updatePermission(AuthPermissionVO authPermissionVO) {
        AuthPermissionReqVO reqVO = new AuthPermissionReqVO();
        reqVO.setApplicationId(authPermissionVO.getApplicationId());
        reqVO.setRoleId(authPermissionVO.getRoleId());
        reqVO.setMenuId(authPermissionVO.getMenuId());
        //更新 功能权限
        updateAuthFeature(reqVO, authPermissionVO.getAuthFeature());
        updateAuthOperation(reqVO, authPermissionVO.getAuthOperations());
        updateAuthEntity(reqVO, authPermissionVO.getAuthEntities());
        updateAuthDataGroup(reqVO, authPermissionVO.getAuthDataGroups());
        updateAuthField(reqVO, authPermissionVO.getAuthFields());
    }

    private void updateAuthFeature(AuthPermissionReqVO reqVO, AuthFeatureVO authFeature) {
        AuthFeatureDO authFeatureDO = authFeatureRepository.findByQuery(reqVO);
        if (authFeatureDO == null) {
            authFeatureDO = new AuthFeatureDO();
            authFeatureDO.setApplicationId(reqVO.getApplicationId());
            authFeatureDO.setRoleId(reqVO.getRoleId());
            authFeatureDO.setMenuId(reqVO.getMenuId());
            authFeatureDO.setPageAllowed(authFeature.getPageAllowed());
            authFeatureDO.setAllEntitiesAllowed(authFeature.getAllEntitiesAllowed());
            authFeatureRepository.insert(authFeatureDO);
        } else {
            authFeatureDO.setPageAllowed(authFeature.getPageAllowed());
            authFeatureDO.setAllEntitiesAllowed(authFeature.getAllEntitiesAllowed());
            authFeatureRepository.update(authFeatureDO);
        }
    }

    private void updateAuthOperation(AuthPermissionReqVO reqVO, List<AuthOperationVO> authOperations) {
        List<AuthOperationDO> authOperationDOS = authOperationRepository.findByQuery(reqVO);

    }

    private void updateAuthEntity(AuthPermissionReqVO reqVO, List<AuthEntityVO> authEntities) {

    }

    private void updateAuthDataGroup(AuthPermissionReqVO reqVO, List<AuthDataGroupVO> authDataGroups) {

    }

    private void updateAuthField(AuthPermissionReqVO reqVO, List<AuthFieldVO> authFields) {

    }

}
