package com.cmsr.onebase.module.app.build.service.version;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.io.ZipUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.vo.version.VersionImportReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthViewDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.AppPublishEnum;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
import com.cmsr.onebase.module.app.core.enums.version.VersionTypeEnum;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.flow.api.FlowDataManager;
import com.cmsr.onebase.module.metadata.api.version.MetadataDataManagerApi;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 *                  @Date：2025/7/24 11:04
 */
@Setter
@Service
@Validated
@Slf4j
public class AppVersionServiceImpl implements AppVersionService {

    /**
     * 配置文件名常量
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

    @Autowired
    private AppApplicationRepository applicationRepository;

    @Autowired
    private AppVersionRepository versionRepository;

    @Autowired
    private AppCommonService appCommonService;

    @Autowired
    private AppDataManager appDataManager;

    @Autowired
    private BpmDataManager bpmDataManager;

    @Autowired
    private FlowDataManager flowDataManager;

    @Autowired
    private MetadataDataManagerApi metadataVersionManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo) {
        PageResult<AppVersionDO> pageResult = versionRepository.selectPage(listReqVo.getApplicationId(), listReqVo);
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());
        List<VersionPageRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    VersionPageRespVO bean = BeanUtils.toBean(v, VersionPageRespVO.class);
                    bean.setCreatorName(userHelper.getUserNickname(v.getCreator()));
                    bean.setUpdaterName(userHelper.getUserNickname(v.getUpdater()));
                    bean.setVersionTypeLabel(VersionTypeEnum.getLabel(v.getVersionType()));
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    @Override
    public void onlineApplication(VersionOnlineReq createReqVO) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        Long applicationId = applicationDO.getId();
        validateVersionUnique(applicationId, createReqVO.getVersionNumber());
        //
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // 找打当前Runtime版本信息，肯定能找到，因为发布的时候会同步创建一个，把当前版本信息变成历史状态
            AppVersionDO currentRunVersion = versionRepository.findByApplicationIdAndVersionType(applicationId,
                    VersionTypeEnum.RUNTIME.getValue());
            if (currentRunVersion != null) {
                currentRunVersion.setVersionType(VersionTypeEnum.HISTORY.getValue());
                versionRepository.updateById(currentRunVersion);
                Long historyVersionTag = currentRunVersion.getId();
                // 备份当前版本为历史版本
                metadataVersionManager.moveMetaDataRuntimeToHistory(applicationId, historyVersionTag);
                appDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                bpmDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                flowDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
            }
            // 发布上线版本
            metadataVersionManager.copyMetaDataEditToRuntime(applicationId);
            appDataManager.copyEditToRuntime(applicationId);
            bpmDataManager.copyEditToRuntime(applicationId);
            flowDataManager.copyEditToRuntime(applicationId);
            // 创建新的版本信息
            AppVersionDO newRunVersionDO = createNewVersion(createReqVO, applicationId);
            applicationRepository.updateStatusByApplicationId(applicationId, AppStatusEnum.ONLINE,
                    AppPublishEnum.ONCE_PUBLISHED);
            versionRepository.save(newRunVersionDO);
        });
        // online services that required
        flowDataManager.updateRuntimeData(applicationId);
    }

    private void validateVersionUnique(Long applicationId, String versionNumber) {
        long count = versionRepository.countByApplicationIdAndName(applicationId, versionNumber);
        if (count > 0) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.VERSION_DUPLICATE);
        }
    }

    @Override
    public void offlineApplication() {
        Long applicationId = ApplicationManager.getRequiredApplicationId();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            AppVersionDO currentRunVersion = versionRepository.findByApplicationIdAndVersionType(applicationId,
                    VersionTypeEnum.RUNTIME.getValue());
            if (currentRunVersion != null) {
                currentRunVersion.setVersionType(VersionTypeEnum.HISTORY.getValue());
                versionRepository.updateById(currentRunVersion);
                Long historyVersionTag = currentRunVersion.getId();
                // 备份当前版本为历史版本
                metadataVersionManager.moveMetaDataRuntimeToHistory(applicationId, historyVersionTag);
                appDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                bpmDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                flowDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
            }
            applicationRepository.updateAppStatusByApplicationId(applicationId, AppStatusEnum.OFFLINE);
        });
        flowDataManager.deleteRuntimeData(applicationId);
    }

    private AppVersionDO createNewVersion(VersionOnlineReq createReqVO, Long applicationId) {
        AppVersionDO newRunVersionDO = new AppVersionDO();
        newRunVersionDO.setApplicationId(applicationId);
        newRunVersionDO.setVersionName(createReqVO.getVersionName());
        newRunVersionDO.setVersionNumber(createReqVO.getVersionNumber());
        newRunVersionDO.setVersionDescription(createReqVO.getVersionDescription());
        newRunVersionDO.setOperationType(createReqVO.getOperationType());
        newRunVersionDO.setEnvironment(createReqVO.getEnvironment());
        newRunVersionDO.setVersionType(VersionTypeEnum.RUNTIME.getValue());
        return newRunVersionDO;
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
        // 获取历史版本对象
    }

    @Override
    public void deleteApplicationVersion(Long versionId) {
        AppVersionDO versionDO = versionRepository.getById(versionId);
        if (versionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        if (versionDO.getVersionType() == VersionTypeEnum.BUILD.getValue()) {
            throw new IllegalArgumentException("不允许删除当前运行的版本");
        }
        if (versionDO.getVersionType() == VersionTypeEnum.RUNTIME.getValue()) {
            throw new IllegalArgumentException("不允许删除当前运行的版本");
        }
        Long applicationId = versionDO.getApplicationId();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // 删除对应的信息
            metadataVersionManager.deleteApplicationVersionData(applicationId, versionId);
            bpmDataManager.removeApplicationVersion(applicationId, versionId);
            appDataManager.deleteApplicationVersionData(applicationId, versionId);
            flowDataManager.deleteApplicationVersionData(applicationId, versionId);
            // 删除版本
            versionRepository.removeById(versionId);
        });
    }

    /**
     * 导出应用版本配置压缩包
     *
     * @param versionId 版本ID
     * @param response  HTTP响应对象
     */
    @Override
    public void exportApplicationVersion(Long versionId, HttpServletResponse response) {
        // 获取版本信息
        AppVersionDO versionDO = versionRepository.getById(versionId);
        if (versionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }

        // 获取应用信息
        Long applicationId = versionDO.getApplicationId();
        AppApplicationDO applicationDO = applicationRepository.getById(applicationId);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }

