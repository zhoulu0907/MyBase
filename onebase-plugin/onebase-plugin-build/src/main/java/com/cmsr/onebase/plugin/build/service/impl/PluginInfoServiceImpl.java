package com.cmsr.onebase.plugin.build.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.plugin.build.redis.PluginCommandPublisher;
import com.cmsr.onebase.plugin.build.service.PluginInfoService;
import com.cmsr.onebase.plugin.build.validator.PluginMetaValidator;
import com.cmsr.onebase.plugin.build.validator.PluginZipValidator;
import com.cmsr.onebase.plugin.build.validator.PluginZipValidator.PackageInfo;
import com.cmsr.onebase.plugin.build.vo.req.PluginInfoPageReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginInfoUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginUploadReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginVersionStatusReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginInfoDetailRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginInfoRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginVersionRespVO;
import com.cmsr.onebase.plugin.core.constant.PluginStatusConstants;
import com.cmsr.onebase.plugin.core.dal.database.PluginConfigInfoRepository;
import com.cmsr.onebase.plugin.core.dal.database.PluginInfoRepository;
import com.cmsr.onebase.plugin.core.dal.database.PluginPackageInfoRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginConfigInfoDO;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginInfoDO;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginPackageInfoDO;
import com.cmsr.onebase.plugin.core.model.PluginMetaInfo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
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
 * 插件信息服务实现类
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Service
@Slf4j
public class PluginInfoServiceImpl implements PluginInfoService {

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
    private PluginCommandPublisher pluginCommandPublisher;

    @Resource
    private FileApi fileApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPlugin(PluginUploadReqVO uploadReqVO) {
        // 1. 校验并读取插件包文件
        byte[] content = pluginZipValidator.validate(uploadReqVO.getFile());

        // 2. 提取并解析plugin.json
        String pluginJson = pluginZipValidator.extractPluginJson(content);
        PluginMetaInfo metaInfo = pluginMetaValidator.validate(pluginJson);

        // 3. 提取plugin.schema.json（插件配置模板）
        String pluginSchemaJson = pluginZipValidator.extractPluginSchemaJson(content);

        // 4. 检查插件是否已存在
        List<PluginInfoDO> existingPlugins = pluginInfoRepository.getListByPluginId(metaInfo.getPluginId());
        if (CollUtil.isNotEmpty(existingPlugins)) {
            throw exception(PLUGIN_ALREADY_EXISTS);
        }

        // 5. 上传插件包到MinIO
        String fileId = fileApi.createFile(content, uploadReqVO.getFile().getOriginalFilename());

        // 6. 获取插件图标（直接存储字符串）
        String pluginIcon = uploadReqVO.getPluginIcon();

        // 7. 保存插件信息（优先使用用户输入的名称和描述，否则使用plugin.json中的）
        PluginInfoDO pluginInfoDO = PluginInfoDO.builder()
                .pluginId(metaInfo.getPluginId())
                .pluginName(StrUtil.isNotBlank(uploadReqVO.getPluginName()) ? uploadReqVO.getPluginName() : metaInfo.getPluginName())
                .pluginDescription(StrUtil.isNotBlank(uploadReqVO.getPluginDescription()) ? uploadReqVO.getPluginDescription() : metaInfo.getDescription())
                .pluginVersion(StrUtil.isNotBlank(uploadReqVO.getPluginVersion()) ? uploadReqVO.getPluginVersion() : metaInfo.getPluginVersion())
                .pluginVersionDescription(StrUtil.isNotBlank(uploadReqVO.getPluginVersionDescription()) ? uploadReqVO.getPluginVersionDescription() : metaInfo.getVersionDescription())
                .pluginPackage(Long.parseLong(fileId))
                .pluginIcon(pluginIcon)
                .pluginMetaInfo(pluginJson)
                .pluginConfigInfo(pluginSchemaJson)  // 插件配置模板，来自zip包中的plugin.schema.json
                .status(PluginStatusConstants.DISABLED)
                .build();
        pluginInfoRepository.insert(pluginInfoDO);

        // 8. 检测并保存包信息（自动检测前端/后端包）
        List<PackageInfo> packages = pluginZipValidator.detectPackages(content);
        savePackageInfo(packages, pluginInfoDO.getPluginId(), pluginInfoDO.getPluginVersion());

        // 9. 保存配置信息
        saveConfigInfo(metaInfo, pluginInfoDO.getPluginId(), pluginInfoDO.getPluginVersion());

        // 10. 发布插件上传消息通知runtime模块
        pluginCommandPublisher.publishUploadCommand(
                pluginInfoDO.getPluginId(),
                pluginInfoDO.getPluginVersion(),
                TenantContextHolder.getTenantId(),
                pluginInfoDO.getPluginPackage()
        );

        log.info("插件上传成功: pluginId={}, version={}", metaInfo.getPluginId(), pluginInfoDO.getPluginVersion());
        return pluginInfoDO.getPluginId();
    }

