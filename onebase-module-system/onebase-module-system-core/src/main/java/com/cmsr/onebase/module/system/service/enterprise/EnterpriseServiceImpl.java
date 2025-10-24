package com.cmsr.onebase.module.system.service.enterprise;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.*;
import com.cmsr.onebase.module.system.dal.database.AdminUserDataRepository;
import com.cmsr.onebase.module.system.dal.database.ApplicationAuthEnterpriseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.ApplicationAuthEnterpriseDO;
import com.cmsr.onebase.module.system.dal.dataobject.enterprise.EnterpriseDO;
import com.cmsr.onebase.module.system.dal.database.EnterpriseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;


/**
 * 企业服务实现类
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Service
@Validated
@Slf4j
public class EnterpriseServiceImpl implements EnterpriseService {

    @Resource
    private EnterpriseDataRepository enterpriseDataRepository;

    @Resource
    private AdminUserDataRepository adminUserDataRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private ApplicationAuthEnterpriseDataRepository applicationAuthEnterpriseDataRepository;

    @Resource
    private AppApplicationRepository appApplicationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEnterprise(EnterpriseSaveReqVO reqVO) {

        //用于校验企业名称是否已存在
        validEnterpriseNameDuplicate(reqVO.getEnterpriseName());
        //用于校验企业用户数量是否超过限制（如大于500）
        validEnterpriseUserCountDuplicate(reqVO.getUserCount());

        EnterpriseDO enterprise = BeanUtils.toBean(reqVO, EnterpriseDO.class);
        enterprise.setLockVersion(0L);
        enterprise.setTenantId(reqVO.getTenantId());
        enterprise.setCreateTime(java.time.LocalDateTime.now());
        enterprise.setUpdateTime(java.time.LocalDateTime.now());
        enterprise.setDeleted(0L);
        return enterpriseDataRepository.insert(enterprise).getId();
    }

    private void validEnterpriseUserCountDuplicate(Integer userCount) {
        if (userCount != null && userCount > 500) {
            throw exception(ENTERPRRISE_USER_COUNT, userCount);
        }
    }


    private void validEnterpriseNameDuplicate(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        EnterpriseDO enterpriseDO = enterpriseDataRepository.findByName(name);
        if (enterpriseDO != null) {
            throw exception(ENTERPRRISE_EXISTS, name);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnterprise(EnterpriseSaveReqVO reqVO) {
        validEnterpriseUserCountDuplicate(reqVO.getUserCount());
        EnterpriseDO checkEnterprise = enterpriseDataRepository.findById(reqVO.getId());
        if (checkEnterprise == null) {
            throw exception(ENTERPRRISE_NO_EXISTS, reqVO.getEnterpriseName());
        }
        EnterpriseDO enterprise = BeanUtils.toBean(reqVO, EnterpriseDO.class);
        enterprise.setUpdateTime(java.time.LocalDateTime.now());
        enterpriseDataRepository.update(enterprise);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnterprise(Long id) {
        DataRow row = new DataRow();
        row.put(EnterpriseDO.ID, id);
        row.put("deleted", 1L);
        row.put("update_time", java.time.LocalDateTime.now());
        enterpriseDataRepository.updateByConfig(row, new DefaultConfigStore().eq(EnterpriseDO.ID, id));
    }

    @Override
    public PageResult<EnterpriseRespVO> getEnterprisePage(EnterprisePageReqVO pageReqVO) {

        // 调用数据仓库进行分页查询
        PageResult<EnterpriseDO> pageResult = enterpriseDataRepository.selectPage(pageReqVO);

        // 将 DO 对象转换为 VO 对象
        return new PageResult<EnterpriseRespVO>(
                pageResult.getList().stream()
                        .map(enterpriseDO -> {
                            EnterpriseRespVO enterpriseRespVO = BeanUtils.toBean(enterpriseDO, EnterpriseRespVO.class);
                            return enterpriseRespVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }

    @Override
    public EnterpriseRespVO getEnterprise(Long id) {
        EnterpriseDO enterprise = enterpriseDataRepository.findById(id);
        if (enterprise == null) {
            return null;
        }

        EnterpriseRespVO respVO = BeanUtils.toBean(enterprise, EnterpriseRespVO.class);
        return respVO;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public EnterpriseUserRespVO createUser(EnterpriseUserReqVO reqVO) {

        // 2.2.1 判断如果不存在，在进行插入
        AdminUserDO existUser = adminUserDataRepository.findByUsername(reqVO.getUsername());
        if (existUser != null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        // 插入用户
        AdminUserDO user = BeanUtils.toBean(reqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        String password = getRandomPassWord();

        user.setPassword(encodePassword(password)); // 加密密码
        if (user.getAdminType() == null) {
            user.setAdminType(AdminTypeEnum.CUSTOM.getType());
        }
        AdminUserDO adminUserDO = adminUserDataRepository.insert(user);
        EnterpriseUserRespVO vo = new EnterpriseUserRespVO();
        vo.setUsername(reqVO.getUsername());
        vo.setPassword(password);
        vo.setId(adminUserDO.getId());

        // 回写企业表管理员id
        DataRow row = new DataRow();
        row.put(EnterpriseDO.ID, reqVO.getEnterpriseId());
        row.put("admin_id", String.valueOf(adminUserDO.getId()));
        row.put("update_time", java.time.LocalDateTime.now());
        enterpriseDataRepository.updateByConfig(row, new DefaultConfigStore().eq(EnterpriseDO.ID, reqVO.getEnterpriseId()));

        return vo;

    }

    @Override
    public void updateStatus(Long id, Long status) {
        //  企业禁用
        DataRow row = new DataRow();
        row.put(EnterpriseDO.ID, id);
        row.put("status", status);
        row.put("update_time", java.time.LocalDateTime.now());
        enterpriseDataRepository.updateByConfig(row, new DefaultConfigStore().eq(EnterpriseDO.ID, id));

    }

    /**
     * 获取应用名称
     *
     * @param enterpriseId 企业ID
     * @return Map<Long, String> key为企业ID，value为企业名称
     */
    private Map<Long, ApplicationDO> getApplicationNameMap(Long enterpriseId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("enterpriseId", enterpriseId); //目前并没有用
        List<ApplicationDO> pageDOList = appApplicationRepository.findAllByConfig(configs);

        // 将 List<ApplicationDO> 转换为 Map<Long, String>
        return pageDOList.stream()
                .collect(Collectors.toMap(
                        ApplicationDO::getId,           // key: id
                        Function.identity() // value: enterpriseName
                ));
    }

    /**
     * 获取
     *
     * @param pageReqVO
     * @return
     */

    @Override
    public PageResult<EnterpriseApplicationRespVO> enterpriseApplicationPage(ApplicationAuthEnterprisePageReqVO pageReqVO) {
        // 调用数据仓库进行分页查询
        PageResult<ApplicationAuthEnterpriseDO> pageResult = applicationAuthEnterpriseDataRepository.selectPage(pageReqVO);
        Map<Long, ApplicationDO> applicationMap = getApplicationNameMap(Long.valueOf(pageReqVO.getEnterpriseId()));

        // 将 DO 对象转换为 VO 对象
        return new PageResult<EnterpriseApplicationRespVO>(
                pageResult.getList().stream()
                        .map(applicationAuthEnterpriseDO -> {
                            EnterpriseApplicationRespVO enterpriseRespVO = new EnterpriseApplicationRespVO();
                            ApplicationDO appDo = applicationMap.get(applicationAuthEnterpriseDO.getApplicationId());
                            if (appDo != null) {
                                enterpriseRespVO.setApplicationName(appDo.getAppName());
                                enterpriseRespVO.setApplicationCode(appDo.getAppCode());
                                enterpriseRespVO.setAuthTime(applicationAuthEnterpriseDO.getCreateTime());
                                enterpriseRespVO.setVersionNumber(appDo.getVersionNumber());
                                enterpriseRespVO.setExpiresTime(applicationAuthEnterpriseDO.getExpiresTime());
                            }
                            enterpriseRespVO.setApplicationId(applicationAuthEnterpriseDO.getApplicationId()+"");
                            return enterpriseRespVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }

    /**
     * 随机生成一个密码
     *
     * @return randomStr
     */
    public String getRandomPassWord() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        String randomStr = sb.toString();
        return randomStr;
    }
}
