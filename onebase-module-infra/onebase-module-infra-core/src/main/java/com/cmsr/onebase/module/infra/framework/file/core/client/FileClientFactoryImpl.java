package com.cmsr.onebase.module.infra.framework.file.core.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.cmsr.onebase.module.infra.dal.database.FileContentDataRepositoryOld;
import com.cmsr.onebase.module.infra.dal.dataflex.FileContentDataRepository;
import com.cmsr.onebase.module.infra.framework.file.core.client.db.DBFileClient;
import com.cmsr.onebase.module.infra.framework.file.core.client.db.DBFileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.enums.FileStorageEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件客户端的工厂实现类
 *
 */
@Slf4j
@Component
public class FileClientFactoryImpl implements FileClientFactory {

    @Resource
    private FileContentDataRepository fileContentDataRepository;

    /**
     * 文件客户端 Map
     * key：配置编号
     */
    private final ConcurrentMap<Long, AbstractFileClient<?>> clients = new ConcurrentHashMap<>();

    @Override
    public FileClient getFileClient(Long configId) {
        AbstractFileClient<?> client = clients.get(configId);
        if (client == null) {
            log.error("[getFileClient][配置编号({}) 找不到客户端]", configId);
        }
        return client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Config extends FileClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config) {
        AbstractFileClient<Config> client = (AbstractFileClient<Config>) clients.get(configId);
        if (client == null) {
            client = this.createFileClient(configId, storage, config);
            client.init();
            clients.put(client.getId(), client);
        } else {
            client.refresh(config);
        }
    }

    @SuppressWarnings("unchecked")
    private <Config extends FileClientConfig> AbstractFileClient<Config> createFileClient(
            Long configId, Integer storage, Config config) {
        FileStorageEnum storageEnum = FileStorageEnum.getByStorage(storage);
        Assert.notNull(storageEnum, String.format("文件配置(%s) 为空", storageEnum));

        // 特殊处理 DBFileClient，需要注入依赖
        if (storageEnum == FileStorageEnum.DB) {
            return (AbstractFileClient<Config>) new DBFileClient(configId, (DBFileClientConfig) config, fileContentDataRepository);
        }

        // 创建其他类型的客户端
        return (AbstractFileClient<Config>) ReflectUtil.newInstance(storageEnum.getClientClass(), configId, config);
    }

}
