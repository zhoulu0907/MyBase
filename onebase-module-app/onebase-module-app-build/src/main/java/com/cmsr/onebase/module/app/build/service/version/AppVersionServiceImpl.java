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
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.flow.api.FlowDataManager;
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

    @Autowired
    private BpmDataManager bpmDataManager;

    @Autowired
    private FlowDataManager flowDataManager;

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
        Long applicationId = applicationDO.getId();
        Long runtimeVersionId = applicationId;

        AppVersionDO currentVersion = versionRepository.findCurrentVersion(runtimeVersionId);
        if (currentVersion != null) {
            // 因为第一次不存在versionTag=1，所以可以直接向下执行，不需要多做判断
            // 1. move runtime to history
            currentVersion.setId(null);
            versionRepository.save(currentVersion);
            Long historyVersionId = currentVersion.getId();
            appDataManager.moveRuntimeToHistory(applicationId, historyVersionId);
            bpmDataManager.moveRuntimeToHistory(applicationId, historyVersionId);
            flowDataManager.moveRuntimeToHistory(applicationId, historyVersionId);
        }
        // 2. copy edit to runtime
        appDataManager.copyEditToRuntime(applicationId);
        bpmDataManager.copyEditToRuntime(applicationId);
        flowDataManager.copyEditToRuntime(applicationId);
        // 3. online services that required
        flowDataManager.onlineRuntimeData(applicationId);
        // 4. update runtime version
        AppVersionDO versionDO = new AppVersionDO();
        versionDO.setId(runtimeVersionId);
        versionDO.setApplicationId(applicationId);
        versionDO.setVersionName(createReqVO.getVersionName());
        versionDO.setVersionNumber(createReqVO.getVersionNumber());
        versionDO.setVersionDescription(createReqVO.getVersionDescription());
        versionDO.setVersionURL(UUID.randomUUID().toString().replace("-", ""));
        versionDO.setOperationType(createReqVO.getOperationType());
        versionDO.setEnvironment(createReqVO.getEnvironment());
        versionRepository.saveOrUpdate(versionDO);
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
        // 获取历史版本对象
        AppVersionDO targetVersionDO = versionRepository.getById(versionId);
        if (targetVersionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        final Long applicationId = targetVersionDO.getApplicationId();
        final Long runtimeVersionId = applicationId;
        if (versionId == runtimeVersionId) {
            throw new IllegalArgumentException("当前已是最新版本");
        }
        // 备份当前版本为历史版本
        AppVersionDO currentVersion = versionRepository.findCurrentVersion(applicationId);
        if (currentVersion == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        currentVersion.setId(null);
        versionRepository.save(currentVersion);
        Long historyVersionTag = currentVersion.getId();
        // 1. backup runtime
        appDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
//        bpmDataManager.moveRuntimeToHistory(applicationId, runtimeVersionId);
        // 2. publish history
        appDataManager.copyHistoryToRuntime(applicationId, versionId);
        targetVersionDO.setId(runtimeVersionId);
        versionRepository.updateById(targetVersionDO);
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
