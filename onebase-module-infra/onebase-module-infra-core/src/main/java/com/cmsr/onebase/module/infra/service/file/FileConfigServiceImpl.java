package com.cmsr.onebase.module.infra.service.file;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.validation.ValidationUtils;
import com.cmsr.onebase.module.infra.convert.file.FileConfigConvert;
import com.cmsr.onebase.module.infra.dal.dataflex.FileConfigDataRepository;
import com.cmsr.onebase.module.infra.dal.dataflexdo.file.FileConfigDO;
import com.cmsr.onebase.module.infra.dal.vo.file.config.FileConfigPageReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.config.FileConfigRespVO;
import com.cmsr.onebase.module.infra.dal.vo.file.config.FileConfigSaveReqVO;
import com.cmsr.onebase.module.infra.framework.file.core.client.FileClient;
import com.cmsr.onebase.module.infra.framework.file.core.client.FileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.client.FileClientFactory;
import com.cmsr.onebase.module.infra.framework.file.core.enums.FileStorageEnum;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.cache.CacheUtils.buildAsyncReloadingCache;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;

/**
 * 文件配置 Service 实现类
 */
@Service
@Validated
@Slf4j
public class FileConfigServiceImpl implements FileConfigService {

    private static final Long CACHE_MASTER_ID = 0L;

    @Resource
    private FileClientFactory fileClientFactory;

    @Resource
    private FileConfigDataRepository fileConfigDataRepository;

    @Resource
    private Validator validator;

    /**
     * {@link FileClient} 缓存，通过它异步刷新 fileClientFactory
     */
    @Getter
    private final LoadingCache<Long, FileClient> clientCache = buildAsyncReloadingCache(Duration.ofSeconds(10L),
            new CacheLoader<>() {
                @Override
                public FileClient load(Long id) {
                    FileConfigDO config = Objects.equals(CACHE_MASTER_ID, id) ?
                            fileConfigDataRepository.findByMaster(true)
                            : fileConfigDataRepository.getById(id);
                    if (config != null) {
                        fileClientFactory.createOrUpdateFileClient(config.getId(), config.getStorage(),
                                parseClientConfig(config.getStorage(), config.getConfig()));
                    }
                    return fileClientFactory.getFileClient(null == config ? id : config.getId());
                }
            });

    @Override
    public Long createFileConfig(FileConfigSaveReqVO createReqVO) {
        FileConfigDO fileConfig = FileConfigConvert.INSTANCE.convert(createReqVO);
        fileConfig .setConfig(createReqVO.getConfig())
                // .setConfig(parseClientConfig(createReqVO.getStorage(), createReqVO.getConfig()))
                .setMaster(NumberUtils.INTEGER_ZERO); // 默认非 master
        fileConfigDataRepository.save(fileConfig);
        return fileConfig.getId();
    }

    @Override
    public void updateFileConfig(FileConfigSaveReqVO updateReqVO) {
        // 校验存在
        FileConfigDO config = validateFileConfigExists(updateReqVO.getId());
        // 更新
        FileConfigDO updateObj = FileConfigConvert.INSTANCE.convert(updateReqVO);
        // .setConfig(parseClientConfig(config.getStorage(), updateReqVO.getConfig()));
        fileConfigDataRepository.updateById(updateObj);

        // 清空缓存
        clearCache(config.getId(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFileConfigMaster(Long id) {
        // 校验存在
        validateFileConfigExists(id);
        // 更新其它为非 master
        fileConfigDataRepository.updateAllToNotMaster();
        // 更新
        fileConfigDataRepository.updateToMaster(id);

        // 清空缓存
        clearCache(null, true);
    }

    private FileClientConfig parseClientConfig(Integer storage, Map<String, Object> config) {
        // 获取配置类
        Class<? extends FileClientConfig> configClass = FileStorageEnum.getByStorage(storage).getConfigClass();
        FileClientConfig clientConfig = JsonUtils.parseObject2(JsonUtils.toJsonString(config), configClass);
        // 参数校验
        ValidationUtils.validate(validator, clientConfig);
        // 设置参数
        return clientConfig;
    }

    @Override
    public void deleteFileConfig(Long id) {
        // 校验存在
        FileConfigDO config = validateFileConfigExists(id);
        if (Boolean.TRUE.equals(config.getMaster())) {
            throw exception(FILE_CONFIG_DELETE_FAIL_MASTER);
        }
        // 删除
        fileConfigDataRepository.removeById(id);

        // 清空缓存
        clearCache(id, null);
    }

    /**
     * 清空指定文件配置
     *
     * @param id     配置编号
     * @param master 是否主配置
     */
    private void clearCache(Long id, Boolean master) {
        if (id != null) {
            clientCache.invalidate(id);
        }
        if (Boolean.TRUE.equals(master)) {
            clientCache.invalidate(CACHE_MASTER_ID);
        }
    }

    private FileConfigDO validateFileConfigExists(Long id) {
        FileConfigDO config = fileConfigDataRepository.getById(id);
        if (config == null) {
            throw exception(FILE_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @Override
    public FileConfigRespVO getFileConfig(Long id) {
        FileConfigDO fileConfigDO = fileConfigDataRepository.getById(id);
        FileConfigRespVO fileConfigRespVO = FileConfigConvert.INSTANCE.convertToFileConfigRespVO(fileConfigDO);
        FileClientConfig config = parseClientConfig(fileConfigDO.getStorage(), fileConfigDO.getConfig());
        fileConfigRespVO.setConfig(config);
        return fileConfigRespVO;
    }

    @Override
    public PageResult<FileConfigRespVO> getFileConfigPage(FileConfigPageReqVO pageReqVO) {
        PageResult<FileConfigDO> fileConfigResult = fileConfigDataRepository.findPage(pageReqVO);
        if (CollectionUtils.isEmpty(fileConfigResult.getList())) {
            return new PageResult<>();
        }
        // 把 PageResult<FileConfigDO> 转换为PageResult<FileConfigRespVO>
        // 由于 FileConfigDO 中的 config 字段是 Map<String, Object> 类型，所以需要转换为 FileClientConfig 类型
        List<FileConfigRespVO> fileConfigRespVOList = fileConfigResult.getList().stream()
                .map(fileConfigDO -> {
                    FileConfigRespVO fileConfigRespVO = FileConfigConvert.INSTANCE.convertToFileConfigRespVO(fileConfigDO);
                    FileClientConfig config = parseClientConfig(fileConfigDO.getStorage(), fileConfigDO.getConfig());
                    fileConfigRespVO.setConfig(config);
                    return fileConfigRespVO;
                })
                .collect(Collectors.toList());
        return new PageResult<>(fileConfigRespVOList, fileConfigResult.getTotal());
    }

    @Override
    public String testFileConfig(Long id) throws Exception {
        // 校验存在
        validateFileConfigExists(id);
        // 上传文件
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        return getFileClient(id).upload(content, IdUtil.fastSimpleUUID() + ".jpg", "image/jpeg");
    }

    @Override
    public FileClient getFileClient(Long id) {
        return clientCache.getUnchecked(id);
    }

    @Override
    public FileClient getMasterFileClient() {
        return clientCache.getUnchecked(CACHE_MASTER_ID);
    }

}