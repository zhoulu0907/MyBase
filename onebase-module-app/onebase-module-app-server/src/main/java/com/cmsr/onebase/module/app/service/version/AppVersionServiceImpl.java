package com.cmsr.onebase.module.app.service.version;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionPageReqVo;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionPageRespVO;
import com.cmsr.onebase.module.app.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionResourceRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionDO;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionResourceDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.version.ResTypeEnum;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.service.app.AppApplicationService;
import com.cmsr.onebase.module.app.util.VersionUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/7/24 11:04
 */
@Setter
@Service
@Validated
public class AppVersionServiceImpl implements AppVersionService {

    @Resource
    private AppVersionRepository versionRepository;

    @Resource
    private AppVersionResourceRepository versionResourceRepository;

    @Resource
    private AppApplicationRepository applicationRepository;

    @Resource
    private AppMenuRepository menuRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private AppApplicationService appApplicationService;

    @Override
    public PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo) {
        PageResult<VersionDO> pageResult = versionRepository.selectPage(listReqVo);
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(pageResult.getList());
        List<VersionPageRespVO> respVOS = pageResult.getList().stream()
                .map(v -> {
                    VersionPageRespVO bean = BeanUtils.toBean(v, VersionPageRespVO.class);
                    bean.setUpdaterName(userHelper.getUserName(v.getUpdater()));
                    return bean;
                })
                .toList();
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    @Transactional
    @Override
    public void createApplicationVersion(VersionCreateReqVO createReqVO) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        // 先备份老的相关数据
        // 创建新版本
        final VersionDO versionDO = new VersionDO();
        versionDO.setApplicationId(applicationDO.getId());
        versionDO.setVersionName(createReqVO.getVersionName());
        versionDO.setVersionNumber(createReqVO.getVersionNumber());
        versionDO.setVersionDescription(createReqVO.getVersionDescription());
        versionDO.setVersionURL(UUID.randomUUID().toString().replace("-", ""));
        versionDO.setOperationType(createReqVO.getOperationType());
        versionDO.setEnvironment(createReqVO.getEnvironment());
        versionRepository.insert(versionDO);
        // 备份 Menu
        backupMenu(applicationDO.getId(), versionDO.getId());
        // 备份 pageset


    }

    private void backupMenu(Long applicationId, Long versionId) {
        List<MenuDO> menuDOS = menuRepository.findByApplicationId(applicationId);
        VersionResourceDO versionResourceDO = new VersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.MENU.getValue());
        versionResourceDO.setResData(JsonUtils.toJsonString(menuDOS));
        versionResourceRepository.insert(versionResourceDO);
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
        VersionDO applicationVersionDO = validateApplicationVersionExist(versionId);
        Long applicationId = applicationVersionDO.getApplicationId();

        // 更新到主表
        ApplicationDO applicationDO = applicationRepository.findById(applicationId);
        applicationDO.setVersionURL(applicationVersionDO.getVersionURL());
        applicationDO.setVersionNumber(applicationVersionDO.getVersionNumber());
        applicationRepository.update(applicationDO);
        // 恢复菜单
        restoreMenu(applicationId, versionId);
    }

    private void restoreMenu(Long applicationId, Long versionId) {
        // 删除相关数据
        menuRepository.deleteByApplicationId(applicationId);
        // 恢复菜单
        VersionResourceDO resourceDOS = versionResourceRepository
                .findByApplicationIdAndVersionIdAndResType(applicationId, versionId, ResTypeEnum.MENU.getValue());
        List<MenuDO> menuDOS = JsonUtils.parseArray(resourceDOS.getResData(), MenuDO.class);
        prepareForBackup(menuDOS);
        menuRepository.insertBatch(menuDOS);
    }

    private void prepareForBackup(List<? extends BaseDO> list) {
        list.forEach(v -> {
            //TODO clean方法待完善
            v.clean();
            v.setId(null);
            v.setUpdater(null);
            v.setUpdateTime(null);
            v.setCreator(null);
            v.setCreateTime(null);
            v.setDeleted(null);
            v.setLockVersion(null);
        });
    }

    @Override
    public void deleteApplicationVersion(Long versionId) {
        versionRepository.deleteById(versionId);
        versionResourceRepository.deleteByVersionId(versionId);
    }

    private VersionDO validateApplicationVersionExist(Long id) {
        VersionDO versionDO = versionRepository.findById(id);
        if (versionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        return versionDO;
    }

}
