package com.cmsr.onebase.module.system.service.corpapprelation;


import com.cmsr.onebase.framework.common.enums.CorpAppReationStatusEnum;
import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;
import com.cmsr.onebase.module.system.dal.database.CorpAppRelationDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.vo.corp.CorpApplicationRespVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.APPLICATION_AUTH_TENANT_NOT_EXISTS;

/**
 * 企业应用关联表 Service 实现类
 */
@Service
@Validated
@Slf4j
public class CorpAppRelationServiceImpl implements CorpAppRelationService {

    @Resource
    private CorpAppRelationDataRepository corpAppRelationDataRepository;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Resource
    private CorpAppRelationDataRepository corpAppRelationRepository;

    @Override
    public void createListCorpAppRelation(List<AppAuthTimeReqVO> corpAppRelationInertReqVOList, Long corpId) {
        if (CollectionUtils.isEmpty(corpAppRelationInertReqVOList)) {
            return;
        }
        // 插入
        corpAppRelationInertReqVOList.forEach(corpAppRelationInertReqVO -> {
            // 先删除后插入
            ConfigStore configs = new DefaultConfigStore();
            configs.eq(CorpAppRelationDO.APPLICATION_ID, corpAppRelationInertReqVO.getId());
            configs.eq(CorpAppRelationDO.CORP_ID, corpId);
            corpAppRelationDataRepository.deleteByConfig(configs);

            // 验证是否重复提交，先删除后插入
            CorpAppRelationDO corpAppRelationDO = new CorpAppRelationDO();
            corpAppRelationDO.setApplicationId(corpAppRelationInertReqVO.getId());
            corpAppRelationDO.setAuthorizationTime(corpAppRelationInertReqVO.getAuthorizationTime());
            corpAppRelationDO.setExpiresTime(corpAppRelationInertReqVO.getExpiresTime());
            corpAppRelationDO.setStatus(CorpStatusEnum.ENABLE.getValue());
            corpAppRelationDO.setCorpId(corpId);
            corpAppRelationDataRepository.insert(corpAppRelationDO);
        });
    }

    @Override
    public void createCorpAppRelation(CorpAppRelationInertReqVO vo) {
        // 插入
        vo.getApplicationIdList().forEach(appId -> {
            // 先删除后插入
            ConfigStore configs = new DefaultConfigStore();
            configs.eq(CorpAppRelationDO.APPLICATION_ID, appId);
            configs.eq(CorpAppRelationDO.CORP_ID, vo.getCorpId());
            corpAppRelationDataRepository.deleteByConfig(configs);

            // 验证是否重复提交，先删除后插入
            CorpAppRelationDO corpAppRelationDO = new CorpAppRelationDO();
            corpAppRelationDO.setApplicationId(appId);
            corpAppRelationDO.setAuthorizationTime(vo.getAuthorizationTime());
            corpAppRelationDO.setExpiresTime(vo.getExpiresTime());
            corpAppRelationDO.setStatus(CorpStatusEnum.ENABLE.getValue());
            corpAppRelationDO.setCorpId(vo.getCorpId());
            corpAppRelationDataRepository.insert(corpAppRelationDO);
        });

    }

