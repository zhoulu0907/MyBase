package com.cmsr.onebase.module.system.service.corp;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.infra.api.security.SecurityConfigApi;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.core.dto.app.ApplicationDTO;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import com.cmsr.onebase.module.system.util.encrypt.PasswordRandomGenerator;
import com.cmsr.onebase.module.system.vo.corp.*;
import com.cmsr.onebase.module.system.vo.corpapprelation.AppAuthTimeReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
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

    @Resource
    private AppApplicationApi appApplicationApi;

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
        // 获取应用数据,根据应用id封装map
        List<ApplicationDTO> applicationDTOS = appApplicationApi.findAppApplicationByAppName(null);
        CorpAppRelationPageReqVO corpAppRelationPageReqVO = BeanUtils.toBean(pageReqVO, CorpAppRelationPageReqVO.class);

        Map<Long, ApplicationDTO> applicationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(applicationDTOS)) {
            applicationMap = applicationDTOS.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(ApplicationDTO::getId, Function.identity()));
        }
        // 获取关联关系，根据企业id 分组获取 关联的应用
        List<CorpAppRelationDO> corpAppRelationDOList = corpAppRelationService.getCorpAppRelationList(corpAppRelationPageReqVO);

        // 封装成Map，key是企业id，value是分组后的list
       Map<Long, List<CorpAppRelationDO>> corpAppRelationMap = new HashMap<>();
        if (com.alibaba.nacos.common.utils.CollectionUtils.isNotEmpty(corpAppRelationDOList)) {
            corpAppRelationMap = corpAppRelationDOList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(CorpAppRelationDO::getCorpId));
        }


        // 赋值corpApplicationList 授权应用数据获取
        Map<Long, List<CorpAppRelationDO>> finalCorpAppRelationMap = corpAppRelationMap;
        Map<Long, ApplicationDTO> finalApplicationMap = applicationMap;
        return new PageResult<CorpRespVO>(
                pageResult.getList().stream()
                        .map(corpDO -> {
                            CorpRespVO corpRespVO = BeanUtils.toBean(corpDO, CorpRespVO.class);
                            // 如果企业id在corpAppRelationMap中存在，设置corpApplicationList
                            if (finalCorpAppRelationMap != null && finalCorpAppRelationMap.containsKey(corpDO.getId())) {
                                List<CorpAppRelationDO> corplist= finalCorpAppRelationMap.get(corpDO.getId());
                                List<CorpAppVo> corpApplicationList=new ArrayList();
                                if (corplist != null && !corplist.isEmpty()) {
                                    for (CorpAppRelationDO corpAppRelationDO : corplist) {
                                        Long  applicationId=corpAppRelationDO.getApplicationId();
                                        if(finalApplicationMap.get(applicationId)!=null){
                                            ApplicationDTO dto= finalApplicationMap.get(applicationId);
                                            CorpAppVo vo=new CorpAppVo();
                                            vo.setAppCount(corplist.size());
                                            vo.setAppName(dto.getAppName());
                                            vo.setIconName(dto.getIconName());
                                            corpApplicationList.add(vo);
                                        }
                                    }
                                }
                                corpRespVO.setCorpApplicationList(corpApplicationList);
                            }
                            return corpRespVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }

    @Override
    @TenantIgnore
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
        String password = PasswordRandomGenerator.generateSecurePassword(15); // 生成15位随机密码
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

}
