package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.vo.version.VersionCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/7/24 11:04
 */
@Setter
@Service
@Validated
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppVersionRepository versionRepository;

    @Autowired
    private AppCommonService appCommonService;

    @Autowired
    private AppDataManager appDataManager;

    @Override
    public PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo) {
        PageResult<AppVersionDO> pageResult = versionRepository.selectPage(listReqVo.getApplicationId(), listReqVo);
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());
        List<VersionPageRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    VersionPageRespVO bean = BeanUtils.toBean(v, VersionPageRespVO.class);
                    bean.setUpdaterName(userHelper.getUserNickname(v.getUpdater()));
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    @Transactional
    @Override
    public void createApplicationVersion(VersionCreateReqVO createReqVO) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        // 先备份老的相关数据
        // 创建新版本
        final Long applicationId = applicationDO.getId();
        final Long runtimeVersionId = applicationId;
        final AppVersionDO versionDO = new AppVersionDO();
        versionDO.setId(runtimeVersionId);
        versionDO.setApplicationId(applicationId);
        versionDO.setVersionName(createReqVO.getVersionName());
        versionDO.setVersionNumber(createReqVO.getVersionNumber());
        versionDO.setVersionDescription(createReqVO.getVersionDescription());
        versionDO.setVersionURL(UUID.randomUUID().toString().replace("-", ""));
        versionDO.setOperationType(createReqVO.getOperationType());
        versionDO.setEnvironment(createReqVO.getEnvironment());

        this.saveVersion(applicationId, versionDO);
        // 因为第一次不存在versionTag=1，所以可以直接向下执行，不需要多做判断
        // 1. move runtime to history
        appDataManager.moveRuntimeToHistory(applicationId, runtimeVersionId);
        // 2. copy edit to runtime
        appDataManager.copyEditToRuntime(applicationId);
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
        AppVersionDO targetVersionDO = versionRepository.getById(versionId);
        if (targetVersionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        final Long applicationId = targetVersionDO.getApplicationId();
        final Long runtimeVersionId = applicationId;
        if (versionId == runtimeVersionId) {
            throw new IllegalArgumentException("当前已是最新版本");
        }
        saveVersion(applicationId, targetVersionDO);
        // 1. backup runtime
        appDataManager.moveRuntimeToHistory(applicationId, runtimeVersionId);
        // 2. publish history
        appDataManager.copyHistoryToRuntime(applicationId, versionId);
    }

    private void saveVersion(Long applicationId, AppVersionDO newVersion) {
        // 0. 查询是否存在老版本
        final AppVersionDO currentVersion = versionRepository.findCurrentVersion(applicationId);
        // 1.
        if (currentVersion == null) {
            // 1.1. 不存在老版本
            // 直接保存版本数据
            versionRepository.save(newVersion);
        } else {
            // 1.2. 存在老版本
            // 1.2.1. 将老版本另存为
            currentVersion.setId(null);
            versionRepository.save(currentVersion);
            // 1.2.2. 将新版本保存
            versionRepository.updateById(newVersion);
        }
    }

    @Override
    public void deleteApplicationVersion(Long versionId) {
        versionRepository.removeById(versionId);
    }

    @Override
    public Map<Long, AppVersionDO> findVersionMapByAppIds(List<Long> appIds) {
        List<AppVersionDO> allVersions = versionRepository.findVersionList(appIds);
        Map<Long, AppVersionDO> latestVersionMap = allVersions.stream()
                .sorted(Comparator.comparing(AppVersionDO::getUpdateTime).reversed())
                .collect(Collectors.toMap(
                        AppVersionDO::getApplicationId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
        return latestVersionMap;
    }

    private AppVersionDO validateApplicationVersionExist(Long id) {
        AppVersionDO versionDO = versionRepository.getById(id);
        if (versionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        return versionDO;
    }

}