    @Override
    public void updateCorpAppRelation(@Valid CorpAppRelationUpdateReqVO updateReqVO) {
        // 更新
        CorpAppRelationDO updateObj = BeanUtils.toBean(updateReqVO, CorpAppRelationDO.class);
        corpAppRelationDataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCorpAppRelation(Long id) {
        // 校验存在
        validateCorpAppRelationExists(id);
        // 删除
        corpAppRelationDataRepository.deleteById(id);
    }

    private void validateCorpAppRelationExists(Long id) {
        if (corpAppRelationDataRepository.findById(id) == null) {
            throw exception(APPLICATION_AUTH_TENANT_NOT_EXISTS);
        }
    }

    @Override
    public CorpAppRelationVO getCorpAppRelation(Long id) {
        CorpAppRelationDO applicationAuthEnterpriseDO = corpAppRelationDataRepository.findById(id);
        return BeanUtils.toBean(applicationAuthEnterpriseDO, CorpAppRelationVO.class);
    }

    /**
     * 获取应用名称
     *
     * @param
     * @return Map<Long, String> key为企业ID，value为企业名称
     */
    private Map<Long, ApplicationDTO> getApplicationDoMap(String appName) {
        List<ApplicationDTO> pageDOList = appApplicationApi.findAppApplicationByAppName(appName);
        return pageDOList.stream()
                .collect(Collectors.toMap(
                        ApplicationDTO::getId,
                        Function.identity()
                ));
    }


    @Override
    public PageResult<CorpApplicationRespVO> getCorpAppRelationPage(CorpAppPageReqVO pageReqVO) {
        // todo 空间里有调用，暂时去掉校验
        // Long corpId = pageReqVO.getCorpId();
        // Long loginCorpId = SecurityFrameworkUtils.getLoginUser().getCorpId();
        // if(!Objects.equals(corpId, loginCorpId)){
        //     throw exception(CORP_ID_COMPARE_ERROR);
        // }
        Map<Long, ApplicationDTO> applicationMap = getApplicationDoMap(pageReqVO.getAppName());
        List<Long> applicationIds = new ArrayList<>(applicationMap.keySet());
        // 查询原始分页数据
        PageResult<CorpAppRelationDO> pageResult = corpAppRelationDataRepository.selectPage(pageReqVO, applicationIds);

        // 1. 获取应用ID列表
        List<Long> appIds = pageResult.getList().stream()
                .map(CorpAppRelationDO::getApplicationId)
                .collect(Collectors.toList());

        // 获取标签列表
        Map<Long, List<TagVO>> tagMap = appApplicationApi.queryAppTags(appIds);

        // 转换为 VO 对象并根据 applicationName 过滤
        List<CorpApplicationRespVO> filteredList = pageResult.getList().stream()
                .map(corpDO -> convertToRespVO(corpDO, applicationMap, tagMap))
                .collect(Collectors.toList());
        // 返回过滤后的结果和总数
        return new PageResult<>(filteredList, pageResult.getTotal());
    }


    /**
     * 获取app应用状态描述
     *
     * @param
     * @return Map<Long, String> key为企业ID，value为企业名称
     */
    public Integer getCorpStatus(Integer status, LocalDateTime expiresTime) {
        int showStatus = 0;
        if (status != null && status.equals(CorpStatusEnum.DISABLE.getValue())) {
            showStatus = CorpAppReationStatusEnum.DISABLE.getValue();
        } else if (expiresTime != null) {
            if (expiresTime.isAfter(java.time.LocalDateTime.now())) {
                showStatus = CorpAppReationStatusEnum.ENABLE.getValue();
            } else {
                showStatus = CorpAppReationStatusEnum.EXPIRES.getValue();
            }
        }
        return showStatus;
    }

    /**
     * 转换企业应用关联DO为响应VO对象
     *
     * @param corpDO         企业应用关联DO对象
     * @param applicationMap 应用信息映射表
     * @return CorpApplicationRespVO 响应VO对象
     */
    private CorpApplicationRespVO convertToRespVO(CorpAppRelationDO corpDO, Map<Long, ApplicationDTO> applicationMap, Map<Long, List<TagVO>> tagsMap) {
        CorpApplicationRespVO respVO = new CorpApplicationRespVO();
        ApplicationDTO appDo = applicationMap.get(corpDO.getApplicationId());
        if (appDo != null) {
            respVO = BeanUtils.toBean(appDo, CorpApplicationRespVO.class);
            respVO.setApplicationName(appDo.getAppName());
            respVO.setApplicationCode(appDo.getAppCode());
            respVO.setApplicationUid(appDo.getAppUid());
            respVO.setApplicationId(appDo.getId());
            respVO.setId(corpDO.getId());
            respVO.setVersionNumber(appDo.getVersionNumber());
            // 获取app应用状态描述
            Integer status = corpDO.getStatus();
            respVO.setShowStatus(getCorpStatus(status, corpDO.getExpiresTime()));
            respVO.setTags(tagsMap.get(appDo.getId()));
        }
        respVO.setAuthorizationTime(corpDO.getAuthorizationTime());
        respVO.setExpiresTime(corpDO.getExpiresTime());
        return respVO;
    }

    @Override
    public void deleteCorpAppRelationByCorpId(Long corpID) {
        corpAppRelationDataRepository.deleteCorpAppRelationByCorpId(corpID);
    }

    @Override
    public List<CorpAppRelationDO> getCorpAppRelationList(CorpAppRelationPageReqVO corpAppRelationPageReqVO) {
        List<CorpAppRelationDO> corpApplicationRespVOList = corpAppRelationDataRepository.getCorpAppRelationList(corpAppRelationPageReqVO);
        return corpApplicationRespVOList;
    }

    @Override
    public void updateStatus(Long id, Long status) {
        //  企业禁用/开启
        DataRow row = new DataRow();
        row.put(CorpDO.STATUS, status);
        corpAppRelationRepository.updateByConfig(row, new DefaultConfigStore().eq(CorpAppRelationDO.ID, id));
    }

    @Override
    public List<ApplicationDTO> getCorpNoRelationAppList(CorpRelationAppReqVO relationAppReqVO) {
        List<ApplicationDTO> applicationDTOList = appApplicationApi.findAppApplicationByAppName(relationAppReqVO.getAppName());
        if (null == relationAppReqVO.getCorpId()) {
            // 用于企业创建时拉取全部应用
            return applicationDTOList;
        }
        // 获取企业已关联的数据
        List<CorpAppRelationDO> corpAppRelationDOList = corpAppRelationDataRepository.findCorpAppRelationByCorpId(relationAppReqVO.getCorpId());
        if (corpAppRelationDOList.isEmpty()) {
            return applicationDTOList;
        }
        // 获取已关联的应用ID集合
        Set<Long> relatedAppIds = corpAppRelationDOList.stream()
                .map(CorpAppRelationDO::getApplicationId)
                .collect(Collectors.toSet());

        // 过滤掉已关联的应用
        List<ApplicationDTO> filteredList = applicationDTOList.stream()
                .filter(app -> !relatedAppIds.contains(app.getId()))
                .collect(Collectors.toList());
        return filteredList;
    }
}

