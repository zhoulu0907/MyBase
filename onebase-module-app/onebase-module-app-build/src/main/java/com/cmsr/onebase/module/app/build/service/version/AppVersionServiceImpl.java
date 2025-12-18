package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.app.AppPublishEnum;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
import com.cmsr.onebase.module.app.core.enums.version.VersionTypeEnum;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.flow.api.FlowDataManager;
import com.cmsr.onebase.module.metadata.api.version.MetadataDataManagerApi;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/24 11:04
 */
@Setter
@Service
@Validated
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppApplicationRepository applicationRepository;

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

    @Autowired
    private MetadataDataManagerApi metadataVersionManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo) {
        PageResult<AppVersionDO> pageResult = versionRepository.selectPage(listReqVo.getApplicationId(), listReqVo);
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());
        List<VersionPageRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    VersionPageRespVO bean = BeanUtils.toBean(v, VersionPageRespVO.class);
                    bean.setUpdaterName(userHelper.getUserNickname(v.getUpdater()));
                    bean.setVersionTypeLabel(VersionTypeEnum.getLabel(v.getVersionType()));
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    @Override
    public void onlineApplication(VersionOnlineReq createReqVO) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        Long applicationId = applicationDO.getId();
        validateVersionUnique(applicationId, createReqVO.getVersionNumber(), createReqVO.getVersionName());
        //
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // 找打当前Runtime版本信息，肯定能找到，因为发布的时候会同步创建一个，把当前版本信息变成历史状态
            AppVersionDO currentRunVersion = versionRepository.findByApplicationIdAndVersionType(applicationId, VersionTypeEnum.RUNTIME.getValue());
            if (currentRunVersion != null) {
                currentRunVersion.setVersionType(VersionTypeEnum.HISTORY.getValue());
                versionRepository.updateById(currentRunVersion);
                Long historyVersionTag = currentRunVersion.getId();
                // 备份当前版本为历史版本
                metadataVersionManager.moveMetaDataRuntimeToHistory(applicationId, historyVersionTag);
                appDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                bpmDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                flowDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
            }
            // 发布上线版本
            metadataVersionManager.copyMetaDataEditToRuntime(applicationId);
            appDataManager.copyEditToRuntime(applicationId);
            bpmDataManager.copyEditToRuntime(applicationId);
            flowDataManager.copyEditToRuntime(applicationId);
            // 创建新的版本信息
            AppVersionDO newRunVersionDO = createNewVersion(createReqVO, applicationId);
            applicationRepository.updateStatusByApplicationId(applicationId, AppStatusEnum.ONLINE, AppPublishEnum.ONCE_PUBLISHED);
            versionRepository.save(newRunVersionDO);
        });
        // online services that required
        flowDataManager.updateRuntimeData(applicationId);
    }

    private void validateVersionUnique(Long applicationId, String versionNumber, String versionName) {
        long count = versionRepository.countByApplicationIdAndName(applicationId, versionNumber, versionName);
        if (count > 0) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.VERSION_DUPLICATE);
        }
    }

    @Override
    public void offlineApplication() {
        Long applicationId = ApplicationManager.getRequiredApplicationId();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            AppVersionDO currentRunVersion = versionRepository.findByApplicationIdAndVersionType(applicationId, VersionTypeEnum.RUNTIME.getValue());
            if (currentRunVersion != null) {
                currentRunVersion.setVersionType(VersionTypeEnum.HISTORY.getValue());
                versionRepository.updateById(currentRunVersion);
                Long historyVersionTag = currentRunVersion.getId();
                // 备份当前版本为历史版本
                metadataVersionManager.moveMetaDataRuntimeToHistory(applicationId, historyVersionTag);
                appDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                bpmDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
                flowDataManager.moveRuntimeToHistory(applicationId, historyVersionTag);
            }
            applicationRepository.updateAppStatusByApplicationId(applicationId, AppStatusEnum.OFFLINE);
        });
        flowDataManager.deleteRuntimeData(applicationId);
    }

    private AppVersionDO createNewVersion(VersionOnlineReq createReqVO, Long applicationId) {
        AppVersionDO newRunVersionDO = new AppVersionDO();
        newRunVersionDO.setApplicationId(applicationId);
        newRunVersionDO.setVersionName(createReqVO.getVersionName());
        newRunVersionDO.setVersionNumber(createReqVO.getVersionNumber());
        newRunVersionDO.setVersionDescription(createReqVO.getVersionDescription());
        newRunVersionDO.setOperationType(createReqVO.getOperationType());
        newRunVersionDO.setEnvironment(createReqVO.getEnvironment());
        newRunVersionDO.setVersionType(VersionTypeEnum.RUNTIME.getValue());
        return newRunVersionDO;
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
        // 获取历史版本对象
    }

    @Override
    public void deleteApplicationVersion(Long versionId) {
        AppVersionDO versionDO = versionRepository.getById(versionId);
        if (versionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        if (versionDO.getVersionType() == VersionTypeEnum.BUILD.getValue()) {
            throw new IllegalArgumentException("不允许删除当前运行的版本");
        }
        if (versionDO.getVersionType() == VersionTypeEnum.RUNTIME.getValue()) {
            throw new IllegalArgumentException("不允许删除当前运行的版本");
        }
        Long applicationId = versionDO.getApplicationId();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // 删除对应的信息
            metadataVersionManager.deleteApplicationVersionData(applicationId, versionId);
            bpmDataManager.removeApplicationVersion(applicationId, versionId);
            appDataManager.deleteApplicationVersionData(applicationId, versionId);
            flowDataManager.deleteApplicationVersionData(applicationId, versionId);
            // 删除版本
            versionRepository.removeById(versionId);
        });
    }


}
