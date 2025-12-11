package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.vo.version.VersionCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
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
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    @Override
    public void createApplicationVersion(VersionCreateReqVO createReqVO) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        Long applicationId = applicationDO.getId();
        // 删除当前运行版本数据
        flowDataManager.deleteRuntimeData(applicationId);
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
            versionRepository.save(newRunVersionDO);
        });
        // online services that required
        flowDataManager.onlineRuntimeData(applicationId);
    }

    private AppVersionDO createNewVersion(VersionCreateReqVO createReqVO, Long applicationId) {
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
        versionRepository.removeById(versionId);
    }


}
