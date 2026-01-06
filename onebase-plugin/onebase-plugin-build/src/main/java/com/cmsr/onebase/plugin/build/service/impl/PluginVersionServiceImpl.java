package com.cmsr.onebase.plugin.build.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.plugin.build.service.PluginVersionService;
import com.cmsr.onebase.plugin.build.validator.PluginMetaValidator;
import com.cmsr.onebase.plugin.build.validator.PluginZipValidator;
import com.cmsr.onebase.plugin.build.vo.req.PluginVersionUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginVersionUploadReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginVersionRespVO;
import com.cmsr.onebase.plugin.core.constant.PluginStatusConstants;
import com.cmsr.onebase.plugin.core.dal.database.PluginConfigInfoRepository;
import com.cmsr.onebase.plugin.core.dal.database.PluginInfoRepository;
import com.cmsr.onebase.plugin.core.dal.database.PluginPackageInfoRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginConfigInfoDO;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginInfoDO;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginPackageInfoDO;
import com.cmsr.onebase.plugin.core.model.PluginMetaInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.plugin.build.constant.PluginErrorCodeConstants.*;

/**
 * 插件版本服务实现类
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Service
@Slf4j
public class PluginVersionServiceImpl implements PluginVersionService {

    @Resource
    private PluginInfoRepository pluginInfoRepository;

    @Resource
    private PluginConfigInfoRepository pluginConfigInfoRepository;

    @Resource
    private PluginPackageInfoRepository pluginPackageInfoRepository;

    @Resource
    private PluginZipValidator pluginZipValidator;

    @Resource
    private PluginMetaValidator pluginMetaValidator;

    @Resource
    private FileApi fileApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadVersion(PluginVersionUploadReqVO uploadReqVO) {
        // 1. 校验插件是否存在
        List<PluginInfoDO> existingVersions = pluginInfoRepository.getListByPluginId(uploadReqVO.getPluginId());
        if (CollUtil.isEmpty(existingVersions)) {
            throw exception(PLUGIN_NOT_FOUND);
        }

        // 2. 校验版本号是否已存在
        boolean versionExists = existingVersions.stream()
                .anyMatch(v -> v.getPluginVersion().equals(uploadReqVO.getPluginVersion()));
        if (versionExists) {
            throw exception(PLUGIN_VERSION_ALREADY_EXISTS);
        }

        // 3. 校验并读取文件
        byte[] content = pluginZipValidator.validate(uploadReqVO.getFile());

        // 4. 如果是ZIP文件，提取plugin.json进行校验
        String originalFilename = uploadReqVO.getFile().getOriginalFilename();
        PluginMetaInfo metaInfo = null;
        String pluginJson = null;
        if (originalFilename != null && originalFilename.toLowerCase().endsWith(".zip")) {
            pluginJson = pluginZipValidator.extractPluginJson(content);
            metaInfo = pluginMetaValidator.validate(pluginJson);

            // 校验pluginId一致性
            if (!metaInfo.getPluginId().equals(uploadReqVO.getPluginId())) {
                throw exception(PLUGIN_META_PLUGIN_ID_REQUIRED);
            }
        }

        // 5. 上传文件到MinIO
        String fileId = fileApi.createFile(content, originalFilename);

        // 6. 继承插件基础信息
        PluginInfoDO latestVersion = existingVersions.stream()
                .max(Comparator.comparing(PluginInfoDO::getCreateTime))
                .orElse(existingVersions.get(0));

        // 7. 保存新版本信息
        PluginInfoDO pluginInfoDO = PluginInfoDO.builder()
                .pluginId(uploadReqVO.getPluginId())
                .pluginName(latestVersion.getPluginName())
                .pluginIcon(latestVersion.getPluginIcon())
                .pluginDescription(latestVersion.getPluginDescription())
                .pluginVersion(uploadReqVO.getPluginVersion())
                .pluginVersionDescription(uploadReqVO.getPluginVersionDescription())
                .pluginPackage(Long.parseLong(fileId))
                .pluginMetaInfo(pluginJson)
                .status(PluginStatusConstants.DISABLED)
                .build();
        pluginInfoRepository.insert(pluginInfoDO);

        // 8. 保存包信息
        if (metaInfo != null && CollUtil.isNotEmpty(metaInfo.getPackages())) {
            savePackageInfo(metaInfo, uploadReqVO.getPluginId(), uploadReqVO.getPluginVersion());
        }

        // 9. 复制或保存配置信息
        if (metaInfo != null && CollUtil.isNotEmpty(metaInfo.getConfigTemplates())) {
            saveConfigInfo(metaInfo, uploadReqVO.getPluginId(), uploadReqVO.getPluginVersion());
        } else {
            // 复制上一版本的配置
            copyConfigFromPreviousVersion(latestVersion.getPluginId(), latestVersion.getPluginVersion(),
                    uploadReqVO.getPluginVersion());
        }

        log.info("新版本上传成功: pluginId={}, version={}", uploadReqVO.getPluginId(), uploadReqVO.getPluginVersion());
        return pluginInfoDO.getId();
    }

    @Override
    public List<PluginVersionRespVO> getVersionList(Long pluginId) {
        List<PluginInfoDO> versions = pluginInfoRepository.getListByPluginId(pluginId);
        if (CollUtil.isEmpty(versions)) {
            throw exception(PLUGIN_NOT_FOUND);
        }

        return versions.stream()
                .sorted(Comparator.comparing(PluginInfoDO::getCreateTime).reversed())
                .map(v -> BeanUtils.toBean(v, PluginVersionRespVO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVersion(PluginVersionUpdateReqVO updateReqVO) {
        // 1. 获取版本信息
        PluginInfoDO pluginInfo = pluginInfoRepository.getPluginInfoById(updateReqVO.getId());
        if (pluginInfo == null) {
            throw exception(PLUGIN_VERSION_NOT_FOUND);
        }

        // 2. 检查是否为停用状态
        if (PluginStatusConstants.ENABLED.equals(pluginInfo.getStatus())) {
            throw exception(PLUGIN_ENABLED_CANNOT_UPDATE);
        }

        // 3. 更新版本描述
        if (updateReqVO.getPluginVersionDescription() != null) {
            pluginInfo.setPluginVersionDescription(updateReqVO.getPluginVersionDescription());
        }

        // 4. 如果有新文件上传，更新安装包
        if (updateReqVO.getFile() != null && !updateReqVO.getFile().isEmpty()) {
            // 校验文件
            byte[] content = pluginZipValidator.validate(updateReqVO.getFile());

            // 上传新文件
            String fileId = fileApi.createFile(content, updateReqVO.getFile().getOriginalFilename());
            pluginInfo.setPluginPackage(Long.parseLong(fileId));

            // 如果是ZIP，更新元数据
            String originalFilename = updateReqVO.getFile().getOriginalFilename();
            if (originalFilename != null && originalFilename.toLowerCase().endsWith(".zip")) {
                String pluginJson = pluginZipValidator.extractPluginJson(content);
                PluginMetaInfo metaInfo = pluginMetaValidator.validate(pluginJson);
                pluginInfo.setPluginMetaInfo(pluginJson);

                // 更新包信息
                pluginPackageInfoRepository.deleteByPluginIdAndVersion(
                        pluginInfo.getPluginId(), pluginInfo.getPluginVersion());
                if (CollUtil.isNotEmpty(metaInfo.getPackages())) {
                    savePackageInfo(metaInfo, pluginInfo.getPluginId(), pluginInfo.getPluginVersion());
                }
            }
        }

        // 5. 保存更新
        pluginInfoRepository.update(pluginInfo);

        log.info("版本更新成功: id={}, version={}", updateReqVO.getId(), pluginInfo.getPluginVersion());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersion(Long id) {
        // 1. 获取版本信息
        PluginInfoDO pluginInfo = pluginInfoRepository.getPluginInfoById(id);
        if (pluginInfo == null) {
            throw exception(PLUGIN_VERSION_NOT_FOUND);
        }

        // 2. 检查是否为启用状态
        if (PluginStatusConstants.ENABLED.equals(pluginInfo.getStatus())) {
            throw exception(PLUGIN_ENABLED_CANNOT_DELETE);
        }

        // 3. 检查是否为唯一版本
        List<PluginInfoDO> versions = pluginInfoRepository.getListByPluginId(pluginInfo.getPluginId());
        if (versions.size() <= 1) {
            throw exception(PLUGIN_ONLY_VERSION_CANNOT_DELETE);
        }

        // 4. 删除关联数据
        pluginConfigInfoRepository.deleteByPluginIdAndVersion(pluginInfo.getPluginId(), pluginInfo.getPluginVersion());
        pluginPackageInfoRepository.deleteByPluginIdAndVersion(pluginInfo.getPluginId(), pluginInfo.getPluginVersion());

        // 5. 删除版本记录
        pluginInfoRepository.deleteById(id);

        log.info("版本删除成功: id={}, pluginId={}, version={}",
                id, pluginInfo.getPluginId(), pluginInfo.getPluginVersion());
    }

    /**
     * 保存包信息
     */
    private void savePackageInfo(PluginMetaInfo metaInfo, Long pluginId, String pluginVersion) {
        for (PluginMetaInfo.PluginPackageInfo packageInfo : metaInfo.getPackages()) {
            PluginPackageInfoDO packageDO = PluginPackageInfoDO.builder()
                    .pluginId(pluginId)
                    .pluginVersion(pluginVersion)
                    .packageName(packageInfo.getPackageName())
                    .packageType(packageInfo.getPackageType())
                    .build();
            pluginPackageInfoRepository.insert(packageDO);
        }
    }

    /**
     * 保存配置信息
     */
    private void saveConfigInfo(PluginMetaInfo metaInfo, Long pluginId, String pluginVersion) {
        for (PluginMetaInfo.PluginConfigTemplate configTemplate : metaInfo.getConfigTemplates()) {
            PluginConfigInfoDO configDO = PluginConfigInfoDO.builder()
                    .pluginId(pluginId)
                    .pluginVersion(pluginVersion)
                    .configKey(configTemplate.getConfigKey())
                    .configValue(configTemplate.getDefaultValue())
                    .valueType(configTemplate.getValueType())
                    .build();
            pluginConfigInfoRepository.insert(configDO);
        }
    }

    /**
     * 从上一版本复制配置
     */
    private void copyConfigFromPreviousVersion(Long pluginId, String previousVersion, String newVersion) {
        List<PluginConfigInfoDO> previousConfigs = pluginConfigInfoRepository.getListByPluginIdAndVersion(
                pluginId, previousVersion);
        for (PluginConfigInfoDO config : previousConfigs) {
            PluginConfigInfoDO newConfig = PluginConfigInfoDO.builder()
                    .pluginId(pluginId)
                    .pluginVersion(newVersion)
                    .configKey(config.getConfigKey())
                    .configValue(config.getConfigValue())
                    .valueType(config.getValueType())
                    .build();
            pluginConfigInfoRepository.insert(newConfig);
        }
    }

}
