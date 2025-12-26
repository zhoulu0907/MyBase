package com.cmsr.onebase.module.app.core.impl.app;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.app.AppNavigationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationTagDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppTagDO;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
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

    @Autowired
    private AppNavigationRepository navigationRepository;

    @Override
    public Long countApplicationByTenantId(Long tenantId) {
        Long count = TenantManager.withoutTenantCondition(() -> appApplicationRepository.countByTenantId(tenantId));
        return count;
    }

    @Override
    public List<ApplicationDTO> findAppApplicationByAppName(String appName) {
        List<AppApplicationDO> applicationList = appApplicationRepository.findAppApplicationByAppName(appName, AppStatusEnum.ONLINE.getValue());
        List<ApplicationDTO> applicationDTOList = applicationList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        List<Long> appIds = applicationList.stream()
                .map(AppApplicationDO::getId)  // 获取每个 AppApplicationDO 的 id
                .filter(Objects::nonNull)      // 过滤掉 null 值
                .collect(Collectors.toList());
        return getAppApplicationNavigation(applicationDTOList, appIds);
    }

    @Override
    public List<ApplicationDTO> findAppApplicationByAppIds(Collection<Long> appIds) {
        List<AppApplicationDO> applicationList = appApplicationRepository.findAppApplicationByAppIds(appIds);
        List<ApplicationDTO> applicationDTOList = applicationList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        List<Long> appIdList = appIds.stream()
                .filter(Objects::nonNull)  // 过滤空值
                .collect(Collectors.toList());
        return getAppApplicationNavigation(applicationDTOList, appIdList);
    }
    /**
     * 补充app对应的图标，颜色等
     *
     */
    public List<ApplicationDTO> getAppApplicationNavigation(List<ApplicationDTO> applicationDTOList, List<Long> listIds) {
        // 检查输入参数
        if (CollectionUtils.isEmpty(applicationDTOList) || CollectionUtils.isEmpty(listIds)) {
            return applicationDTOList;
        }

        // 获取导航列表并按 applicationId 分组，每组取第一条数据
        List<AppNavigationDO> navigationList = ApplicationManager.withoutApplicationCondition(() ->
                navigationRepository.findByApplicationIds(listIds));

        // 检查导航列表是否为空
        if (CollectionUtils.isEmpty(navigationList)) {
            return applicationDTOList;
        }

        // 按更新日期倒序排列
        List<AppNavigationDO> sortedNavigationList = navigationList.stream()
                .sorted(Comparator.comparing(AppNavigationDO::getUpdateTime).reversed())
                .toList();

        // 按 applicationId 分组并获取每组的第一条记录
        Map<Long, AppNavigationDO> navigationMap = sortedNavigationList.stream()
                .filter(navigation -> navigation.getApplicationId() != null)  // 过滤 applicationId 为空的记录
                .collect(Collectors.groupingBy(
                        AppNavigationDO::getApplicationId,  // 按 applicationId 分组
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.isEmpty() ? null : list.get(0)  // 每组取第一条数据
                        )
                ));

        // 将导航数据赋值给 applicationList 中的每个 ApplicationDTO 对象
        applicationDTOList.forEach(app -> {
            if (app != null) {  // 检查 app 对象是否为空
                AppNavigationDO navigation = navigationMap.get(app.getId()); // 假设 getId() 返回 appId
                if (navigation != null) {
                    app.setIconColor(navigation.getIconColor());
                    app.setIconName(navigation.getIconName());
                    app.setThemeColor(navigation.getThemeColor());
                }
            }
        });
        return applicationDTOList;
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