        // 获取版本配置数据
        Long versionTag = versionId;
        ApplicationVersionConfigData configData = ApplicationManager
                .withoutVersionTagCondition(() -> appDataManager.getApplicationVersionConfigData(applicationId,
                        versionTag));

        // 设置响应头
        String fileName = String.format("%s_%s_%s_config.zip", applicationDO.getAppName(), versionDO.getVersionName(),
                versionDO.getVersionNumber());
        try {
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            log.warn("文件名编码转换失败", e);
        }
        response.setContentType("application/zip");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 创建ZIP文件
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8)) {
            // 写入版本信息
            ZipUtils.writeJsonToZip(zos, "version.json", versionDO);

            // 写入应用信息
            ZipUtils.writeJsonToZip(zos, "application.json", applicationDO);

            // 写入配置数据
            writeConfigDataToZip(zos, configData);

            zos.finish();
            response.flushBuffer();
        } catch (IOException e) {
            log.error("导出应用版本配置失败", e);
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_ERROR);
        }
    }

    @Override
    public void importApplicationVersion(VersionImportReq versionImportReq) {
        MultipartFile file = versionImportReq.getFile();
        if (file == null || file.isEmpty()) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_IMPORT_FILE_EMPTY);
        }

        // 解析导入文件
        ImportPackage importPackage = parseImportFile(file);
        if (importPackage.getVersionDO() == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_IMPORT_ERROR);
        }

        // 创建全新的应用
        AppApplicationDO importApp = importPackage.getApplicationDO();
        if (importApp == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_IMPORT_ERROR);
        }

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // 拷贝导入的应用信息，创建新应用（去除原有ID，仅保留必要信息）
            String appUid = appCommonService.findAndCreateAppUid();
            AppApplicationDO newApp = applicationRepository.createImportedApplication(importApp, appUid,
                    TenantContextHolder.getRequiredTenantId());
            Long applicationId = newApp.getId();

            // 导入配置数据到新应用
            appDataManager.saveApplicationVersionConfigData(applicationId, appUid, newApp.getTenantId(), 0L,
                    importPackage.getConfigData());
        });
    }

    private ImportPackage parseImportFile(MultipartFile file) {
        Map<String, byte[]> entryMap;
        try {
            entryMap = ZipUtils.readZipEntries(file.getInputStream());
        } catch (IOException e) {
            log.error("读取导入压缩包失败", e);
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_IMPORT_ERROR);
        }
        ImportPackage importPackage = new ImportPackage();
        importPackage.setApplicationDO(JsonUtils.parseObject(entryMap.getOrDefault("application.json", new byte[0]),
                AppApplicationDO.class));
        importPackage.setVersionDO(JsonUtils.parseObject(entryMap.getOrDefault("version.json", new byte[0]),
                AppVersionDO.class));

        ApplicationVersionConfigData configData = new ApplicationVersionConfigData();
        parseConfigDataFromZip(entryMap, configData);

        importPackage.setConfigData(configData);
        return importPackage;
    }

    /**
     * 从ZIP文件中解析配置数据
     *
     * @param entryMap   ZIP条目映射
     * @param configData 配置数据对象
     */
    private void parseConfigDataFromZip(Map<String, byte[]> entryMap, ApplicationVersionConfigData configData) {

        Map<String, BiConsumer<ApplicationVersionConfigData, String>> arrayConfigs = new LinkedHashMap<>();
        arrayConfigs.put(CONFIG_WORKBENCH_COMPONENTS,
                (data, json) -> data.setWorkbenchComponents(
                        JsonUtils.parseArray(json, AppResourceWorkbenchComponentDO.class)));
        arrayConfigs.put(CONFIG_COMPONENTS,
                (data, json) -> data.setComponents(JsonUtils.parseArray(json, AppResourceComponentDO.class)));
        arrayConfigs.put(CONFIG_WORKBENCH_PAGES,
                (data, json) -> data.setWorkbenchPages(
                        JsonUtils.parseArray(json, AppResourceWorkbenchPageDO.class)));
        arrayConfigs.put(CONFIG_PAGES,
                (data, json) -> data.setPages(JsonUtils.parseArray(json, AppResourcePageDO.class)));
        arrayConfigs.put(CONFIG_PAGE_SETS,
                (data, json) -> data.setPageSets(JsonUtils.parseArray(json, AppResourcePagesetDO.class)));
        arrayConfigs.put(CONFIG_MENUS,
                (data, json) -> data.setMenus(JsonUtils.parseArray(json, AppMenuDO.class)));
        arrayConfigs.put(CONFIG_AUTH_VIEWS,
                (data, json) -> data.setAuthViews(JsonUtils.parseArray(json, AppAuthViewDO.class)));
        arrayConfigs.put(CONFIG_AUTH_FIELDS,
                (data, json) -> data.setAuthFields(JsonUtils.parseArray(json, AppAuthFieldDO.class)));
        arrayConfigs.put(CONFIG_AUTH_DATA_GROUPS,
                (data, json) -> data.setAuthDataGroups(JsonUtils.parseArray(json, AppAuthDataGroupDO.class)));
        arrayConfigs.put(CONFIG_AUTH_PERMISSIONS,
                (data, json) -> data.setAuthPermissions(JsonUtils.parseArray(json, AppAuthPermissionDO.class)));
        arrayConfigs.put(CONFIG_AUTH_ROLES,
                (data, json) -> data.setAuthRoles(JsonUtils.parseArray(json, AppAuthRoleDO.class)));

        // 解析Array类型配置
        for (Map.Entry<String, BiConsumer<ApplicationVersionConfigData, String>> entry : arrayConfigs.entrySet()) {
            String json = ZipUtils.toUtf8String(entryMap.get(entry.getKey()));
            if (json != null) {
                entry.getValue().accept(configData, json);
            }
        }

        // Object类型配置（navigation）
        String navigationJson = ZipUtils.toUtf8String(entryMap.get(CONFIG_NAVIGATION));
        if (navigationJson != null) {
            configData.setNavigation(JsonUtils.parseObject(navigationJson, AppNavigationDO.class));
        }
    }

    /**
     * 将配置数据写入ZIP文件
     *
     * @param zos        ZIP输出流
     * @param configData 配置数据
     * @throws IOException IO异常
     */
    private void writeConfigDataToZip(ZipOutputStream zos, ApplicationVersionConfigData configData)
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

        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            ZipUtils.writeJsonToZip(zos, entry.getKey(), entry.getValue());
        }
    }
}
