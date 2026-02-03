package com.cmsr.onebase.module.app.build.service.version;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthDataGroupTableDef.APP_AUTH_DATA_GROUP;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthFieldTableDef.APP_AUTH_FIELD;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthPermissionTableDef.APP_AUTH_PERMISSION;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleTableDef.APP_AUTH_ROLE;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthViewTableDef.APP_AUTH_VIEW;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppMenuTableDef.APP_MENU;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceComponentTableDef.APP_RESOURCE_COMPONENT;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePageTableDef.APP_RESOURCE_PAGE;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetTableDef.APP_RESOURCE_PAGESET;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceWorkbenchComponentTableDef.APP_RESOURCE_WORKBENCH_COMPONENT;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceWorkbenchPageTableDef.APP_RESOURCE_WORKBENCH_PAGE;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import com.cmsr.onebase.module.metadata.api.datasource.dto.export.MetadataExportDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmsr.onebase.framework.common.util.io.ZipUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.core.dal.database.app.AppNavigationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthViewRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppWorkbenchComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppWorkbenchPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import com.mybatisflex.core.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppDataManager {

    @Autowired
    private AppWorkbenchComponentRepository workbenchComponentRepository;

    @Autowired
    private AppComponentRepository resourceComponentRepository;

    @Autowired
    private AppWorkbenchPageRepository workbenchPageRepository;

    @Autowired
    private AppPageRepository resourcePageRepository;

    @Autowired
    private AppPageSetRepository resourcePageSetRepository;

    @Autowired
    private AppMenuRepository menuRepository;

    @Autowired
    private AppAuthViewRepository authViewRepository;

    @Autowired
    private AppAuthFieldRepository authFieldRepository;

    @Autowired
    private AppAuthDataGroupRepository authDataGroupRepository;

    @Autowired
    private AppAuthPermissionRepository authPermissionRepository;

    @Autowired
    private AppNavigationRepository navigationRepository;

    @Autowired
    private AppAuthRoleRepository authRoleRepository;

    @Autowired
    private MetadataDatasourceApi metadataDatasourceApi;

    @Autowired
    private BpmDataManager bpmDataManager;

    public void moveRuntimeToHistory(Long applicationId, Long versionTag) {
        workbenchComponentRepository.moveRuntimeToHistory(applicationId, versionTag);
        resourceComponentRepository.moveRuntimeToHistory(applicationId, versionTag);
        workbenchPageRepository.moveRuntimeToHistory(applicationId, versionTag);
        resourcePageRepository.moveRuntimeToHistory(applicationId, versionTag);
        resourcePageSetRepository.moveRuntimeToHistory(applicationId, versionTag);
        menuRepository.moveRuntimeToHistory(applicationId, versionTag);
        authViewRepository.moveRuntimeToHistory(applicationId, versionTag);
        authFieldRepository.moveRuntimeToHistory(applicationId, versionTag);
        authDataGroupRepository.moveRuntimeToHistory(applicationId, versionTag);
        authPermissionRepository.moveRuntimeToHistory(applicationId, versionTag);
        navigationRepository.moveRuntimeToHistory(applicationId, versionTag);
    }

    public void copyEditToRuntime(Long applicationId) {
        workbenchComponentRepository.copyEditToRuntime(applicationId);
        resourceComponentRepository.copyEditToRuntime(applicationId);
        workbenchPageRepository.copyEditToRuntime(applicationId);
        resourcePageRepository.copyEditToRuntime(applicationId);
        resourcePageSetRepository.copyEditToRuntime(applicationId);
        menuRepository.copyEditToRuntime(applicationId);
        authViewRepository.copyEditToRuntime(applicationId);
        authFieldRepository.copyEditToRuntime(applicationId);
        authDataGroupRepository.copyEditToRuntime(applicationId);
        authPermissionRepository.copyEditToRuntime(applicationId);
        navigationRepository.copyEditToRuntime(applicationId);
    }

    // 3、历史版本数据回滚为运行态数据
    public void copyHistoryToRuntime(Long applicationId, Long versionTag) {
        // 实现回滚逻辑
        // 执行select、insert 动作。
        // 1、select：查询versionTag为参数`versionTag`值的数据
        // 2、insert：插入第一步查询出来的数据，versionTag为1
    }

    // 4、历史版本数据回滚为编辑态数据
    public void historyToEdit(Long applicationId, Long versionTag) {
        // 实现回滚到编辑态逻辑，没想好！
    }

    public void deleteApplicationVersionData(Long applicationId, Long versionTag) {
        workbenchComponentRepository.deleteApplicationVersionData(applicationId, versionTag);
        resourceComponentRepository.deleteApplicationVersionData(applicationId, versionTag);
        workbenchPageRepository.deleteApplicationVersionData(applicationId, versionTag);
        resourcePageRepository.deleteApplicationVersionData(applicationId, versionTag);
        resourcePageSetRepository.deleteApplicationVersionData(applicationId, versionTag);
        menuRepository.deleteApplicationVersionData(applicationId, versionTag);
        authViewRepository.deleteApplicationVersionData(applicationId, versionTag);
        authFieldRepository.deleteApplicationVersionData(applicationId, versionTag);
        authDataGroupRepository.deleteApplicationVersionData(applicationId, versionTag);
        authPermissionRepository.deleteApplicationVersionData(applicationId, versionTag);
        navigationRepository.deleteApplicationVersionData(applicationId, versionTag);
    }

    public void deleteAllApplicationData(Long applicationId) {
        workbenchComponentRepository.deleteAllApplicationData(applicationId);
        resourceComponentRepository.deleteAllApplicationData(applicationId);
        workbenchPageRepository.deleteAllApplicationData(applicationId);
        resourcePageRepository.deleteAllApplicationData(applicationId);
        resourcePageSetRepository.deleteAllApplicationData(applicationId);
        menuRepository.deleteAllApplicationData(applicationId);
        authViewRepository.deleteAllApplicationData(applicationId);
        authFieldRepository.deleteAllApplicationData(applicationId);
        authDataGroupRepository.deleteAllApplicationData(applicationId);
        authPermissionRepository.deleteAllApplicationData(applicationId);
        authRoleRepository.deleteAllApplicationData(applicationId);
        navigationRepository.deleteAllApplicationData(applicationId);
    }

    public void deleteAuthRole(Long applicationId){
        authRoleRepository.deleteAllApplicationData(applicationId);
    }

    /**
     * 获取应用版本配置数据
     *
     * @param applicationId 应用ID
     * @param versionTag    版本标签
     * @return 应用版本配置数据
     */
    public ApplicationVersionConfigData getApplicationVersionConfigData(Long applicationId, Long versionTag) {
        ApplicationVersionConfigData configData = new ApplicationVersionConfigData();

        // 工作台组件
        QueryWrapper workbenchComponentQuery = workbenchComponentRepository.query()
                .where(APP_RESOURCE_WORKBENCH_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_WORKBENCH_COMPONENT.VERSION_TAG.eq(versionTag));
        configData.setWorkbenchComponents(workbenchComponentRepository.list(workbenchComponentQuery));

        // 组件
        QueryWrapper componentQuery = resourceComponentRepository.query()
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_COMPONENT.VERSION_TAG.eq(versionTag));
        configData.setComponents(resourceComponentRepository.list(componentQuery));

        // 工作台页面
        QueryWrapper workbenchPageQuery = workbenchPageRepository.query()
                .where(APP_RESOURCE_WORKBENCH_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_WORKBENCH_PAGE.VERSION_TAG.eq(versionTag));
        configData.setWorkbenchPages(workbenchPageRepository.list(workbenchPageQuery));

        // 页面
        QueryWrapper pageQuery = resourcePageRepository.query()
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.VERSION_TAG.eq(versionTag));
        configData.setPages(resourcePageRepository.list(pageQuery));

        // 页面集
        QueryWrapper pageSetQuery = resourcePageSetRepository.query()
                .where(APP_RESOURCE_PAGESET.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGESET.VERSION_TAG.eq(versionTag));
        configData.setPageSets(resourcePageSetRepository.list(pageSetQuery));

        // 菜单
        QueryWrapper menuQuery = menuRepository.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.VERSION_TAG.eq(versionTag));
        configData.setMenus(menuRepository.list(menuQuery));

        // 权限视图
        QueryWrapper authViewQuery = authViewRepository.query()
                .where(APP_AUTH_VIEW.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_VIEW.VERSION_TAG.eq(versionTag));
        configData.setAuthViews(authViewRepository.list(authViewQuery));

        // 权限字段
        QueryWrapper authFieldQuery = authFieldRepository.query()
                .where(APP_AUTH_FIELD.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_FIELD.VERSION_TAG.eq(versionTag));
        configData.setAuthFields(authFieldRepository.list(authFieldQuery));

        // 权限数据组
        QueryWrapper authDataGroupQuery = authDataGroupRepository.query()
                .where(APP_AUTH_DATA_GROUP.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_DATA_GROUP.VERSION_TAG.eq(versionTag));
        configData.setAuthDataGroups(authDataGroupRepository.list(authDataGroupQuery));

        // 权限
        QueryWrapper authPermissionQuery = authPermissionRepository.query()
                .where(APP_AUTH_PERMISSION.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_PERMISSION.VERSION_TAG.eq(versionTag));
        configData.setAuthPermissions(authPermissionRepository.list(authPermissionQuery));

        // 导航
        configData.setNavigation(navigationRepository.findByApplicationIdAndVersionTag(applicationId, versionTag));

        // 权限角色
        QueryWrapper authRoleQuery = authRoleRepository.query()
                .where(APP_AUTH_ROLE.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_ROLE.VERSION_TAG.eq(versionTag));
        configData.setAuthRoles(authRoleRepository.list(authRoleQuery));

        // Bpm流程
        Object bpmConfig = bpmDataManager.exportApplication(applicationId, versionTag);
        configData.setBpmConfig(bpmConfig);

        // 元数据配置
        Object metadataConfig = metadataDatasourceApi.exportDatasource(applicationId, versionTag);
        configData.setMetaDataConfig(metadataConfig);

        return configData;
    }

    /**
     * 保存导入的应用版本配置数据
     *
     * @param applicationId 应用ID
     * @param tenantId      租户ID
     * @param versionTag    版本标签
     * @param configData    配置数据
     */
    public void saveApplicationVersionConfigData(Long applicationId, String appUid, Long tenantId, Long versionTag,
            ApplicationVersionConfigData configData) {
        if (configData == null) {
            return;
        }
        VersionDataImporter.saveList(configData.getWorkbenchComponents(), applicationId, tenantId, versionTag,
                workbenchComponentRepository);
        VersionDataImporter.saveList(configData.getWorkbenchPages(), applicationId, tenantId, versionTag,
                workbenchPageRepository);
        VersionDataImporter.saveList(configData.getPages(), applicationId, tenantId, versionTag,
                resourcePageRepository);
        VersionDataImporter.saveList(configData.getPageSets(), applicationId, tenantId, versionTag,
                resourcePageSetRepository);
        VersionDataImporter.saveList(configData.getComponents(), applicationId, tenantId, versionTag,
                resourceComponentRepository);
        VersionDataImporter.saveList(configData.getMenus(), applicationId, tenantId, versionTag, menuRepository);
        VersionDataImporter.saveList(configData.getAuthViews(), applicationId, tenantId, versionTag,
                authViewRepository);
        VersionDataImporter.saveList(configData.getAuthFields(), applicationId, tenantId, versionTag,
                authFieldRepository);
        VersionDataImporter.saveList(configData.getAuthDataGroups(), applicationId, tenantId, versionTag,
                authDataGroupRepository);
        VersionDataImporter.saveList(configData.getAuthPermissions(), applicationId, tenantId, versionTag,
                authPermissionRepository);
        VersionDataImporter.saveAppEntityList(configData.getAuthRoles(), applicationId, tenantId, authRoleRepository);

        AppNavigationDO navigation = configData.getNavigation();
        if (navigation != null) {
            VersionDataImporter.prepareEntity(navigation, applicationId, tenantId, versionTag);
            navigationRepository.save(navigation);
        }

        Object bpmConfig = configData.getBpmConfig();
        if (bpmConfig != null) {
            bpmDataManager.importApplication(applicationId, tenantId, versionTag, bpmConfig);
        }

        Object metaDataConfig = configData.getMetaDataConfig();

        if (metaDataConfig != null) {
            metadataDatasourceApi.importDatasource(applicationId, appUid, tenantId, versionTag, metaDataConfig, null);
        }
    }

    /**
     * 配置文件名常量，与 AppVersionServiceImpl 单版本导出保持一致
     */
    private static final String CONFIG_WORKBENCH_COMPONENTS = "config/workbenchComponents.json";
    private static final String CONFIG_COMPONENTS = "config/components.json";
    private static final String CONFIG_WORKBENCH_PAGES = "config/workbenchPages.json";
    private static final String CONFIG_PAGES = "config/pages.json";
    private static final String CONFIG_PAGE_SETS = "config/pageSets.json";
    private static final String CONFIG_MENUS = "config/menus.json";
    private static final String CONFIG_AUTH_VIEWS = "config/authViews.json";
    private static final String CONFIG_AUTH_FIELDS = "config/authFields.json";
    private static final String CONFIG_AUTH_DATA_GROUPS = "config/authDataGroups.json";
    private static final String CONFIG_AUTH_PERMISSIONS = "config/authPermissions.json";
    private static final String CONFIG_NAVIGATION = "config/navigation.json";
    private static final String CONFIG_AUTH_ROLES = "config/authRoles.json";
    private static final String CONFIG_METADATA = "config/metadata.json";
    private static final String CONFIG_BPM = "config/bpm.json";

    /**
     * 将应用版本配置数据写入 ZIP，支持路径前缀（全应用导出时按版本分子目录）
     *
     * @param zos        ZIP 输出流
     * @param configData 配置数据
     * @param pathPrefix 路径前缀，单版本导出传空串，多版本时可传 "versions/{versionId}/"
     * @throws IOException IO 异常
     */
    public void writeConfigDataToZip(ZipOutputStream zos, ApplicationVersionConfigData configData, String pathPrefix)
            throws IOException {
        Map<String, Object> configMap = new LinkedHashMap<>();
        configMap.put(CONFIG_WORKBENCH_COMPONENTS, configData.getWorkbenchComponents());
        configMap.put(CONFIG_COMPONENTS, configData.getComponents());
        configMap.put(CONFIG_WORKBENCH_PAGES, configData.getWorkbenchPages());
        configMap.put(CONFIG_PAGES, configData.getPages());
        configMap.put(CONFIG_PAGE_SETS, configData.getPageSets());
        configMap.put(CONFIG_MENUS, configData.getMenus());
        configMap.put(CONFIG_AUTH_VIEWS, configData.getAuthViews());
        configMap.put(CONFIG_AUTH_FIELDS, configData.getAuthFields());
        configMap.put(CONFIG_AUTH_DATA_GROUPS, configData.getAuthDataGroups());
        configMap.put(CONFIG_AUTH_PERMISSIONS, configData.getAuthPermissions());
        configMap.put(CONFIG_NAVIGATION, configData.getNavigation());
        configMap.put(CONFIG_AUTH_ROLES, configData.getAuthRoles());
        configMap.put(CONFIG_METADATA, configData.getMetaDataConfig());
        configMap.put(CONFIG_BPM, configData.getBpmConfig());

        String prefix = pathPrefix != null ? pathPrefix : "";
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            ZipUtils.writeJsonToZip(zos, prefix + entry.getKey(), entry.getValue());
        }
    }
}
