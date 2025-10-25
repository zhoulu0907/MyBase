package com.cmsr.onebase.module.app.api.permission;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.permission.dto.*;
import com.cmsr.onebase.module.app.core.dal.database.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author：matianyu
 */
@Setter
@Validated
@Service
public class AppPermissionApiImpl implements AppPermissionApi {

    @Resource
    private AppAuthRoleRepository appAuthRoleRepository;

    @Resource
    private AppAuthPermissionRepository appAuthPermissionRepository;

    @Resource
    private AppAuthViewRepository appAuthViewRepository;

    @Resource
    private AppAuthDataGroupRepository appAuthDataGroupRepository;

    @Resource
    private AppAuthDataFilterRepository appAuthDataFilterRepository;

    @Resource
    private AppAuthFieldRepository appAuthFieldRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Override
    public Long findEntityByMenuId(Long menuId) {
        MenuDO menuDO = appMenuRepository.findById(menuId);
        if (menuDO != null) {
            return menuDO.getEntityId();
        } else {
            return null;
        }
    }

    @Override
    public List<RoleDTO> findRoles(Long applicationId, Long userId) {
        return appAuthRoleRepository.findByApplicationIdAndUserId(applicationId, userId)
                .stream()
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    return roleDTO;
                }).toList();
    }

    @Override
    public List<PermissionDTO> findPermissions(Long applicationId, Long roleId) {
        return appAuthPermissionRepository.findByApplicationIdAndRoleId(applicationId, roleId)
                .stream()
                .map(permission -> {
                    PermissionDTO permissionDTO = new PermissionDTO();
                    BeanUtils.copyProperties(permission, permissionDTO);
                    return permissionDTO;
                }).toList();
    }

    @Override
    public List<ViewDTO> findViews(Long applicationId, Long roleId) {
        return appAuthViewRepository.findByApplicationIdAndRoleId(applicationId, roleId)
                .stream()
                .map(view -> {
                    ViewDTO viewDTO = new ViewDTO();
                    BeanUtils.copyProperties(view, viewDTO);
                    return viewDTO;
                }).toList();
    }

    @Override
    public List<DataGroupDTO> findDataGroups(Long applicationId, Long roleId) {
        List<DataGroupDTO> dataGroupDTOS = appAuthDataGroupRepository.findByApplicationIdAndRoleId(applicationId, roleId)
                .stream()
                .map(dataGroup -> {
                    DataGroupDTO dataGroupDTO = new DataGroupDTO();
                    BeanUtils.copyProperties(dataGroup, dataGroupDTO);
                    return dataGroupDTO;
                }).toList();
        for (DataGroupDTO dataGroupDTO : dataGroupDTOS) {
            List<DataFilterDTO> dataFilterDTOS = appAuthDataFilterRepository.findByGroupId(dataGroupDTO.getId())
                    .stream()
                    .map(dataFilter -> {
                        DataFilterDTO dataFilterDTO = new DataFilterDTO();
                        BeanUtils.copyProperties(dataFilter, dataFilterDTO);
                        return dataFilterDTO;
                    }).toList();
            dataGroupDTO.setDataFilters(dataFilterDTOS);
        }
        return dataGroupDTOS;
    }

    @Override
    public List<FieldDTO> findFields(Long applicationId, Long roleId) {
        return appAuthFieldRepository.findByApplicationIdAndRoleId(applicationId, roleId)
                .stream()
                .map(fieldDO -> {
                    FieldDTO fieldDTO = new FieldDTO();
                    BeanUtils.copyProperties(fieldDO, fieldDTO);
                    return fieldDTO;
                }).toList();
    }

}
