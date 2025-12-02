package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author：huangjie
 * @Date：2025/10/28 20:15
 */
@Slf4j
@Setter
@Service
public class AppAuthFieldProvider {

    @Autowired
    private AppAuthFieldRepository appAuthFieldRepository;

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private AppMenuRepository appMenuRepository;

    public List<FieldPermissionItem> findFields(Long applicationId, Set<Long> roleIds, Long menuId) {
        List<String> roleUuids = appAuthRoleRepository.findUuidsByAppIdAndRoleIds(applicationId, roleIds);
        AppMenuDO appMenuDO = appMenuRepository.getById(menuId);
        List<AppAuthFieldDO> fieldDOS = appAuthFieldRepository.findByAppIdAndRoleIdsAndMenuId(applicationId, roleUuids, appMenuDO.getMenuUuid());
        Map<String, FieldPermissionItem> fieldPermissionItemMap = new HashMap<>();
        for (AppAuthFieldDO fieldDO : fieldDOS) {
            FieldPermissionItem item = fieldPermissionItemMap.get(fieldDO.getFieldUuid());
            if (item == null) {
                item = new FieldPermissionItem();
                item.setFieldUuid(fieldDO.getFieldUuid());
                item.setCanRead(false);
                item.setCanEdit(false);
                item.setCanDownload(false);
                fieldPermissionItemMap.put(fieldDO.getFieldUuid(), item);
            }
            item.setCanRead(NumberUtils.INTEGER_ONE.equals(fieldDO.getIsCanRead()));
            item.setCanEdit(NumberUtils.INTEGER_ONE.equals(fieldDO.getIsCanEdit()));
            item.setCanDownload(NumberUtils.INTEGER_ONE.equals(fieldDO.getIsCanDownload()));
        }
        return new ArrayList<>(fieldPermissionItemMap.values());
    }
}
