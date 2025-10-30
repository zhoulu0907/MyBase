package com.cmsr.onebase.module.system.service.corp;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.enterprise.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import com.cmsr.onebase.module.system.vo.corp.*;
import com.cmsr.onebase.module.system.vo.corpapprelation.AppAuthTimeReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationVO;
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
import java.util.Map;
import java.util.Random;
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
    @Resource
    private AppApplicationApi appApplicationApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CorpAdminUserRespVO createCorpCombined(CorpCombinedVo corpCombinReqVO) {
        // 保存基础数据
        Long corpId = createCorp(corpCombinReqVO.getCorpReqVO());
        // 保存系统管理员
        CorpAdminUserRespVO vo = createUser(corpCombinReqVO.getCorpAdminReqVO());
        // 保存关联关系
        AppAuthTimeReqVO appAuthTimeReqVO = corpCombinReqVO.getAppAuthTimeReqVO();
        createCorpAppRelation(appAuthTimeReqVO, corpId);
        return vo;
    }


    public Long createCorp(CorpReqVO reqVO) {
        //用于校验企业名称是否已存在
        validCorpNameDuplicate(reqVO.getCorpName());
        //用于校验企业用户数量是否超过限制（如大于500）
        validCorpUserCountDuplicate(reqVO.getUserLimit());
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
        validCorpUserCountDuplicate(reqVO.getUserLimit());
        CorpDO checkEnterprise = corpDataRepository.findById(reqVO.getId());
        if (checkEnterprise == null) {
            throw exception(ENTERPRRISE_NO_EXISTS, reqVO.getCorpName());
        }
        CorpDO corpDO = BeanUtils.toBean(reqVO, CorpDO.class);
        corpDataRepository.update(corpDO);
    }

    public void createCorpAppRelation(AppAuthTimeReqVO createReqVO, Long corpId) {
        CorpAppRelationInertReqVO createCorpAppRelation = BeanUtils.toBean(createReqVO, CorpAppRelationInertReqVO.class);
        createCorpAppRelation.setCorpId(corpId);
        corpAppRelationService.createCorpAppRelation(createCorpAppRelation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCorp(Long id) {
        // todo  验证条件，什么样的才可以删除
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
        List<CorpDO> corpAllList = corpDataRepository.findAll();
        return corpAllList;
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
    public CorpAdminUserRespVO createUser(CorpAdminReqVO reqVO) {
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
        AdminUserDO adminUserDO = new AdminUserDO();
        CorpAdminUserRespVO vo = new CorpAdminUserRespVO();
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
     * 获取app
     *
     * @param pageReqVO
     * @return
     */

    @Override
    public PageResult<CorpApplicationRespVO> selectCorpAppRelationPage(CorpAppRelationPageReqVO pageReqVO) {
        // 调用数据仓库进行分页查询
        PageResult<CorpAppRelationVO> pageResult = null;//corpAppRelationService.getCorpAppRelationPage(pageReqVO);
        // 如果入参包含应用名称查询条件，进行返回值过滤
        List<CorpAppRelationVO> filteredList = pageResult.getList();
        if (pageReqVO.getApplicationName() != null && !pageReqVO.getApplicationName().trim().isEmpty()) {
            // 获取所有应用信息用于过滤
            List<ApplicationDO> allApplications = appApplicationApi.finAppApplicationAll();
            Map<Long, String> appIdNameMap = allApplications.stream()
                    .collect(Collectors.toMap(ApplicationDO::getId, ApplicationDO::getAppName));

            // 根据应用名称过滤结果
            filteredList = pageResult.getList().stream()
                    .filter(vo -> {
                        String appName = appIdNameMap.get(vo.getApplicationId());
                        return appName != null && appName.contains(pageReqVO.getApplicationName());
                    })
                    .collect(Collectors.toList());
        }

        // 将过滤后的 DO 对象转换为 VO 对象
        return new PageResult<CorpApplicationRespVO>(
                filteredList.stream()
                        .map(corpAppRelationVO -> {
                            CorpApplicationRespVO corpRespVO = BeanUtils.toBean(corpAppRelationVO, CorpApplicationRespVO.class);
                            return corpRespVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                Long.valueOf(filteredList.size()) // 转换为 Long 类型
        );
    }

    /**
     * 获取企业精简列表
     *
     * @return List<CorpDO>
     */
    @Override
    public List<CorpDO> getSimpleCorpList(Integer staus) {
        List<CorpDO> corpDOList =corpDataRepository.getSimpleCorpList(staus);
        return corpDOList;
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
