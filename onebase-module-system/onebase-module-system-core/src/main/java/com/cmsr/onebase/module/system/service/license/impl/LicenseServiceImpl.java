package com.cmsr.onebase.module.system.service.license.impl;

import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.convert.license.LicenseConvert;
import com.cmsr.onebase.module.system.dal.database.LicenseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.enums.license.LicenseStatusEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.util.encrypt.SM4Utils;
import com.cmsr.onebase.module.system.vo.license.LicenseExportRespVO;
import com.cmsr.onebase.module.system.vo.license.LicensePageReqVO;
import com.cmsr.onebase.module.system.vo.license.LicenseSaveReqVO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.license.core.handler.LicenseCheckHandler.LICENSE_KEY;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;
import static com.cmsr.onebase.module.system.util.encrypt.SM4Utils.sm4Encrypt;

/**
 * License 服务实现类
 * <p>
 * 提供License的增删改查等核心服务能力。
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Service
@Validated
@Slf4j
public class LicenseServiceImpl implements LicenseService {

    private static final String LICENSE_SECRET_KEY = "1234567812345678";

    @Resource
    private LicenseDataRepository licenseDataRepository;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建License
     *
     * @param reqVO License创建请求参数
     * @return License主键ID
     */
    @Override
    public Long createLicense(LicenseSaveReqVO reqVO) {
        LicenseDO license = BeanUtils.toBean(reqVO, LicenseDO.class);
        licenseDataRepository.insert(license);
        return license.getId();
    }
    /**
     * 创建License文件
     *
     * @param reqVO License创建请求参数
     * @param response HttpServletResponse
     */
    @Override
    public void createLicenseFile(LicenseSaveReqVO reqVO, HttpServletResponse response) {

        try {
            // 设置响应头，返回加密文件
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"license.lic.sm4\"");
            // 将license信息写入json字符串
            String jsonContent = JsonUtils.toJsonString(reqVO);
            // 使用SM4加密字符串,将加密后的内容写入响应输出流
            String sm4Encrypt = sm4Encrypt(jsonContent, LICENSE_SECRET_KEY);
            response.getOutputStream().write(sm4Encrypt.getBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("导出License失败", e);
            throw exception(LICENSE_CREATE_ERROR);
        }
    }

    /**
     * 更新License
     *
     * @param reqVO License更新请求参数
     */
    @Override
    public void updateLicense(LicenseSaveReqVO reqVO) {
        LicenseDO license = BeanUtils.toBean(reqVO, LicenseDO.class);
        licenseDataRepository.update(license);
    }

    /**
     * 删除License
     *
     * @param id License主键ID
     */
    @Override
    public void deleteLicense(Long id) {
        licenseDataRepository.deleteById(id);
    }

    /**
     * 获取License详情
     *
     * @param id License主键ID
     * @return License详情
     */
    @Override
    public LicenseDO getLicense(Long id) {
        return licenseDataRepository.findById(id);
    }

    @Override
    public LicenseDO getLatestActiveLicense() {
        List<LicenseDO> licenseDOList = licenseDataRepository.findActiveLicenseList();
        if (CollectionUtils.isEmpty(licenseDOList)) {
            log.error("error -------------> License为空！！！");
            return null;
        }
        if (licenseDOList.size() > 1) {
            log.error("error -------------> 存在多个激活的License！！！");
        }
        return licenseDOList.get(0);
    }

    /**
     * 根据状态获取License
     *
     * @param status License状态
     * @return License
     */
    @Override
    public LicenseDO getLicenseByStatus(String status) {
        return licenseDataRepository.findOneByStatus(status);
    }

    /**
     * 分页查询License
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<LicenseDO> getLicensePage(LicensePageReqVO reqVO) {
        return licenseDataRepository.findPage(reqVO);
    }

    /**
     * 获取全部License（精简信息）
     *
     * @return License列表
     */
    @Override
    public List<LicenseDO> getSimpleLicenseList() {
        return licenseDataRepository.findSimpleList();
    }

    @Override
    public List<LicenseDO> getEnableLicenseList() {
        return licenseDataRepository.findAllByConfig(new DefaultConfigStore()
                .eq(LicenseDO.STATUS, LicenseStatusEnum.ENABLE.getStatus()));
    }

    @Override
    @Transactional
    @LogRecord(type = SYSTEM_LICENSE_TYPE, subType = SYSTEM_LICENSE_IMPORT_SUB_TYPE, bizNo = "{{#licenseId}}",
            success = SYSTEM_LICENSE_IMPORT_SUCCESS)
    public Long importLicense(MultipartFile file) {


        try {
            // 直接从MultipartFile获取字节数据并转换为字符串
            byte[] encryptedBytes = file.getBytes();
            String encryptedContent = new String(encryptedBytes, StandardCharsets.UTF_8);
            // 解密内容
            String decryptedContent = SM4Utils.sm4Decrypt(encryptedContent, LICENSE_SECRET_KEY);
            log.info("License解析内容: {}", decryptedContent);
            // 先将旧License置为失效
            // 如果是新创建的license，将其他所有已认证的license更新为已失效状态
            List<LicenseDO> licenses = getEnableLicenseList();
            if (CollectionUtils.isEmpty(licenses)) {
                log.error("error -------------> 没有启用的License！！");
            }
            if (licenses.size() > 1) {
                log.error("error -------------> 存在多个启用的License！！！");
            }
            for (LicenseDO license : licenses) {
                LicenseSaveReqVO updateReqVO = new LicenseSaveReqVO();
                updateReqVO.setId(license.getId());
                updateReqVO.setStatus(LicenseStatusEnum.DISABLE.getStatus());
                updateLicense(updateReqVO);
                log.info("disable license ----> {}", license.getId());
            }
            // 在插入新的 License
            LicenseSaveReqVO licenseSaveReqVO = JsonUtils.parseObject(decryptedContent, LicenseSaveReqVO.class);
            LocalDateTime expireTime = licenseSaveReqVO.getExpireTime();
            if (expireTime.isBefore(LocalDateTime.now())) {
                // 定义时间格式
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                // 转换为指定格式的字符串
                String formattedTime = expireTime.format(formatter);
                log.error("License 过期：{}", formattedTime);
                throw exception(GlobalErrorCodeConstants.LICENSE_IS_EXPIRED);
            }
            licenseSaveReqVO.setLicenseFile(encryptedContent);
            // 创建License时，将状态设置为ENABLE
            licenseSaveReqVO.setStatus(LicenseStatusEnum.ENABLE.getStatus());
            Long licenseId = createLicense(licenseSaveReqVO);

            // 删除License缓存，确保下次获取License时，会重新从数据库中获取
            stringRedisTemplate.delete(LICENSE_KEY);

            log.info("insert and enable new license ------> {}", licenseId);

            // 记录操作日志上下文
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

            LogRecordContext.putVariable("licenseId", licenseId);
            LogRecordContext.putVariable("loginUser", loginUser);

            return licenseId;
        } catch (Exception e) {
            log.error("导入License失败", e);
            throw exception(LICENSE_IMPORT_ERROR);
        }
    }

    @Override
    public void exportLicense(Long id, HttpServletResponse response) {
        try {
            // 从数据库中根据ID查询license
            LicenseDO license = getLicense(id);

            if (license == null) {
                throw exception(LICENSE_NOT_EXISTS, id);
            }
            LicenseExportRespVO licenseExportRespVO = LicenseConvert.INSTANCE.convertToExportVO(license);

            // 设置响应头，返回加密文件
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"license.lic.sm4\"");
            // 将license信息写入json字符串
            String jsonContent = JsonUtils.toJsonString(licenseExportRespVO);

            // 使用SM4加密字符串,将加密后的内容写入响应输出流
            String sm4Encrypt = sm4Encrypt(jsonContent, LICENSE_SECRET_KEY);
            response.getOutputStream().write(sm4Encrypt.getBytes());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("导出License失败", e);
            throw exception(LICENSE_EXPORT_ERROR);
        }
    }


}
