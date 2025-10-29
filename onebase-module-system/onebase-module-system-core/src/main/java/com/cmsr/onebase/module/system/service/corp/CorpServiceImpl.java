package com.cmsr.onebase.module.system.service.corp;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationVO;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.enterprise.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.service.corpAppRelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import com.cmsr.onebase.module.system.vo.corp.*;
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
    public CorpUserRespVO createCorpCombined(CorpCombinedVo corpCombinReqVO) {
        // 保存基础数据
        createCorp(corpCombinReqVO.getCorpRespVO());
        // 保存系统管理员
        CorpUserRespVO vo = createUser(corpCombinReqVO.getCorpUserReqVO());
        // 保存关联关系
        createCorpAppRelation(corpCombinReqVO.getCorpAppRelationInertReqVO());
        return vo;
    }

    public Long createCorp(CorpRespVO reqVO) {
        //用于校验企业名称是否已存在
        validCorpNameDuplicate(reqVO.getCorpName());
        //用于校验企业用户数量是否超过限制（如大于500）
        validCorpUserCountDuplicate(reqVO.getUserCount());
        CorpDO enterprise = BeanUtils.toBean(reqVO, CorpDO.class);
        return corpDataRepository.insert(enterprise).getId();
    }

    private void validCorpUserCountDuplicate(Integer userCount) {
        if (userCount != null && userCount > CorpConstant.USER_LIMIT) {
            throw exception(ENTERPRRISE_USER_COUNT, userCount);
        }
    }


    private void validCorpNameDuplicate(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        CorpDO corpDO = corpDataRepository.findByName(name);
        if (corpDO != null) {
            throw exception(ENTERPRRISE_EXISTS, name);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCorp(CorpUpdateReqVO reqVO) {
        validCorpUserCountDuplicate(reqVO.getUserCount());
        CorpDO checkEnterprise = corpDataRepository.findById(reqVO.getId());
        if (checkEnterprise == null) {
            throw exception(ENTERPRRISE_NO_EXISTS, reqVO.getCorpName());
        }
        CorpDO corpDO = BeanUtils.toBean(reqVO, CorpDO.class);
        corpDataRepository.update(corpDO);
    }

    public void createCorpAppRelation(CorpAppRelationInertReqVO createReqVO) {
        corpAppRelationService.createCorpAppRelation(createReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCorp(Long id) {
        // todo  验证是否可以删除
        corpDataRepository.deleteById(id);
    }

    @Override
    public PageResult<CorpRespVO> getCorpPage(CorpPageReqVO pageReqVO) {

        // 调用数据仓库进行分页查询
        PageResult<CorpDO> pageResult = corpDataRepository.selectPage(pageReqVO);

        // 将 DO 对象转换为 VO 对象
        return new PageResult<CorpRespVO>(
                pageResult.getList().stream()
                        .map(corpDO -> {
                            CorpRespVO corpRespVO = BeanUtils.toBean(corpDO, CorpRespVO.class);
                            return corpRespVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }

    @Override
    public List<CorpDO> findCorpAll() {
        List<CorpDO> corpList = corpDataRepository.findAll();
        return corpList;
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

    @Override
    public CorpUserRespVO createUser(CorpUserReqVO reqVO) {

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
        // todo  adminUserService.createUser(user);
        AdminUserDO adminUserDO = null;
        CorpUserRespVO vo = new CorpUserRespVO();
        vo.setUsername(reqVO.getUsername());
        vo.setPassword(password);
        vo.setId(adminUserDO.getId());
        return vo;

    }

    @Override
    public void updateStatus(Long id, Long status) {
        //  企业禁用
        DataRow row = new DataRow();
        row.put("status", status);
        corpDataRepository.updateByConfig(row, new DefaultConfigStore().eq(CorpDO.ID, id));

    }


    /**
     * 获取应用名称
     *
     * @param
     * @return Map<Long, String> key为企业ID，value为企业名称
     */
    private Map<Long, ApplicationDO> getCorpNameMap() {
        ConfigStore configs = new DefaultConfigStore();
        // todo  调用应用接口，获取应用数据
        List<ApplicationDO> pageDOList = null;

        // 将 List<ApplicationDO> 转换为 Map<Long, String>
        return pageDOList.stream()
                .collect(Collectors.toMap(
                        ApplicationDO::getId,           // key: id
                        Function.identity() // value: enterpriseName
                ));
    }

    /**
     * 获取app
     *
     * @param pageReqVO
     * @return
     */

    @Override
    public PageResult<CorpApplicationRespVO> selectCorpAppRelationPage(CorpAppRelationPageReqVO pageReqVO) {
        // 调用数据仓库进行分页查询
        PageResult<CorpAppRelationVO> pageResult = corpAppRelationService.getCorpAppRelationPage(pageReqVO);
        Map<Long, ApplicationDO> applicationMap = getCorpNameMap();

        // 将 DO 对象转换为 VO 对象
        return new PageResult<CorpApplicationRespVO>(
                pageResult.getList().stream()
                        .map(applicationAuthEnterpriseDO -> {
                            CorpApplicationRespVO enterpriseRespVO = new CorpApplicationRespVO();
                            ApplicationDO appDo = applicationMap.get(applicationAuthEnterpriseDO.getApplicationId());
                            if (appDo != null) {
                                enterpriseRespVO.setApplicationName(appDo.getAppName());
                                enterpriseRespVO.setApplicationCode(appDo.getAppCode());
                            }
                            enterpriseRespVO.setApplicationId(applicationAuthEnterpriseDO.getApplicationId() + "");
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
