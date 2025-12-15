package com.cmsr.onebase.module.app.core.impl.app;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationTagDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppTagDO;
import com.mybatisflex.core.tenant.TenantManager;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:36
 */
@Setter
@Service
public class AppApplicationApiImpl implements AppApplicationApi {

    @Autowired
    private AppApplicationRepository appApplicationRepository;

    @Autowired
    private AppAuthFieldRepository authFieldRepository;

    @Autowired
    private AppMenuRepository menuRepository;

    @Autowired
    private AppComponentRepository componentRepository;

    @Resource
    private AppTagRepository tagRepository;


    @Resource
    private AppApplicationTagRepository applicationTagRepository;

    @Override
    public Long countApplicationByTenantId(Long tenantId) {
        Long count = TenantManager.withoutTenantCondition(() -> appApplicationRepository.countByTenantId(tenantId));
        return count;
    }

    @Override
    public List<ApplicationDTO> findAppApplicationByAppName(String appName) {
        List<AppApplicationDO> applicationList = appApplicationRepository.findAppApplicationByAppName(appName);
        return applicationList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> findAppApplicationByAppIds(Collection<Long> appIds) {
        List<AppApplicationDO> applicationList = appApplicationRepository.findAppApplicationByAppIds(appIds);
        return applicationList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationDTO findAppApplicationById(Long appId) {
        AppApplicationDO applicationDO = TenantManager.withoutTenantCondition(() -> appApplicationRepository.getById(appId));
        return convertToDTO(applicationDO);
    }

    private ApplicationDTO convertToDTO(AppApplicationDO applicationDO) {
        return BeanUtils.toBean(applicationDO, ApplicationDTO.class);
    }

    @Override
    public Map<Long, Integer> countAppByTenantId() {
        return appApplicationRepository.countAppByTenantId();
    }

    @Override
    public void updateAppTimeById(Long appId) {
        appApplicationRepository.updateAppTimeByApplicationId(appId);
    }

    @Override
    public Map<Long, List<TagVO>> queryAppTags(List<Long> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return Collections.emptyMap();
        }
        Map<Long, List<TagVO>> tagListMap = new HashMap<>();
        Map<Long, List<Long>> listMap = findTagIdsByApplicationIdsGrouped(appIds);
        listMap.forEach((appId, tagIds) -> {
            List<AppTagDO> tagDOList = tagRepository.listByIds(tagIds);
            List<TagVO> tagVOList = tagDOList.stream()
                    .map(tagDO -> BeanUtils.toBean(tagDO, TagVO.class))
                    .collect(Collectors.toList());
            tagListMap.put(appId, tagVOList);
        });

        return tagListMap;
    }

    @Override
    public boolean existsEntityRelation(String entityUuid, String entityName) {
        boolean refferedByMenu = menuRepository.existsByEntityUuid(entityUuid);
        if (refferedByMenu) {
            return true;
        }
        boolean refferedAsTable = componentRepository.existsEntityRefferedByTable(entityName);
        if (refferedAsTable) {
            return true;
        }
        boolean refferedAsSubTable = componentRepository.existsEntityRefferedBySubTable(entityUuid);
        if (refferedAsSubTable) {
            return true;
        }
        return false;
    }

    @Override
    public boolean existsEntityFieldRelation(String entityUuid, String entityName, String fieldUuid, String fieldName) {
        boolean refferedByComponent = componentRepository.existsFieldRefferedByComponent(entityName, fieldName);
        if (refferedByComponent) {
            return true;
        }
        boolean refferedByTable = componentRepository.existsFieldRefferedByTable(entityName, fieldName);
        if (refferedByTable) {
            return true;
        }
        // TODO: 需要产品确认，权限是否需要做判断
        return false;
    }

    public Map<Long, List<Long>> findTagIdsByApplicationIdsGrouped(List<Long> appIds) {
        List<AppApplicationTagDO> tagDOListIds = applicationTagRepository.findTagIdsByApplicationIds(appIds);
        return tagDOListIds.stream()
                .collect(Collectors.groupingBy(
                        AppApplicationTagDO::getApplicationId,
                        Collectors.mapping(AppApplicationTagDO::getTagId, Collectors.toList())
                ));
    }

}