    @Override
    public PluginInfoDetailRespVO getPluginDetail(Long id) {
        // 1. 获取当前版本信息
        PluginInfoDO pluginInfo = pluginInfoRepository.getPluginInfoById(id);
        if (pluginInfo == null) {
            throw exception(PLUGIN_NOT_FOUND);
        }

        // 2. 获取所有版本列表
        List<PluginInfoDO> versions = pluginInfoRepository.getListByPluginId(pluginInfo.getPluginId());

        // 3. 构建响应
        PluginInfoDetailRespVO respVO = new PluginInfoDetailRespVO();
        respVO.setPluginId(pluginInfo.getPluginId());
        respVO.setPluginName(pluginInfo.getPluginName());
        respVO.setPluginIcon(pluginInfo.getPluginIcon());
        respVO.setPluginDescription(pluginInfo.getPluginDescription());
        respVO.setPluginMetaInfo(pluginInfo.getPluginMetaInfo());

        // 设置版本列表，按创建时间倒序
        List<PluginVersionRespVO> versionList = versions.stream()
                .sorted(Comparator.comparing(PluginInfoDO::getCreateTime).reversed())
                .map(v -> {
                    PluginVersionRespVO versionVO = new PluginVersionRespVO();
                    versionVO.setId(v.getId());
                    versionVO.setPluginVersion(v.getPluginVersion());
                    versionVO.setPluginVersionDescription(v.getPluginVersionDescription());
                    versionVO.setPluginPackage(v.getPluginPackage());
                    versionVO.setStatus(v.getStatus());
                    versionVO.setCreateTime(v.getCreateTime());
                    versionVO.setUpdateTime(v.getUpdateTime());
                    return versionVO;
                })
                .collect(Collectors.toList());
        respVO.setVersions(versionList);

        // 设置首次创建时间（取最早版本的创建时间）
        versions.stream()
                .min(Comparator.comparing(PluginInfoDO::getCreateTime))
                .ifPresent(oldest -> respVO.setCreateTime(oldest.getCreateTime()));

        return respVO;
    }

    @Override
    public PageResult<PluginInfoRespVO> getPluginPage(PluginInfoPageReqVO pageReqVO) {
        // 1. 构建查询条件 - 按pluginId分组，取最新版本或启用版本
        QueryWrapper queryWrapper = pluginInfoRepository.query();

        if (StrUtil.isNotBlank(pageReqVO.getPluginName())) {
            queryWrapper.like(PluginInfoDO::getPluginName, pageReqVO.getPluginName());
        }
        if (pageReqVO.getStatus() != null) {
            queryWrapper.eq(PluginInfoDO::getStatus, pageReqVO.getStatus());
        }

        queryWrapper.orderBy(PluginInfoDO::getCreateTime, false);

        // 2. 执行分页查询
        Page<PluginInfoDO> page = pluginInfoRepository.page(
                new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()),
                queryWrapper
        );

