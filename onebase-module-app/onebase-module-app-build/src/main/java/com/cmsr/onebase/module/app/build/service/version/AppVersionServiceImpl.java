package com.cmsr.onebase.module.app.build.service.version;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.io.ZipUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.vo.version.ExportPageReqVO;
import com.cmsr.onebase.module.app.build.vo.version.ExportPageRespVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionImportReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.app.AppExportRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthViewDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppExportDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.AppExportStatusEnum;
import com.cmsr.onebase.module.app.core.enums.app.AppPublishEnum;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
import com.cmsr.onebase.module.app.core.enums.version.VersionTypeEnum;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.flow.api.FlowDataManager;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.module.metadata.api.datasource.dto.export.MetadataExportDataDTO;
import com.cmsr.onebase.module.metadata.api.version.MetadataDataManagerApi;

import jakarta.servlet.http.HttpServletRequest;
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

    /**
     * 自注入代理对象，用于调用异步方法（解决同一类中 @Async 不生效的问题）
     * 使用 @Lazy 避免循环依赖
     */
    @Lazy
    @Autowired
    private AppVersionServiceImpl self;

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

    @Autowired
    private AppExportRepository appExportRepository;

    @Autowired
    private FileApi fileApi;

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

        AppApplicationDO importApp = importPackage.getApplicationDO();
        if (importApp == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_IMPORT_ERROR);
        }

        Long applicationId = versionImportReq.getApplicationId();
        if (applicationId != null) {
            // 覆盖当前应用的开发版本
            AppApplicationDO existingApp = appCommonService.validateApplicationExist(applicationId);
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                // 删除现有的开发版本数据（version_tag 为 0）
                appDataManager.deleteApplicationVersionData(applicationId, VersionTagEnum.BUILD.getValue());

                // 导入配置数据到开发版本
                appDataManager.saveApplicationVersionConfigData(applicationId, existingApp.getAppUid(),
                        existingApp.getTenantId(), VersionTagEnum.BUILD.getValue(), importPackage.getConfigData());
            });
        } else {
            // 创建全新的应用
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                // 拷贝导入的应用信息，创建新应用（去除原有ID，仅保留必要信息）
                String appUid = appCommonService.findAndCreateAppUid();
                AppApplicationDO newApp = applicationRepository.createImportedApplication(importApp, appUid,
                        TenantContextHolder.getRequiredTenantId());
                Long newApplicationId = newApp.getId();

                // 导入配置数据到新应用
                appDataManager.saveApplicationVersionConfigData(newApplicationId, appUid, newApp.getTenantId(),
                        VersionTagEnum.BUILD.getValue(), importPackage.getConfigData());
                log.info("创建新应用，newApplicationId: {}", newApplicationId);
            });
        }
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

        String bpmJson = ZipUtils.toUtf8String(entryMap.get(CONFIG_BPM));
        if (bpmJson != null) {
            configData.setBpmConfig(JsonUtils.parseObject(bpmJson, Object.class));
        }

        String metadataJson = ZipUtils.toUtf8String(entryMap.get(CONFIG_METADATA));
        if (metadataJson != null) {
            configData.setMetaDataConfig(JsonUtils.parseObject(metadataJson, MetadataExportDataDTO.class));
        }

    }

    /**
     * 导出指定版本的应用配置为 ZIP
     *
     * @param versionId 版本ID
     * @return 导出记录ID
     */
    @Override
    public Long exportApplicationVersion(Long versionId, Long applicationId) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(applicationId);

        AppVersionDO versionDO = null;
        // 验证版本是否存在且属于当前应用
        if (versionId.intValue() == VersionTypeEnum.BUILD.getValue()
                || versionId.intValue() == VersionTypeEnum.RUNTIME.getValue()) {
            versionDO = new AppVersionDO();
            versionDO.setApplicationId(applicationId);
            versionDO.setVersionType(versionId.intValue());
        } else {
            versionDO = ApplicationManager
                    .withoutApplicationIdAndVersionTag(
                            () -> versionRepository.getById(versionId));
        }

        if (versionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        if (!versionDO.getApplicationId().equals(applicationId)) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }

        // 创建导出记录，状态为导出中
        AppExportDO exportDO = new AppExportDO();
        exportDO.setApplicationId(applicationId);
        exportDO.setExportStatus(AppExportStatusEnum.EXPORTING.getValue());
        exportDO.setVersionId(versionId);
        appExportRepository.save(exportDO);
        Long exportId = exportDO.getId();

        // 通过代理对象调用异步方法，确保 @Async 生效
        self.asyncExportApplication(exportId, applicationDO, versionDO);

        return exportId;
    }

    /**
     * 根据导出记录ID获取导出资源
     * <p>
     * 如果导出状态为 SUCCESS，则通过 response 返回文件流；否则返回当前状态值
     *
     * @param exportId 导出记录ID
     * @param request  HTTP 请求
     * @param response HTTP 响应，用于输出文件流（仅当状态为成功时）
     */
    @Override
    public void getExportApplicationResource(Long exportId, HttpServletRequest request,
            HttpServletResponse response) {
        // 查询导出记录
        AppExportDO exportDO = appExportRepository.getById(exportId);
        if (exportDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_ERROR);
        }

        String objectId = exportDO.getObjectId();
        Long applicationId = exportDO.getApplicationId();

        // 将 objectId（文件ID）转换为 Long，通过 FileApi 获取文件内容并写入 response
        try {
            Long fileId = Long.parseLong(objectId);
            // 使用 getFileContentBytes 获取文件字节数组
            CommonResult<byte[]> result = fileApi.getFileContentBytes(fileId);
            if (result == null || !result.isSuccess() || result.getData() == null) {
                log.error("获取文件内容失败，exportId: {}, fileId: {}", exportId, fileId);
                throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_ERROR);
            }

            byte[] fileContent = result.getData();
            String fileName = applicationId + ".zip";

            // 设置响应头
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // 将文件内容写入 response
            response.getOutputStream().write(fileContent);
            response.getOutputStream().flush();

        } catch (Exception e) {
            log.error("获取导出资源失败，exportId: {}, objectId: {}", exportId, objectId, e);
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_ERROR);
        }
    }

    /**
     * 异步执行应用导出
     *
     * @param exportId      导出记录ID
     * @param applicationDO 应用信息
     * @param versionDO     版本信息
     */
    @Async
    public void asyncExportApplication(Long exportId, AppApplicationDO applicationDO, AppVersionDO versionDO) {
        log.info("异步执行应用导出，exportId: {}, applicationDO: {}, versionDO: {}", exportId, applicationDO, versionDO);

        try {
            String fileName = applicationDO.getAppName() + "_" + versionDO.getVersionNumber() + "_config.zip";

            // 将 ZIP 写入内存
            byte[] zipBytes;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
                ZipUtils.writeJsonToZip(zos, "application.json", applicationDO);
                ZipUtils.writeJsonToZip(zos, "version.json", versionDO);

                long versionTag = resolveVersionTag(versionDO);
                ApplicationVersionConfigData configData = ApplicationManager
                        .withoutApplicationIdAndVersionTag(
                                () -> appDataManager.getApplicationVersionConfigData(applicationDO.getId(),
                                        versionTag));

                appDataManager.writeConfigDataToZip(zos, configData, "");

                zos.finish();
                zipBytes = baos.toByteArray();
            }

            // 上传到对象存储
            String objectId = fileApi.createFile(zipBytes, fileName, "app/export", "application/zip");

            // 更新导出记录：状态为成功，保存 objectId
            updateExportStatusAndObjectId(exportId, AppExportStatusEnum.SUCCESS, objectId);
        } catch (Exception e) {
            log.error("异步导出应用配置失败，exportId: {}", exportId, e);
            // 导出失败，更新状态为失败
            updateExportStatus(exportId, AppExportStatusEnum.FAILED);
        }
    }

    /**
     * 更新导出状态
     *
     * @param exportId 导出记录ID
     * @param status   导出状态
     */
    private void updateExportStatus(Long exportId, AppExportStatusEnum status) {
        updateExportStatusAndObjectId(exportId, status, null);
    }

    /**
     * 更新导出状态和对象ID
     *
     * @param exportId 导出记录ID
     * @param status   导出状态
     * @param objectId 对象ID（S3中的资源ID）
     */
    private void updateExportStatusAndObjectId(Long exportId, AppExportStatusEnum status, String objectId) {
        try {
            AppExportDO exportDO = appExportRepository.getById(exportId);
            if (exportDO != null) {
                exportDO.setExportStatus(status.getValue());
                if (objectId != null) {
                    exportDO.setObjectId(objectId);
                }
                appExportRepository.updateById(exportDO);
            }
        } catch (Exception e) {
            log.error("更新导出状态失败，exportId: {}, status: {}, objectId: {}", exportId, status, objectId, e);
        }
    }

    /**
     * 查询导出状态
     *
     * @param exportId 导出记录ID
     * @return 导出状态值
     */
    @Override
    public Integer getExportStatus(Long exportId) {
        AppExportDO exportDO = appExportRepository.getById(exportId);
        if (exportDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_RECORD_NOT_EXIST);
        }
        Integer exportStatus = exportDO.getExportStatus();
        if (exportStatus == null) {
            exportStatus = AppExportStatusEnum.UNKNOWN.getValue();
        }
        return exportStatus;
    }

    /**
     * 分页查询导出记录
     *
     * @param pageReqVO 分页查询请求
     * @return 分页结果
     */
    @Override
    public PageResult<ExportPageRespVO> getExportPage(ExportPageReqVO pageReqVO) {
        PageResult<AppExportDO> pageResult = appExportRepository.selectPage(pageReqVO.getApplicationId(),
                pageReqVO.getExportStatus(),
                pageReqVO);

        if (pageResult.getList().isEmpty()) {
            return PageResult.empty();
        }

        // 获取用户信息
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());

        // 转换为响应VO
        List<ExportPageRespVO> respVOS = pageResult.getList().stream().map(exportDO -> {
            ExportPageRespVO respVO = BeanUtils.toBean(exportDO, ExportPageRespVO.class);
            respVO.setCreatorName(userHelper.getUserNickname(exportDO.getCreator()));
            respVO.setUpdaterName(userHelper.getUserNickname(exportDO.getUpdater()));
            return respVO;
        }).toList();

        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    /**
     * 删除导出记录
     *
     * @param exportId 导出记录ID
     */
    @Override
    public void deleteExportRecord(Long exportId) {
        Long applicationId = ApplicationManager.getApplicationId();
        if (applicationId == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }

        // 查询导出记录并验证是否属于当前应用
        AppExportDO exportDO = appExportRepository.getById(exportId);
        if (exportDO == null || !exportDO.getApplicationId().equals(applicationId)) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_RECORD_DELETE_ERROR);
        }

        // 删除导出记录
        appExportRepository.removeById(exportId);
    }

    /**
     * 重试导出应用
     *
     * @param exportId  导出记录ID
     * @param versionId 版本ID
     * @return 导出记录ID（返回原导出记录ID）
     */
    @Override
    public Long retryExportApplication(Long exportId, Long applicationId) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(applicationId);

        // 查询导出记录并验证是否属于当前应用
        AppExportDO exportDO = appExportRepository.getById(exportId);
        if (exportDO == null || !exportDO.getApplicationId().equals(applicationId)) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_ERROR);
        }

        // 验证版本是否存在且属于当前应用
        AppVersionDO versionDO = versionRepository.getById(exportDO.getVersionId());
        if (versionDO == null || !versionDO.getApplicationId().equals(applicationId)) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }

        // 验证导出状态，只有失败状态才能重试
        Integer exportStatus = exportDO.getExportStatus();
        if (exportStatus == null || !AppExportStatusEnum.FAILED.getValue().equals(exportStatus)) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_EXPORT_ERROR);
        }

        // 更新导出记录状态为导出中
        exportDO.setExportStatus(AppExportStatusEnum.EXPORTING.getValue());
        exportDO.setObjectId(null); // 清空之前的文件ID
        appExportRepository.updateById(exportDO);

        // 通过代理对象调用异步方法，确保 @Async 生效
        self.asyncExportApplication(exportId, applicationDO, versionDO);

        return exportId;
    }

    /**
     * 按版本类型解析用于查询配置的 versionTag：BUILD=0，RUNTIME=1，HISTORY=版本 ID
     */
    private long resolveVersionTag(AppVersionDO versionDO) {
        Integer vt = versionDO.getVersionType();
        if (vt != null && vt == VersionTypeEnum.BUILD.getValue()) {
            return VersionTagEnum.BUILD.getValue();
        }
        if (vt != null && vt == VersionTypeEnum.RUNTIME.getValue()) {
            return VersionTagEnum.RUNTIME.getValue();
        }
        return versionDO.getId();
    }

}
