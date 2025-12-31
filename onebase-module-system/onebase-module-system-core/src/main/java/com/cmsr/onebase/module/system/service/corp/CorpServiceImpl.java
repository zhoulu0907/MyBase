package com.cmsr.onebase.module.system.service.corp;

import cn.hutool.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.biz.system.dict.DictDataCommonApi;
import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.service.dict.DictDataService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.corp.*;
import com.cmsr.onebase.module.system.vo.corpapprelation.AppAuthTimeReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;


/**
 * 企业服务实现类
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Service
@Validated
@Slf4j
@EnableTransactionManagement
public class CorpServiceImpl implements CorpService {

    // 租户管理员设置默认密码
    private static final String CORP_ADMIN_PASSWORD = "CorpChina2025!";

    @Resource
    private CorpDataRepository corpDataRepository;

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private CorpAppRelationService corpAppRelationService;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Resource
    private DictDataCommonApi dictDataApi;

    @Resource
    private DictDataService dictDataService;

    @Resource
    @Lazy
    private TenantService tenantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_CORP_TYPE, subType = SYSTEM_CORP_CREATE_SUB_TYPE, bizNo = "{{#corpId}}",
            success = SYSTEM_CORP_CREATE_SUCCESS)
    public CorpAdminUserRespVO createCorpCombined(CorpCombinedVo corpCombineReqVO) {
        // 保存基础数据
        Long corpId = createCorp(corpCombineReqVO.getCorpReqVO());
        // 保存系统管理员
        CorpAdminUserRespVO vo = createAdminUser(corpCombineReqVO.getCorpAdminReqVO(), corpId);
        // 保存关联关系
        List<AppAuthTimeReqVO> appAuthTimeReqVO = corpCombineReqVO.getAppAuthTimeReqVO();

        createListCorpAppRelation(appAuthTimeReqVO, corpId);
        // 更新企业管理员Id
        updateCorpAdminIdById(corpId, vo.getId());

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("corpName", corpCombineReqVO.getCorpReqVO().getCorpName());
        LogRecordContext.putVariable("corpId", corpId);
        return vo;
    }

    private void createListCorpAppRelation(List<AppAuthTimeReqVO> appAuthTimeReqVOs, Long corpId) {
        corpAppRelationService.createListCorpAppRelation(appAuthTimeReqVOs, corpId);
    }


    public Long createCorp(CorpReqVO reqVO) {
        // 验证企业基本信息
        validCreateCorp(reqVO);

        CorpDO corpDO = BeanUtils.toBean(reqVO, CorpDO.class);
        corpDO.setTenantId(TenantContextHolder.getTenantId());

        corpDataRepository.insert(corpDO);
        return corpDO.getId();
    }

    private Integer getExistUserLimitExcludeCorp(Long corpId) {
        List<CorpDO> corpList = corpDataRepository.getAllEnableCorp();
        return corpList.stream()
                .filter(corp -> !Objects.equals(corp.getId(), corpId))
                .filter(corp -> corp.getUserLimit() != null)
                .mapToInt(CorpDO::getUserLimit)
                .sum();
    }

    private void validCorpUserMaxCountLimit(Integer userCount, Long corpId) {
        if (userCount != null && userCount > CorpConstant.USER_LIMIT) {
            throw exception(CORP_USER_LIMIT_COUNT, userCount);
        }
        // 验证同一空间内企业数据是否超出限制
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        TenantDO tenantDO = tenantService.getTenant(loginUser.getTenantId());
        // 空间用户总数
        Integer tenantUserLimit = tenantDO.getAccountCount();
        // 获取企业已存在数量
        Integer existUserLimit = getExistUserLimitExcludeCorp(corpId);
        if (existUserLimit + userCount > tenantUserLimit) {
            Integer remainingCount = tenantUserLimit - existUserLimit;
            throw exception(CORP_USER_LIMIT_COUNT_CHECK, tenantUserLimit, remainingCount);
        }
    }

    private void validCorpNameDuplicate(String name, Long corpId) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        CorpDO corpDO = corpDataRepository.findCorpByName(name);
        if (corpDO == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
        if (corpId == null) {
            throw exception(CORP_NAME_EXISTS, name);
        }
        if (!corpDO.getId().equals(corpId)) {
            throw exception(CORP_NAME_EXISTS, name);
        }
    }

    private void validCorpCodeDuplicate(String corpCode, Long corpId) {
        if (StringUtils.isBlank(corpCode)) {
            return;
        }
        CorpDO corpDO = corpDataRepository.findCorpByCorpCode(corpCode);
        if (corpDO == null) {
            return;
        }

        // 如果 id 为空，说明不用比较是否为相同名字的租户
        if (corpId == null) {
            throw exception(CORP_ID_EXISTS, corpCode);
        }
        if (!corpDO.getId().equals(corpId)) {
            throw exception(CORP_ID_EXISTS, corpCode);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_CORP_TYPE, subType = SYSTEM_CORP_UPDATE_SUB_TYPE, bizNo = "{{#corp.id}}",
            success = SYSTEM_CORP_UPDATE_SUCCESS)
    public void updateCorp(CorpUpdateReqVO reqVO) {
        CorpDO checkCorp = corpDataRepository.findById(reqVO.getId());
        if (checkCorp == null) {
            throw exception(CORP_NO_EXISTS, reqVO.getCorpName());
        }
        // 用于校验企业名称是否已存在
        validCorpNameDuplicate(reqVO.getCorpName(),reqVO.getId());
        validCorpCodeDuplicate(reqVO.getCorpCode(),reqVO.getId());

        if (null != reqVO.getUserLimit()) {
            //  检查1：用户数下限，不能小于企业已有开启状态的用户实际数量
            validCorpUserMinCountLimit(reqVO.getUserLimit(), reqVO.getId());
            // 检查2：用户数上限，不能大于空间下可用的用户数量
            validCorpUserMaxCountLimit(reqVO.getUserLimit(), reqVO.getId());
        }
        CorpDO corpDO = BeanUtils.toBean(reqVO, CorpDO.class);
        corpDataRepository.update(corpDO);

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("corp", corpDO);
    }

    private void validCorpUserMinCountLimit(Integer userLimit, Long corpId) {
        // 获取已存在的空间用户数
        Map<Long, Integer> existUserCountMap = userService.getCorpExistUserCountByCorpIds(List.of(corpId));
        Integer existUserCountInt = existUserCountMap.get(corpId);
        int existUserCount = existUserCountInt != null ? existUserCountInt : 0;
        if (userLimit < existUserCount) {
            throw exception(CORP_USER_EXITES_LIMIT_COUNT_CHECK, existUserCount);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCorpAdminIdById(Long corpId, Long adminId) {
        // 企业修改管理员Id
        corpDataRepository.updateCorpAdminId(corpId, adminId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_CORP_TYPE, subType = SYSTEM_CORP_DELETE_SUB_TYPE, bizNo = "{{#corp.id}}",
            success = SYSTEM_CORP_DELETE_SUCCESS)
    public void deleteCorp(Long id) {
        // 查询企业
        CorpDO corp = corpDataRepository.findById(id);
        // 删除企业
        corpDataRepository.deleteById(id);
        // 删除关联关系
        corpAppRelationService.deleteCorpAppRelationByCorpId(id);

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("corp", corp);
    }


    @Override
    public PageResult<CorpRespVO> getCorpAppsPage(CorpPageReqVO pageReqVO) {

        /**
         * 根据企业分页条件，查询企业及其关联应用信息的分页列表
         *
         * @param pageReqVO 分页与筛选条件
         * @return PageResult<CorpRespVO> 企业-应用信息分页结果
         */
        // Step 1：分页查询企业列表
        PageResult<CorpDO> pageResult = corpDataRepository.selectPage(pageReqVO);
        List<CorpDO> corpList = pageResult.getList();
        if (CollectionUtils.isEmpty(corpList)) {
            return new PageResult<>(Collections.emptyList(), pageResult.getTotal());
        }

        // 提取当前页企业ID集合
        Set<Long> corpIds = corpList.stream()
                .map(CorpDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Step 2：根据企业列表查询关联的应用ID列表（先拉取关联关系，再按当前页企业过滤）
        CorpAppRelationPageReqVO relationReqVO = new CorpAppRelationPageReqVO();
        relationReqVO.setCorpIds(corpIds);

        List<CorpAppRelationDO> appRelations = corpAppRelationService.getCorpAppRelationList(relationReqVO);

        CommonResult<List<DictDataRespDTO>> dictlist = dictDataApi.getDictDataList(CorpConstant.INDUSTRY_TYPE);
        Map<Long, String> dictmap = dictlist.getData().stream()
                .collect(Collectors.toMap(DictDataRespDTO::getId
                        , DictDataRespDTO::getLabel));
        Set<Long> adminUserIds = corpList.stream()
                .map(CorpDO::getAdminId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AdminUserDO> userDOMap = userService.getUserMap(adminUserIds);

        if (appRelations.isEmpty()) {
            // 无关联应用，直接返回企业基本信息
            List<CorpRespVO> noAppResp = corpList.stream()
                    .map(corpDO -> {
                        CorpRespVO respVO = BeanUtils.toBean(corpDO, CorpRespVO.class);
                        AdminUserDO adminUser = userDOMap.get(corpDO.getAdminId());
                        if (adminUser != null) {
                            respVO.setAdminName(adminUser.getNickname());
                            respVO.setAdminMobile(adminUser.getMobile());
                            respVO.setAdminEmail(adminUser.getEmail());
                        }
                        respVO.setIndustryTypeName(dictmap.get(respVO.getIndustryType()));
                        return respVO;
                    })
                    .collect(Collectors.toList());
            return new PageResult<>(noAppResp, pageResult.getTotal());
        }

        // Step 3：根据应用ID列表，查询所有应用详情列表（全量拉取后按ID过滤）
        Set<Long> appIds = appRelations.stream()
                .map(CorpAppRelationDO::getApplicationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<ApplicationDTO> allApps = appApplicationApi.findAppApplicationByAppIds(appIds);
        Map<Long, ApplicationDTO> applicationMap = (allApps == null ? Collections.<ApplicationDTO>emptyList() : allApps)
                .stream()
                .filter(Objects::nonNull)
                .filter(a -> appIds.contains(a.getId()))
                .collect(Collectors.toMap(ApplicationDTO::getId, Function.identity(), (a, b) -> a));

        // 关联关系按企业分组，便于组装
        Map<Long, List<CorpAppRelationDO>> relationGroupByCorp = appRelations.stream()
                .collect(Collectors.groupingBy(CorpAppRelationDO::getCorpId));
        // Step 4：组装返回值
        List<CorpRespVO> respList = corpList.stream()
                .map(corpDO -> {
                    CorpRespVO respVO = BeanUtils.toBean(corpDO, CorpRespVO.class);
                    List<CorpAppRelationDO> corpRels = relationGroupByCorp.getOrDefault(corpDO.getId(), Collections.emptyList());
                    if (!corpRels.isEmpty()) {
                        List<CorpAppVo> corpApplicationList = new ArrayList<>();
                        for (CorpAppRelationDO rel : corpRels) {
                            ApplicationDTO dto = applicationMap.get(rel.getApplicationId());
                            if (dto != null) {
                                CorpAppVo vo = new CorpAppVo();
                                vo.setAppId(dto.getId());
                                vo.setAppName(dto.getAppName());
                                vo.setIconName(dto.getIconName());
                                vo.setIconColor(dto.getIconColor());
                                corpApplicationList.add(vo);
                            }
                        }
                        respVO.setCorpApplicationList(corpApplicationList);
                    }
                    AdminUserDO adminUser = userDOMap.get(corpDO.getAdminId());
                    if (adminUser != null) {
                        respVO.setAdminName(adminUser.getNickname());
                        respVO.setAdminMobile(adminUser.getMobile());
                        respVO.setAdminEmail(adminUser.getEmail());
                    }
                    respVO.setIndustryTypeName(dictmap.get(respVO.getIndustryType()));
                    return respVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(respList, pageResult.getTotal());
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
        AdminUserDO userDO = userService.getUser(corpDO.getAdminId());
        if (userDO != null) {
            respVO.setAdminName(userDO.getNickname());
            respVO.setAdminEmail(userDO.getEmail());
            respVO.setAdminMobile(userDO.getMobile());
        }
        respVO.setAppCount(getCorpAppCount(id));
        Long userCountLong = userService.getUserCountByCorpId(id);
        Integer userCount = (userCountLong != null) ? userCountLong.intValue() : 0;
        respVO.setUserCount(userCount);

        DictDataDO dictData = dictDataService.getDictData(respVO.getIndustryType());
        if (null != dictData) {
            respVO.setIndustryTypeName(dictData.getLabel());
        }
        return respVO;
    }

    /**
     * 获取企业关联的应用数量
     *
     * @param corpId 企业ID
     * @return 应用数量
     */
    public Integer getCorpAppCount(Long corpId) {
        CorpAppRelationPageReqVO relationReqVO = new CorpAppRelationPageReqVO();
        Set<Long> corpIds = new HashSet<>();
        corpIds.add(corpId);
        relationReqVO.setCorpIds(corpIds);
        List<CorpAppRelationDO> relations = corpAppRelationService.getCorpAppRelationList(relationReqVO);
        return relations != null ? relations.size() : 0;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public CorpAdminUserRespVO createAdminUser(CorpAdminReqVO reqVO, Long corpId) {
        // 2.2.1 判断如果不存在，在进行插入
        AdminUserDO existUser = userService.getUserByUsername(reqVO.getUsername());
        if (existUser != null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        // 插入用户
        AdminUserDO user = BeanUtils.toBean(reqVO, AdminUserDO.class);
        // 暂时注掉使用默认，方便测试
        // String password = PasswordRandomGenerator.generateSecurePassword(15);
        String password = CORP_ADMIN_PASSWORD;
        user.setPassword(encodePassword(CORP_ADMIN_PASSWORD)); // 加密密码
        user.setCorpId(corpId);
        // 在空间创建空企业（Tenant）用户
        user.setUserType(UserTypeEnum.CORP.getValue());
        Long userId = userService.createCorpAdminUser(user);

        CorpAdminUserRespVO vo = new CorpAdminUserRespVO();
        vo.setUsername(reqVO.getUsername());
        vo.setMobile(reqVO.getMobile());
        vo.setPassword(password);
        vo.setId(userId);
        vo.setCorpId(corpId);
        return vo;

    }

    @Override
    public void updateStatus(Long id, Long status) {
        // 企业禁用/开启
        corpDataRepository.updateStatus(id, status == null ? null : status.intValue());
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

    public void validCreateCorp(CorpReqVO corpReqVO) {
        // 用于校验企业名称是否已存在
        validCorpNameDuplicate(corpReqVO.getCorpName(),null);
        // 用于校验企业ID是否已存在
        validCorpCodeDuplicate(corpReqVO.getCorpCode(),null);
        // 用于校验企业用户数量是否超过限制（如大于500）
        validCorpUserMaxCountLimit(corpReqVO.getUserLimit(), null);
    }


    @Override
    public void checkCorp(CorpReqVO corpReqVO) {
        validCreateCorp(corpReqVO);
    }

    @Override
    public void checkCorpAdminUser(CorpAdminReqVO corpAdminReqVO) {
        AdminUserDO user = BeanUtils.toBean(corpAdminReqVO, AdminUserDO.class);
        userService.checkCorpAdminUser(user);
    }

    @Override
    public List<CorpDO> getAllCorpList() {
        return corpDataRepository.getAllCorpList();
    }

}
