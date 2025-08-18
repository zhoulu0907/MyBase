package com.cmsr.onebase.module.infra.framework.file.core.client.db;

import com.cmsr.onebase.module.infra.dal.database.FileContentDataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileContentDO;
import com.cmsr.onebase.module.infra.framework.file.core.client.AbstractFileClient;
import jakarta.annotation.Resource;

/**
 * 基于 DB 存储的文件客户端的配置类
 *
 */
public class DBFileClient extends AbstractFileClient<DBFileClientConfig> {

    @Resource
    private FileContentDataRepository fileContentDataRepository;

    public DBFileClient(Long id, DBFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {}

    @Override
    public String upload(byte[] content, String path, String type) {
        FileContentDO contentDO = new FileContentDO().setConfigId(getId())
                .setPath(path).setContent(content);
        fileContentDataRepository.insert(contentDO);
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
