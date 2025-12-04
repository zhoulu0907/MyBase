package com.cmsr.onebase.module.infra.service.file;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import com.cmsr.onebase.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.cmsr.onebase.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.cmsr.onebase.framework.common.enums.RunModeEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.module.infra.dal.database.FileDataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileDO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FileCreateReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePageReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePresignedUrlRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.enums.file.FileEnvFlagEnum;
import com.cmsr.onebase.module.infra.enums.file.FileUploadCheckConstants;
import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;
import com.cmsr.onebase.module.infra.framework.file.core.client.FileClient;
import com.cmsr.onebase.module.infra.framework.file.core.client.s3.FilePresignedUrlRespDTO;
import com.cmsr.onebase.module.infra.framework.file.core.utils.FileMNValidateUtil;
import com.cmsr.onebase.module.infra.framework.file.core.utils.FileTypeUtils;
import com.cmsr.onebase.module.infra.framework.file.core.utils.LightweightPdfXssDetector;
import com.cmsr.onebase.module.infra.service.file.dto.FileTypeInfo;
import com.cmsr.onebase.module.infra.service.security.SecurityConfigService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.FILE_NOT_EXISTS;
import static com.cmsr.onebase.module.infra.framework.file.core.utils.FileTypeUtils.writeAttachment;