        // 3. 按pluginId聚合，返回当前启用版本或最新版本
        List<PluginInfoRespVO> respList = page.getRecords().stream()
                .collect(Collectors.groupingBy(PluginInfoDO::getPluginId))
                .values().stream()
                .map(versions -> {
                    // 优先取启用版本，否则取最新版本
                    PluginInfoDO current = versions.stream()
                            .filter(v -> PluginStatusConstants.ENABLED.equals(v.getStatus()))
                            .findFirst()
                            .orElse(versions.stream()
                                    .max(Comparator.comparing(PluginInfoDO::getCreateTime))
                                    .orElse(versions.get(0)));

                    PluginInfoRespVO respVO = BeanUtils.toBean(current, PluginInfoRespVO.class);
                    respVO.setVersionCount(versions.size());
                    return respVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(respList, page.getTotalRow());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePluginInfo(PluginInfoUpdateReqVO updateReqVO) {
        // 1. 校验插件存在
        List<PluginInfoDO> versions = pluginInfoRepository.getListByPluginId(updateReqVO.getPluginId());
        if (CollUtil.isEmpty(versions)) {
            throw exception(PLUGIN_NOT_FOUND);
        }

        // 2. 获取插件图标
        String pluginIcon = updateReqVO.getPluginIcon();

        // 3. 更新所有版本的基础信息
        for (PluginInfoDO version : versions) {
            if (StrUtil.isNotBlank(updateReqVO.getPluginName())) {
                version.setPluginName(updateReqVO.getPluginName());
            }
            if (updateReqVO.getPluginDescription() != null) {
                version.setPluginDescription(updateReqVO.getPluginDescription());
            }
            if (StrUtil.isNotBlank(pluginIcon)) {
                version.setPluginIcon(pluginIcon);
            }
            pluginInfoRepository.update(version);
        }

        log.info("插件基础信息更新成功: pluginId={}", updateReqVO.getPluginId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlugin(String pluginId) {
        // 1. 获取所有版本
        List<PluginInfoDO> versions = pluginInfoRepository.getListByPluginId(pluginId);
        if (CollUtil.isEmpty(versions)) {
            throw exception(PLUGIN_NOT_FOUND);
        }

        // 2. 检查是否有启用版本
        boolean hasEnabled = versions.stream()
                .anyMatch(v -> PluginStatusConstants.ENABLED.equals(v.getStatus()));
        if (hasEnabled) {
            throw exception(PLUGIN_ENABLED_CANNOT_DELETE);
        }

        // 3. 获取租户ID
        Long tenantId = TenantContextHolder.getTenantId();

        // 4. 删除所有版本及关联数据，并通知Runtime删除插件
        for (PluginInfoDO version : versions) {
            // 发布Redis消息通知Runtime删除插件
            pluginCommandPublisher.publishDeleteCommand(
                    pluginId,
                    version.getPluginVersion(),
                    tenantId
            );

            // 删除配置信息
            pluginConfigInfoRepository.deleteByPluginIdAndVersion(pluginId, version.getPluginVersion());
            // 删除包信息
            pluginPackageInfoRepository.deleteByPluginIdAndVersion(pluginId, version.getPluginVersion());
            // 删除插件信息
            pluginInfoRepository.deleteById(version.getId());
        }

        log.info("插件删除成功: pluginId={}", pluginId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enablePlugin(PluginVersionStatusReqVO statusReqVO) {
        // 1. 根据pluginId和version获取版本信息
        PluginInfoDO pluginInfo = pluginInfoRepository.getByPluginIdAndVersion(
                statusReqVO.getPluginId(), statusReqVO.getPluginVersion());
        if (pluginInfo == null) {
            throw exception(PLUGIN_VERSION_NOT_FOUND);
        }

        // 2. 检查是否已启用
        if (PluginStatusConstants.ENABLED.equals(pluginInfo.getStatus())) {
            throw exception(PLUGIN_ALREADY_ENABLED);
        }

        // 3. 获取租户ID
        Long tenantId = TenantContextHolder.getTenantId();

        // 4. 查找并自动停用其他已启用版本
        List<PluginInfoDO> versions = pluginInfoRepository.getListByPluginId(pluginInfo.getPluginId());
        List<PluginInfoDO> enabledVersions = versions.stream()
                .filter(v -> PluginStatusConstants.ENABLED.equals(v.getStatus()))
                .filter(v -> !v.getId().equals(pluginInfo.getId()))
                .toList();

        for (PluginInfoDO enabledVersion : enabledVersions) {
            log.info("自动停用其他版本: pluginId={}, version={}", enabledVersion.getPluginId(), enabledVersion.getPluginVersion());
            // 更新数据库状态为禁用
            enabledVersion.setStatus(PluginStatusConstants.DISABLED);
            pluginInfoRepository.update(enabledVersion);
            // 发布禁用命令通知Runtime卸载并清理旧版本
            pluginCommandPublisher.publishDisableAndCleanCommand(
                    enabledVersion.getPluginId(),
                    enabledVersion.getPluginVersion(),
                    tenantId
            );
        }

        // 5. 更新状态为启用
        pluginInfo.setStatus(PluginStatusConstants.ENABLED);
        pluginInfoRepository.update(pluginInfo);

        // 6. 发布Redis消息通知Runtime启用新版本
        pluginCommandPublisher.publishEnableCommand(
                pluginInfo.getPluginId(),
                pluginInfo.getPluginVersion(),
                tenantId,
                pluginInfo.getPluginPackage()
        );

        log.info("插件启用成功: pluginId={}, version={}", pluginInfo.getPluginId(), pluginInfo.getPluginVersion());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disablePlugin(PluginVersionStatusReqVO statusReqVO) {
        // 1. 根据pluginId和version获取版本信息
        PluginInfoDO pluginInfo = pluginInfoRepository.getByPluginIdAndVersion(
                statusReqVO.getPluginId(), statusReqVO.getPluginVersion());
        if (pluginInfo == null) {
            throw exception(PLUGIN_VERSION_NOT_FOUND);
        }

        // 2. 检查是否已停用
        if (PluginStatusConstants.DISABLED.equals(pluginInfo.getStatus())) {
            throw exception(PLUGIN_ALREADY_DISABLED);
        }

        // 3. 更新状态为停用
        pluginInfo.setStatus(PluginStatusConstants.DISABLED);
        pluginInfoRepository.update(pluginInfo);

        // 4. 发布Redis消息通知Runtime
        Long tenantId = TenantContextHolder.getTenantId();
        pluginCommandPublisher.publishDisableCommand(
                pluginInfo.getPluginId(),
                pluginInfo.getPluginVersion(),
                tenantId
        );

        log.info("插件禁用成功: pluginId={}, version={}", pluginInfo.getPluginId(), pluginInfo.getPluginVersion());
    }

    /**
     * 保存包信息（使用自动检测的包信息）
     */
    private void savePackageInfo(List<PackageInfo> packages, String pluginId, String pluginVersion) {
        if (CollUtil.isEmpty(packages)) {
            return;
        }
        for (PackageInfo packageInfo : packages) {
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
    private void saveConfigInfo(PluginMetaInfo metaInfo, String pluginId, String pluginVersion) {
        if (CollUtil.isEmpty(metaInfo.getConfigTemplates())) {
            return;
        }
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

}
