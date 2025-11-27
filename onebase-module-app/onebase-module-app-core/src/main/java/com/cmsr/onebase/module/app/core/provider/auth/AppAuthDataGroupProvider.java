package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionFilter;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionLevel;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionTag;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/28 20:06
 */
@Slf4j
@Setter
@Service
public class AppAuthDataGroupProvider {

    @Autowired
    private AppAuthDataGroupRepository appAuthDataGroupRepository;

    public List<DataPermissionGroup> findDataGroups(Long applicationId, Set<Long> roleIds, Long menuId) {
        List<AppAuthDataGroupDO> authDataGroupDOS = appAuthDataGroupRepository.findByAppIdAndRoleIdsAndMenuId(applicationId, roleIds, menuId);
        return authDataGroupDOS.stream().map(authDataGroupDO -> {
            DataPermissionGroup dataPermissionGroup = new DataPermissionGroup();
            if (authDataGroupDO.getId() == null) {
                authDataGroupDO = AuthDefaultFactory.createAuthDataGroupDO();
            }
            dataPermissionGroup.setScopTags(toDataPermissionTags(authDataGroupDO.getScopeTags()));
            dataPermissionGroup.setScopeFieldId(authDataGroupDO.getScopeFieldId());
            dataPermissionGroup.setScopeLevel(DataPermissionLevel.fromCode(authDataGroupDO.getScopeLevel()));
            dataPermissionGroup.setScopeValue(authDataGroupDO.getScopeValue());
            dataPermissionGroup.setFilters(toDataPermissionFilter(authDataGroupDO.getDataFilter()));
            List<String> operationTags = toOperationTags(authDataGroupDO.getOperationTags());
            for (String operationTag : operationTags) {
                if (operationTag.equalsIgnoreCase("edit")) {
                    dataPermissionGroup.setCanEdit(true);
                }
                if (operationTag.equalsIgnoreCase("delete")) {
                    dataPermissionGroup.setCanDelete(true);
                }
            }
            return dataPermissionGroup;
        }).toList();
    }


    private List<DataPermissionTag> toDataPermissionTags(String scopeTags) {
        if (StringUtils.isBlank(scopeTags)) {
            return Collections.emptyList();
        }
        List<String> tags = JsonUtils.parseArray(scopeTags, String.class);
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        return DataPermissionTag.createTags(tags);
    }

    private List<List<DataPermissionFilter>> toDataPermissionFilter(String dataFilter) {
        if (StringUtils.isBlank(dataFilter)) {
            return Collections.emptyList();
        }
        return JsonUtils.parseObject(dataFilter, new TypeReference<List<List<DataPermissionFilter>>>() {
        });
    }

    private List<String> toOperationTags(String operationTags) {
        if (StringUtils.isBlank(operationTags)) {
            return Collections.emptyList();
        }
        return JsonUtils.parseArray(operationTags, String.class);
    }

}