/**
 * 文件 Service 实现类
 *
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    /**
     * 上传文件的前缀，是否包含日期（yyyyMMdd）
     * <p>
     * 目的：按照日期，进行分目录
     */
    static boolean PATH_PREFIX_DATE_ENABLE      = true;
    /**
     * 上传文件的后缀，是否包含时间戳
     * <p>
     * 目的：保证文件的唯一性，避免覆盖
     * 定制：可按需调整成 UUID、或者其他方式
     */
    static boolean PATH_SUFFIX_TIMESTAMP_ENABLE = true;

    @Resource
    private FileConfigService fileConfigService;

    @Resource
    private FileDataRepository fileDataRepository;

    @Resource
    private SecurityConfigService securityConfigService;

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private OAuth2TokenCommonApi oauth2TokenApi;

    @Override
    public PageResult<FileDO> getFilePage(FilePageReqVO pageReqVO) {
        return fileDataRepository.findPage(pageReqVO);
    }

    @Override
    @SneakyThrows
    public String createFile(byte[] content, String name, String directory, String type, String envFlag) {
        // 1.1 处理 type 为空的情况
        if (StrUtil.isEmpty(type)) {
            type = FileTypeUtils.getMineType(content, name);
        }
        // 1.2 处理 name 为空的情况
        if (StrUtil.isEmpty(name)) {
            name = DigestUtil.md5Hex(content);
        }

        if (StrUtil.isEmpty(FileNameUtil.extName(name))) {
            // 如果 name 没有后缀 type，则补充后缀
            String extension = FileTypeUtils.getExtension(type);
            if (StrUtil.isNotEmpty(extension)) {
                name = name + extension;
            }
        }

        // 执行文件校验
        validateFile(content, name, type);

        // 检查是否有重复文件（基于MD5）
        String md5 = DigestUtil.md5Hex(content);
        FileDO existingFile = fileDataRepository.findByMd5(md5);
        if (existingFile != null) {
            // 存在相同MD5的文件，直接返回已存在的URL
            return existingFile.getId().toString();
        }

        // 2.1 生成上传的 path，需要保证唯一
        String path = generateUploadPath(name, directory);
        // 2.2 上传到文件存储器
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        String url = client.upload(content, path, type);

        // 3. 保存到数据库
        if (StrUtil.isEmpty(envFlag)) {
            envFlag = FileEnvFlagEnum.PUBLIC.getEnvFlag();
        }
        FileDO fileDO = fileDataRepository.insert(new FileDO().setConfigId(client.getId())
                .setName(name).setPath(path).setUrl(url)
                .setType(type).setSize(content.length)
                .setMd5(md5)
                .setEnvFlag(envFlag));
        return fileDO.getId().toString();
    }

    /**
     * 校验上传的文件
     *
     * @param content 文件内容
     * @param name    文件名
     * @param type    MIME类型
     */
    private void validateFile(byte[] content, String name, String type) {
        // 获取租户配置项
        Long tenantId = TenantContextHolder.getTenantId();
        List<SecurityConfigItemRespVO> configs = securityConfigService.getTenantConfigItems(
                tenantId, FileUploadCheckConstants.FILE_UPLOAD_CATEGORY); // 假设文件上传配置在默认分类中

        // 从配置中获取参数
        Map<String, String> configMap = configs.stream()
                .collect(Collectors.toMap(SecurityConfigItemRespVO::getConfigKey,
                        SecurityConfigItemRespVO::getConfigValue));

        // 1. 校验文件大小
        long maxSize = getConfiguredMaxFileSize(configMap);
        if (content.length > maxSize) {
            // TODO 改为：throw exception(FILE_NOT_EXISTS);
            throw new IllegalArgumentException("文件大小超过限制，最大允许" + (maxSize / 1024 / 1024) + "MB");
        }

        // 2. 校验文件名长度
        int maxNameLength = getConfiguredMaxFileNameLength(configMap);
        if (name != null && name.length() > maxNameLength) {
            throw new IllegalArgumentException("文件名长度超过限制，最大允许" + maxNameLength + "字符");
        }

        // 3. 获取文件扩展名
        String extension;
        if (StrUtil.isNotEmpty(name)) {
            extension = FileNameUtil.extName(name).toLowerCase();
        } else {
            extension = FileTypeUtils.getExtension(type);
        }
        if (StrUtil.isEmpty(extension)) {
            throw new IllegalArgumentException("无法识别文件扩展名");
        }
        Map<String, FileTypeInfo> configuredFileCheckList = getConfiguredFileCheckList(configMap);
        // 4. 校验文件后缀
        Set<String> extensionSet = configuredFileCheckList.keySet();
        if (!extensionSet.contains(extension)) {
            throw new IllegalArgumentException("不允许上传该类型的文件");
        }
        // 5. 校验MIME类型
        FileTypeInfo fileTypeInfo = configuredFileCheckList.get(extension);
        String mimeType = fileTypeInfo.getMimeType();
        if (StringUtils.isBlank(mimeType)
                || (!FileUploadCheckConstants.UNCHECK.equalsIgnoreCase(mimeType) && !mimeType.equals(type))) {
            throw new IllegalArgumentException("文件MIME类型与扩展名不匹配");
        }
        // 6. 校验文件头魔数
        String magicNumber = fileTypeInfo.getMagicNumber();
        if (StringUtils.isBlank(magicNumber) || magicNumber.equalsIgnoreCase(FileUploadCheckConstants.DEFAULT)) {
            if (!FileMNValidateUtil.isValidDefaultMagicNumber(content, extension)) {
                throw new IllegalArgumentException("文件实际格式与扩展名不匹配");
            }
        } else if (!FileUploadCheckConstants.UNCHECK.equalsIgnoreCase(magicNumber)) {
            FileMNValidateUtil.isValidCustomMagicNumber(content, magicNumber, extension);
        }

        // 7. 特殊处理PDF XSS注入问题
        if (FileUploadCheckConstants.PDF.equals(extension)) {
            if (LightweightPdfXssDetector.hasPdfXssContent(content)) {
                throw new IllegalArgumentException("PDF文件包含非法内容");
            }
        }
    }

    /**
     * 获取配置的文件最大大小
     *
     * @param configMap 配置映射
     * @return 最大大小（字节）
     */
    private long getConfiguredMaxFileSize(Map<String, String> configMap) {
        String configValue = configMap.get(SecurityConfigKey.uploadFileLengthLimit.getConfigKey());
        // 配置值单位为MB
        int mb = Integer.parseInt(configValue);
        return (long) mb * 1024 * 1024;
    }

    /**
     * 获取配置的文件名最大长度
     *
     * @param configMap 配置映射
     * @return 最大长度
     */
    private int getConfiguredMaxFileNameLength(Map<String, String> configMap) {
        String configValue = configMap.get(SecurityConfigKey.uploadFileNameLengthLimit.getConfigKey());
        return Integer.parseInt(configValue);
    }

    /**
     * 获取配置的文件后缀/mimeType/magicNumber 检查项
     *
     * @param configMap 配置映射
     * @return 最大长度
     */
    private Map<String, FileTypeInfo> getConfiguredFileCheckList(Map<String, String> configMap) {
        String configValue = configMap.get(SecurityConfigKey.uploadFileCheckList.getConfigKey());
        if (StrUtil.isEmpty(configValue)) {
            throw new IllegalArgumentException("文件上传检查项配置不能为空");
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, FileTypeInfo>>() {
        }.getType();
        return gson.fromJson(configValue, type);
    }


    /**
     * 生成上传的文件路径
     * 命名规则：日期/原文件名_时间戳.后缀
     *
     * @param name      文件名称
     * @param directory 目录
     * @return 上传路径
     */
    // todo 文件名称去除特殊字符
    @VisibleForTesting
    String generateUploadPath(String name, String directory) {
        // 1. 生成前缀、后缀
        String prefix = null;
        if (PATH_PREFIX_DATE_ENABLE) {
            prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATE_PATTERN);
        }
        String suffix = null;
        if (PATH_SUFFIX_TIMESTAMP_ENABLE) {
            suffix = String.valueOf(System.currentTimeMillis());
        }

        // 2.1 先拼接 suffix 后缀
        if (StrUtil.isNotEmpty(suffix)) {
            String ext = FileNameUtil.extName(name);
            if (StrUtil.isNotEmpty(ext)) {
                name = FileNameUtil.mainName(name) + StrUtil.C_UNDERLINE + suffix + StrUtil.DOT + ext;
            } else {
                name = name + StrUtil.C_UNDERLINE + suffix;
            }
        }
        // 2.2 再拼接 prefix 前缀
        if (StrUtil.isNotEmpty(prefix)) {
            name = prefix + StrUtil.SLASH + name;
        }
        // 2.3 最后拼接 directory 目录
        if (StrUtil.isNotEmpty(directory)) {
            name = directory + StrUtil.SLASH + name;
        }
        return name;
    }

    @Override
    @SneakyThrows
    public FilePresignedUrlRespVO getFilePresignedUrl(String name, String directory) {
        // 1. 生成上传的 path，需要保证唯一
        String path = generateUploadPath(name, directory);

        // 2. 获取文件预签名地址
        FileClient fileClient = fileConfigService.getMasterFileClient();
        FilePresignedUrlRespDTO presignedObjectUrl = fileClient.getPresignedObjectUrl(path);
        return BeanUtils.toBean(presignedObjectUrl, FilePresignedUrlRespVO.class,
                object -> object.setConfigId(fileClient.getId()).setPath(path));
    }

    @Override
    public Long createFile(FileCreateReqVO createReqVO) {
        FileDO file = BeanUtils.toBean(createReqVO, FileDO.class);
        fileDataRepository.insert(file);
        return file.getId();
    }

    @Override
    public void deleteFile(Long id) throws Exception {
        // 校验存在
        FileDO file = validateFileExists(id);

        // 从文件存储器中删除
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        client.delete(file.getPath());

        // 删除记录
        fileDataRepository.deleteById(id);
    }

    private FileDO validateFileExists(Long id) {
        FileDO fileDO = fileDataRepository.findById(id);
        if (fileDO == null) {
            throw exception(FILE_NOT_EXISTS);
        }
        return fileDO;
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        return client.getContent(path);
    }


    @Override
    public List<FileDO> getFileListByIds(Collection<Long> ids) {
        return fileDataRepository.findAllByIds(ids);
    }

    @Override
    public void getFileContent(Long id, String envFlag, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (StrUtil.isEmpty(envFlag)) {
            envFlag = FileEnvFlagEnum.PUBLIC.getEnvFlag();
        }

        if (!envFlag.equals(FileEnvFlagEnum.PUBLIC.getEnvFlag())) {
            String token = SecurityFrameworkUtils.obtainAuthorization(request,
                    securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
            // 校验访问令牌
            // TODO 直接通过tokne获取tokenDO数据，将其runMode和文件的runMode进行对比
            // 提示词：未登录；无权限获取文件：环境标识不匹配
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(RunModeEnum.RUNTIME.getValue(), token).getCheckedData();
            if (accessToken == null) {
                throw new IllegalArgumentException("无效的访问令牌，无法下载该文件");
            }
        }
        // 获取文件信息
        FileDO file = fileDataRepository.findById(id);
        if (file == null) {
            throw exception(FILE_NOT_EXISTS);
        }

        if (StrUtil.isNotEmpty(file.getEnvFlag()) && !file.getEnvFlag().equals(envFlag)) {
            throw new IllegalArgumentException("文件环境标识不匹配，无法下载该文件");
        }

        if (StrUtil.isEmpty(file.getPath())) {
            throw new IllegalArgumentException("文件路径为空，该文件无法下载");
        }
        // 解码，解决中文路径的问题 https://gitee.com/zhijiantianya/onebase_v3/pulls/807/
        String path = URLUtil.decode(file.getPath());

        // 获取文件内容
        byte[] content = getFileContent(file.getConfigId(), path);
        if (content == null) {
            log.warn("[getFileContent][configId({}) path({}) 文件不存在]", file.getConfigId(), path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        writeAttachment(response, path, content);
    }


}