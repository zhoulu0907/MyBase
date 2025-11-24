package com.cmsr.onebase.module.system.service.corp;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.cmsr.onebase.framework.common.biz.system.dict.DictDataCommonApi;
import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.util.encrypt.PasswordRandomGenerator;
import com.cmsr.onebase.module.system.vo.corp.*;
import com.cmsr.onebase.module.system.vo.corpapprelation.AppAuthTimeReqVO;
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
    private UserService corpUserService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private CorpAppRelationService corpAppRelationService;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Resource
    private   DictDataCommonApi dictDataApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CorpAdminUserRespVO createCorpCombined(CorpCombinedVo corpCombineReqVO) {
        // 保存基础数据
        Long corpId = createCorp(corpCombineReqVO.getCorpReqVO());
        // 保存系统管理员
        CorpAdminUserRespVO vo = createAdminUser(corpCombineReqVO.getCorpAdminReqVO(),corpId);
        // 保存关联关系
        List<AppAuthTimeReqVO> appAuthTimeReqVO = corpCombineReqVO.getAppAuthTimeReqVO();

        createListCorpAppRelation(appAuthTimeReqVO, corpId);
        // 更新企业管理员Id
        updateCorpAdminIdById(corpId, vo.getId());
        return vo;
    }

    private void createListCorpAppRelation(List<AppAuthTimeReqVO> appAuthTimeReqVOs, Long corpId) {
        corpAppRelationService.createListCorpAppRelation(appAuthTimeReqVOs,corpId);
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



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCorp(Long id) {
        // 删除企业
        corpDataRepository.deleteById(id);
        // 删除关联关系
        corpAppRelationService.deleteCorpAppRelationByCorpId(id);
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

        List<CorpAppRelationDO> allRelations = corpAppRelationService.getCorpAppRelationList(relationReqVO);
        List<CorpAppRelationDO> relations = (allRelations == null ? Collections.<CorpAppRelationDO>emptyList() : allRelations)
                .stream()
                .filter(Objects::nonNull)
                .toList();

        if (relations.isEmpty()) {
            // 无关联应用，直接返回企业基本信息
            List<CorpRespVO> noAppResp = corpList.stream()
                    .map(c -> BeanUtils.toBean(c, CorpRespVO.class))
                    .collect(Collectors.toList());
            return new PageResult<>(noAppResp, pageResult.getTotal());
        }

        // Step 3：根据应用ID列表，查询所有应用详情列表（全量拉取后按ID过滤）
        Set<Long> appIds = relations.stream()
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
        Map<Long, List<CorpAppRelationDO>> relationGroupByCorp = relations.stream()
                .collect(Collectors.groupingBy(CorpAppRelationDO::getCorpId));

         CommonResult<List<DictDataRespDTO>> dictlist= dictDataApi.getDictDataList(CorpConstant.INDUSTRY_TYPE);
        Map<Long, String> dictmap = dictlist.getData().stream()
                .collect(Collectors.toMap(DictDataRespDTO::getId
                        , DictDataRespDTO::getLabel));

        Set<Long> adminUserIds = corpList.stream()
                .map(CorpDO::getAdminId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AdminUserDO> userDOMap= corpUserService.getUserMap(adminUserIds);

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
                    AdminUserDO userDO=userDOMap.get(corpDO.getAdminId());
                    if(userDO!=null){
                        respVO.setAdminName(userDO.getNickname());
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
        AdminUserDO userDO=  corpUserService.getUser(corpDO.getAdminId());
        if(userDO!=null){
            respVO.setAdminName(userDO.getNickname());
            respVO.setEmail(userDO.getEmail());
            respVO.setMobile(userDO.getMobile());
        }
        respVO.setAppCount(getCorpAppCount(id));
        Long userCountLong=corpUserService.getUserCountByCorpId(id);
        Integer userCount= (userCountLong != null) ? userCountLong.intValue() : 0;
        respVO.setUserCount(userCount);
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

    public CorpAdminUserRespVO createAdminUser(CorpAdminReqVO reqVO,Long corpId) {
        // 2.2.1 判断如果不存在，在进行插入
        AdminUserDO existUser = corpUserService.getUserByUsername(reqVO.getUsername());
        if (existUser != null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        // 插入用户
        AdminUserDO user = BeanUtils.toBean(reqVO, AdminUserDO.class);
        String password = PasswordRandomGenerator.generateSecurePassword(15);
        user.setPassword(encodePassword(password)); // 加密密码
        user.setCorpId(corpId);
        Long userId = corpUserService.createCorpAdminUser(user);
        CorpAdminUserRespVO vo = new CorpAdminUserRespVO();
            vo.setUsername(reqVO.getUsername());
            vo.setMobile(reqVO.getMobile());
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
