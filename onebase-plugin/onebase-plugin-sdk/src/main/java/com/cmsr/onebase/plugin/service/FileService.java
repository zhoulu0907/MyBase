package com.cmsr.onebase.plugin.service;

import java.io.InputStream;

/**
 * 文件操作服务
 * <p>
 * 提供文件上传、下载、删除等能力。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public interface FileService {

    // ==================== 上传 ====================

    /**
     * 上传文件
     *
     * @param fileName 文件名
     * @param content  文件内容
     * @return 文件ID或URL
     */
    String upload(String fileName, byte[] content);

    /**
     * 上传文件
     *
     * @param fileName    文件名
     * @param inputStream 输入流
     * @return 文件ID或URL
     */
    String upload(String fileName, InputStream inputStream);

    /**
     * 上传文件到指定路径
     *
     * @param path     存储路径
     * @param fileName 文件名
     * @param content  文件内容
     * @return 文件ID或URL
     */
    String upload(String path, String fileName, byte[] content);

    // ==================== 下载 ====================

    /**
     * 下载文件
     *
     * @param fileId 文件ID或URL
     * @return 文件内容
     */
    byte[] download(String fileId);

    /**
     * 获取文件输入流
     *
     * @param fileId 文件ID或URL
     * @return 输入流
     */
    InputStream getInputStream(String fileId);

    // ==================== 访问URL ====================

    /**
     * 获取文件访问URL
     *
     * @param fileId 文件ID
     * @return 访问URL
     */
    String getUrl(String fileId);

    /**
     * 获取带有效期的访问URL
     *
     * @param fileId        文件ID
     * @param expireSeconds 有效期（秒）
     * @return 访问URL
     */
    String getPresignedUrl(String fileId, int expireSeconds);

    // ==================== 删除 ====================

    /**
     * 删除文件
     *
     * @param fileId 文件ID或URL
     */
    void delete(String fileId);

    // ==================== 文件信息 ====================

    /**
     * 检查文件是否存在
     *
     * @param fileId 文件ID或URL
     * @return true表示存在
     */
    boolean exists(String fileId);

    /**
     * 获取文件大小
     *
     * @param fileId 文件ID或URL
     * @return 文件大小（字节）
     */
    long getSize(String fileId);

    /**
     * 获取文件MIME类型
     *
     * @param fileId 文件ID或URL
     * @return MIME类型
     */
    String getContentType(String fileId);
}
