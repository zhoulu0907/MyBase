package com.cmsr.onebase.plugin.runtime.service;

import com.cmsr.onebase.plugin.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 文件服务实现
 * <p>
 * 桥接平台的文件存储服务，提供给插件使用。
 * TODO: 实际使用时需要注入平台的FileService来实现真实的文件操作。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    // TODO: 注入平台的文件服务
    // @Resource
    // private com.cmsr.onebase.module.infra.service.FileService platformFileService;

    @Override
    public String upload(String fileName, byte[] content) {
        log.debug("FileService.upload: fileName={}, size={}", fileName, content.length);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public String upload(String fileName, InputStream inputStream) {
        log.debug("FileService.upload: fileName={}", fileName);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public String upload(String path, String fileName, byte[] content) {
        log.debug("FileService.upload: path={}, fileName={}, size={}", path, fileName, content.length);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public byte[] download(String fileId) {
        log.debug("FileService.download: fileId={}", fileId);
        // TODO: 调用平台服务实现
        return new byte[0];
    }

    @Override
    public InputStream getInputStream(String fileId) {
        log.debug("FileService.getInputStream: fileId={}", fileId);
        // TODO: 调用平台服务实现
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public String getUrl(String fileId) {
        log.debug("FileService.getUrl: fileId={}", fileId);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public String getPresignedUrl(String fileId, int expireSeconds) {
        log.debug("FileService.getPresignedUrl: fileId={}, expireSeconds={}", fileId, expireSeconds);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public void delete(String fileId) {
        log.debug("FileService.delete: fileId={}", fileId);
        // TODO: 调用平台服务实现
    }

    @Override
    public boolean exists(String fileId) {
        log.debug("FileService.exists: fileId={}", fileId);
        // TODO: 调用平台服务实现
        return false;
    }

    @Override
    public long getSize(String fileId) {
        log.debug("FileService.getSize: fileId={}", fileId);
        // TODO: 调用平台服务实现
        return 0;
    }

    @Override
    public String getContentType(String fileId) {
        log.debug("FileService.getContentType: fileId={}", fileId);
        // TODO: 调用平台服务实现
        return null;
    }
}
