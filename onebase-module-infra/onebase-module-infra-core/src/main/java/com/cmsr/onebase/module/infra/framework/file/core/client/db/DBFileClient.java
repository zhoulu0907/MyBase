package com.cmsr.onebase.module.infra.framework.file.core.client.db;

import com.cmsr.onebase.module.infra.dal.database.FileContentDataRepositoryOld;
import com.cmsr.onebase.module.infra.dal.dataflex.FileContentDataRepository;
import com.cmsr.onebase.module.infra.dal.dataflexdo.file.FileContentDO;
import com.cmsr.onebase.module.infra.framework.file.core.client.AbstractFileClient;

/**
 * 基于 DB 存储的文件客户端的配置类
 *
 */
public class DBFileClient extends AbstractFileClient<DBFileClientConfig> {

    private final FileContentDataRepository fileContentDataRepository;

    public DBFileClient(Long id, DBFileClientConfig config, FileContentDataRepository fileContentDataRepository) {
        super(id, config);
        this.fileContentDataRepository = fileContentDataRepository;
    }

    @Override
    protected void doInit() {}

    @Override
    public String upload(byte[] content, String path, String type) {
        FileContentDO contentDO = new FileContentDO().setConfigId(getId())
                .setPath(path).setContent(content);
        fileContentDataRepository.save(contentDO);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        fileContentDataRepository.deleteByConfigIdAndPath(getId(), path);
    }

    @Override
    public byte[] getContent(String path) {
        return fileContentDataRepository.getContentByConfigIdAndPath(getId(), path);
    }

}
