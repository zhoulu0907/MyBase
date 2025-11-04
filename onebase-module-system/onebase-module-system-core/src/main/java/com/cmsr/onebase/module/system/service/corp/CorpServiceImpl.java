package com.cmsr.onebase.module.system.service.corp;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import com.cmsr.onebase.module.system.vo.corp.*;
import com.cmsr.onebase.module.system.vo.corpapprelation.AppAuthTimeReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Random;

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
public class CorpServiceImpl implements CorpService {

    @Resource
    private CorpDataRepository corpDataRepository;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private CorpAppRelationService corpAppRelationService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CorpAdminUserRespVO createCorpCombined(CorpCombinedVo corpCombineReqVO) {
        // 保存基础数据
        Long corpId = createCorp(corpCombineReqVO.getCorpReqVO());
        // 保存系统管理员
        CorpAdminUserRespVO vo = createAdminUser(corpCombineReqVO.getCorpAdminReqVO());
        // 保存关联关系
        AppAuthTimeReqVO appAuthTimeReqVO = corpCombineReqVO.getAppAuthTimeReqVO();
        createCorpAppRelation(appAuthTimeReqVO, corpId);
        // 更新企业管理员Id
        updateCorpAdminIdById(corpId, vo.getId());
        return vo;
    }


    public Long createCorp(CorpReqVO reqVO) {
        //用于校验企业名称是否已存在
        validCorpNameDuplicate(reqVO.getCorpName());
        //用于校验企业ID是否已存在
        validCorpIdDuplicate(reqVO.getCorpCode());
        //用于校验企业用户数量是否超过限制（如大于500）
        validCorpUserCountDuplicate(reqVO.getUserLimit());

        CorpDO corpDO = BeanUtils.toBean(reqVO, CorpDO.class);
        corpDO.setTenantId(TenantContextHolder.getTenantId());
        corpDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        return corpDataRepository.insert(corpDO).getId();
    }

    private void validCorpUserCountDuplicate(Integer userCount) {
        if (userCount != null && userCount > CorpConstant.USER_LIMIT) {
            throw exception(CORP_USER_LIMIT_COUNT, userCount);
        }
    }

    private void validCorpNameDuplicate(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        CorpDO corpDO = corpDataRepository.findCorpByName(name);
        if (corpDO != null) {
            throw exception(CORP_NAME_EXISTS, name);
        }
    }

    private void validCorpIdDuplicate(String corpCode) {
        if (StringUtils.isBlank(corpCode)) {
            return;
        }
        CorpDO corpDO = corpDataRepository.findCorpByCorpCode(corpCode);
        if (corpDO != null) {
            throw exception(CORP_ID_EXISTS, corpCode);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCorp(CorpUpdateReqVO reqVO) {
        validCorpUserCountDuplicate(reqVO.getUserLimit());
        CorpDO checkCorp = corpDataRepository.findById(reqVO.getId());
        if (checkCorp == null) {
            throw exception(CORP_NO_EXISTS, reqVO.getCorpName());
        }
        CorpDO corpDO = BeanUtils.toBean(reqVO, CorpDO.class);
        corpDataRepository.update(corpDO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCorpAdminIdById(Long corpId, Long adminId) {
        //  企业修改管理员Id
        DataRow row = new DataRow();
        row.put(CorpDO.ADMIN_ID, adminId);
        corpDataRepository.updateByConfig(row, new DefaultConfigStore().eq(CorpDO.ID, corpId));

    }

    public void createCorpAppRelation(AppAuthTimeReqVO createReqVO, Long corpId) {
        CorpAppRelationInertReqVO createCorpAppRelation = BeanUtils.toBean(createReqVO, CorpAppRelationInertReqVO.class);
        createCorpAppRelation.setCorpId(corpId);
        corpAppRelationService.createCorpAppRelation(createCorpAppRelation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCorp(Long id) {
        // 删除企业
        corpDataRepository.deleteById(id);
        // 删除关联关系
        corpAppRelationService.deleteCorpAppRelationByCorpId(id);
    }

    @Override
    public PageResult<CorpRespVO> getCorpPage(CorpPageReqVO pageReqVO) {
        // 调用数据仓库进行分页查询
        PageResult<CorpDO> pageResult = corpDataRepository.selectPage(pageReqVO);
        // 将 DO 对象转换为 VO 对象
        return new PageResult<CorpRespVO>(
                pageResult.getList().stream()
                        .map(corpDO -> BeanUtils.toBean(corpDO, CorpRespVO.class))
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }

    @Override
    public List<CorpDO> findCorpAll() {
        return corpDataRepository.findAll();
    }

    @Override
    public CorpRespVO getCorp(Long id) {
        CorpDO corpDO = corpDataRepository.findById(id);
        if (corpDO == null) {
            return null;
        }
        CorpRespVO respVO = BeanUtils.toBean(corpDO, CorpRespVO.class);
        return respVO;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public CorpAdminUserRespVO createAdminUser(CorpAdminReqVO reqVO) {
        // 2.2.1 判断如果不存在，在进行插入
        AdminUserDO existUser = adminUserService.getUserByUsername(reqVO.getUsername());
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
        Long userId = adminUserService.createCorpAdminUser(user);
        CorpAdminUserRespVO vo = new CorpAdminUserRespVO();
            vo.setUsername(reqVO.getUsername());
            vo.setPassword(password);
            vo.setId(userId);
        return vo;

    }

    @Override
    public void updateStatus(Long id, Long status) {
        //  企业禁用/开启
        DataRow row = new DataRow();
        row.put(CorpDO.STATUS, status);
        corpDataRepository.updateByConfig(row, new DefaultConfigStore().eq(CorpDO.ID, id));
    }


    /**
     * 获取企业精简列表
     *
     * @return List<CorpDO>
     */
    @Override
    public List<CorpDO> getSimpleCorpList(Integer staus) {
        return corpDataRepository.getSimpleCorpList(staus);
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
        return sb.toString();
    }
}
