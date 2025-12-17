package com.cmsr.onebase.module.infra.service.file;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.cmsr.onebase.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.cmsr.onebase.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.cmsr.onebase.framework.common.enums.SecurityCategoryCodeEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.module.infra.dal.database.FileDataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileDO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FileCreateReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePageReqVO;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePresignedUrlRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigCategoryGroupRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigGetReqVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.*;
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
    static boolean PATH_PREFIX_DATE_ENABLE = true;
    /**
     * 上传文件的后缀，是否包含时间戳
     * <p>
     * 目的：保证文件的唯一性，避免覆盖
     * 定制：可按需调整成 UUID、或者其他方式
     */
    static boolean PATH_SUFFIX_TIMESTAMP_ENABLE = true;

    /**
     * 清洗文件名，去除特殊字符并做基本保护处理
     */
    private static final Pattern INVALID_FILE_NAME_CHARS =
            Pattern.compile("[^a-zA-Z0-9\\u4e00-\\u9fa5_.-]");

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
    public String createFile(byte[] content, String name, String directory, String type, String visitMode) {
        // 1.1 处理 type 为空的情况
        if (StrUtil.isEmpty(type)) {
            type = FileTypeUtils.getMineType(content, name);
        }
        // 1.2 处理 name 为空的情况
        if (StrUtil.isEmpty(name)) {
            name = DigestUtil.md5Hex(content);
        }
        // 1.3 处理 visitMode 为空的情况
        if (StrUtil.isEmpty(visitMode)) {
            visitMode = FileVisitModeEnum.PUBLIC.getValue();
        }

        // 1.4 处理 文件后缀 为空的情况
        if (StrUtil.isEmpty(FileNameUtil.extName(name))) {
            // 如果 name 没有后缀 type，则补充后缀
            String extension = FileTypeUtils.getExtension(type);
            if (StrUtil.isNotEmpty(extension)) {
                name = name + extension;
            }
        }

        // 2. 执行文件校验
        validateFile(content, name, type);

        // 3. 检查是否有重复文件（基于MD5）
        String md5 = DigestUtil.md5Hex(content);
        FileDO existingFile = fileDataRepository.findByMd5AndVisitMode(md5, visitMode);
        if (existingFile != null) {
            // 存在相同MD5的文件，直接返回已存在的URL
            return existingFile.getId().toString();
        }

        // 4.1 生成上传的 path，需要保证唯一
        String path = generateUploadPath(name, directory);
        // 4.2 上传到文件存储器
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        String url = client.upload(content, path, type);

        // 5. 保存到数据库
        String runMode = null;
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null){
            runMode = loginUser.getRunMode();
        }
        FileDO fileDO = fileDataRepository.insert(new FileDO().setConfigId(client.getId())
                .setName(name).setPath(path).setUrl(url)
                .setType(type).setSize(content.length)
                .setMd5(md5)
                .setVisitMode(visitMode)
                .setRunMode(runMode));
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
        ArrayList<String> categoryCodes = new ArrayList<>();
        categoryCodes.add(SecurityCategoryCodeEnum.FILE_SECURITY.getValue());
        List<SecurityConfigCategoryGroupRespVO> tenantConfigItemsByCategoryCodes = securityConfigService.getTenantConfigItemsByCategoryCodes(new SecurityConfigGetReqVO().setTenantId(tenantId).setCategoryCode(categoryCodes));
        List<SecurityConfigItemRespVO> configs = new ArrayList<>();
        for (SecurityConfigCategoryGroupRespVO tenantConfigItemsByCategoryCode : tenantConfigItemsByCategoryCodes) {
            if (SecurityCategoryCodeEnum.FILE_SECURITY.getValue().equals(tenantConfigItemsByCategoryCode.getCategoryCode())){
                configs = securityConfigService.getTenantConfigItems(
                        tenantId, FileUploadCheckConstants.FILE_UPLOAD_CATEGORY);
            }
        }
        if (configs.isEmpty()){
            throw exception(FILE_CHECK_LIST_NOT_EXISTS);
        }

        // 从配置中获取参数
        Map<String, String> configMap = configs.stream()
                .collect(Collectors.toMap(SecurityConfigItemRespVO::getConfigKey,
                        SecurityConfigItemRespVO::getConfigValue));

        // 1. 校验文件大小
        long maxSize = getConfiguredMaxFileSize(configMap);
        if (content.length > maxSize) {
            throw exception(FILE_SIZE_OVERRUN,(maxSize / 1024 / 1024));
        }

        // 2. 校验文件名长度
        int maxNameLength = getConfiguredMaxFileNameLength(configMap);
        if (name != null && name.length() > maxNameLength) {
            throw exception(FILE_NAME_LENGTH_OVERRUN,maxNameLength);
        }

        // 3. 获取文件扩展名(这里不通过文件名获取真实扩展名，有些场景文件名并不准确，比如用户手动修改文件名后缀、图片裁切JPG转为PNG等)
        String extension = FileTypeUtils.getExtension(type);
        // if (StrUtil.isNotEmpty(name)) {
        //     extension = FileNameUtil.extName(name).toLowerCase();
        // } else {
        //     extension = FileTypeUtils.getExtension(type);
        // }

        if (StrUtil.isEmpty(extension)) {
            throw exception(FILE_EXTENSION_UNIDENTIFIABLE);
        }
        Map<String, FileTypeInfo> configuredFileCheckList = getConfiguredFileCheckList(configMap);
        // 4. 校验文件后缀
        Set<String> extensionSet = configuredFileCheckList.keySet();
        if (!extensionSet.contains(extension)) {
            throw exception(FILE_EXTENSION_NOT_ALLOW);
        }
        // 5. 校验MIME类型
        FileTypeInfo fileTypeInfo = configuredFileCheckList.get(extension);
        String mimeType = fileTypeInfo.getMimeType();
        if (StringUtils.isBlank(mimeType)
                || (!FileUploadCheckConstants.UNCHECK.equalsIgnoreCase(mimeType) && !mimeType.equals(type))) {
            throw exception(FILE_MIMETYPE_AND_EXTENSION_MISMATCHING);
        }
        // 6. 校验文件头魔数
        String magicNumber = fileTypeInfo.getMagicNumber();
        if (StringUtils.isBlank(magicNumber) || magicNumber.equalsIgnoreCase(FileUploadCheckConstants.DEFAULT)) {
            if (!FileMNValidateUtil.isValidDefaultMagicNumber(content, extension)) {
                throw exception(FILE_FORMAT_AND_EXTENSION_MISMATCHING);
            }
        } else if (!FileUploadCheckConstants.UNCHECK.equalsIgnoreCase(magicNumber)) {
            FileMNValidateUtil.isValidCustomMagicNumber(content, magicNumber, extension);
        }

        // 7. 特殊处理PDF XSS注入问题
        if (FileUploadCheckConstants.PDF.equals(extension)) {
            if (LightweightPdfXssDetector.hasPdfXssContent(content)) {
                throw exception(FILE_TYPE_PDF_CONTENT_NOT_STANDARD);
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
        if (StrUtil.isEmpty(configValue)){
            return FileUploadCheckConstants.FILE_DEFAULT_SIZE * 1024 * 1024;
        }
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
        if (StrUtil.isEmpty(configValue)){
            return FileUploadCheckConstants.FILE_NAME_DEFAULT_LENGTH;
        }
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
            throw exception(FILE_CHECK_LIST_NOT_EXISTS);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, FileTypeInfo>>() {
        }.getType();
        return gson.fromJson(configValue, type);
    }

    /**
     * 对上传的文件名进行清洗：
     * 1. Unicode 规范化（NFKC）
     * 2. 替换路径分隔符，防止路径穿越
     * 3. 合并连续的点号，移除非法字符
     * 4. 避免以点号开头
     *
     * @param name 原始文件名
     * @return 清洗后的文件名（不会为 null）
     */
    private String sanitizeFileName(String name) {
        if (name == null) {
            return "";
        }

        // 1. Unicode 规范化，避免某些特殊字符通过组合字符绕过校验
        name = Normalizer.normalize(name, Normalizer.Form.NFKC);

        // 2. 移除路径分隔符，替换为下划线，避免目录穿越
        name = name.replaceAll("[/\\\\]+", "_");

        // 3. 合并连续点（例如 ".." -> "."），然后移除非法字符
        name = name.replaceAll("\\.{2,}", ".");
        name = INVALID_FILE_NAME_CHARS.matcher(name).replaceAll("");

        // 4. 去除首尾空白
        name = name.trim();

        // 5. 避免以点号开头（隐藏/特殊文件），改为下划线前缀
        if (name.startsWith(".")) {
            name = "_" + (name.length() > 1 ? name.substring(1) : "");
        }

        // 若清洗后为空，返回默认名
        if (name.isEmpty()) {
            return FileUploadCheckConstants.DEFAULT_FILE_NAME;
        }
        return name;
    }


    /**
     * 生成上传的文件路径
     * 命名规则：日期/原文件名_时间戳.后缀
     *
     * @param name      文件名称
     * @param directory 目录
     * @return 上传路径
     */
    @VisibleForTesting
    String generateUploadPath(String name, String directory) {

        // 文件名称去除特殊字符
        name = sanitizeFileName(name);

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
    public void getFileContent(Long id, HttpServletRequest request, HttpServletResponse response, String visitMode) throws Exception {

        // 获取文件信息
        FileDO file = fileDataRepository.findByIdAndVisitMode(id, visitMode);
        if (file == null) {
            throw exception(FILE_NOT_EXISTS);
        }

        if (FileVisitModeEnum.AUTHEN.getValue().equals(file.getVisitMode())) {
            String token = SecurityFrameworkUtils.obtainAuthorization(request,
                    securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
            // 校验访问令牌
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.getAccessToken(token).getCheckedData();
            if (accessToken == null) {
                throw exception(FILE_DOWNLOAD_NOT_LOGIN);
            }

            if (StrUtil.isNotEmpty(file.getRunMode()) && !file.getRunMode().equals(accessToken.getRunMode())) {
                throw exception(FILE_NOT_DOWNLOAD);
            }
        }


        if (StrUtil.isEmpty(file.getPath())) {
            throw exception(FILE_PATH_NOT_EXISTS);
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