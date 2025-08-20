package com.cmsr.onebase.module.infra.framework.file.core.client.local;

import cn.hutool.core.io.FileUtil;
import com.cmsr.onebase.module.infra.framework.file.core.client.AbstractFileClient;

import java.io.File;

/**
 * 本地文件客户端
 *
 */
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        File file = new File(filePath);
        // 确保目录存在
        FileUtil.mkParentDirs(file);
        // 写入文件
        FileUtil.writeBytes(content, file);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        FileUtil.del(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        return FileUtil.readBytes(filePath);
    }

    private String getFilePath(String path) {
        // 获取项目根目录
        String porjectBaseDir = System.getProperty("user.dir");
        return porjectBaseDir + File.separator + config.getBasePath() + File.separator + path;
        // return config.getBasePath() + File.separator + path;
    }

}
