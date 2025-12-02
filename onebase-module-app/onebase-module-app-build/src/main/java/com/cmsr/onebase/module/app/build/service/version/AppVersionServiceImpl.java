package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.vo.version.VersionCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionResourceRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.*;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.version.ResTypeEnum;
import jakarta.annotation.Resource;
import lombok.Setter;
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
    private AppPageSetRepository pageSetRepository;

//    @Resource
//    private AppPageSetPageRepository pageSetPageRepository;

    @Resource
    private AppPageRepository pageRepository;

    @Resource
    private UidGenerator uidGenerator;

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
        final AppVersionDO versionDO = new AppVersionDO();
        versionDO.setApplicationId(applicationDO.getId());
        versionDO.setVersionName(createReqVO.getVersionName());
        versionDO.setVersionNumber(createReqVO.getVersionNumber());
        versionDO.setVersionDescription(createReqVO.getVersionDescription());
        versionDO.setVersionURL(UUID.randomUUID().toString().replace("-", ""));
        versionDO.setOperationType(createReqVO.getOperationType());
        versionDO.setEnvironment(createReqVO.getEnvironment());
        versionRepository.save(versionDO);
        Long applicationId = applicationDO.getId();
        Long versionId = versionDO.getId();
        // 备份 Menu
        List<Long> menuIds = backupMenu(applicationId, versionId);
        // 备份 pageset
        List<Long> pageSetCodes = backupPageSet(applicationId, versionId, menuIds);
        // 备份 PageSet Page
        List<Long> pageCodes = backupPageSetPage(applicationId, versionId, pageSetCodes);
        // 备份 page
        backupPage(applicationId, versionId, pageCodes);
        // 备份 page ref router
    }

    private List<Long> backupMenu(Long applicationId, Long versionId) {
        List<AppMenuDO> menuDOS = menuRepository.findByApplicationId(applicationId);
        AppVersionResourceDO versionResourceDO = new AppVersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.MENU.getValue());
        versionResourceDO.setResData(JsonUtils.toJsonString(menuDOS));
        versionResourceRepository.save(versionResourceDO);
        return menuDOS.stream().map(BaseEntity::getId).toList();
    }

    private List<Long> backupPageSet(Long applicationId, Long versionId, List<Long> menuIds) {
//        List<AppResourcePagesetDO> pageSetDOS = pageSetRepository.findByMenuIds(menuIds);
//        AppVersionResourceDO versionResourceDO = new AppVersionResourceDO();
//        versionResourceDO.setApplicationId(applicationId);
//        versionResourceDO.setVersionId(versionId);
//        versionResourceDO.setResType(ResTypeEnum.PAGE_SET.getValue());
//        versionResourceDO.setResData(JsonUtils.toJsonString(pageSetDOS));
//        versionResourceRepository.save(versionResourceDO);
//        return pageSetDOS.stream().map(BaseEntity::getId).toList();
        return null;
    }

    private List<Long> backupPageSetPage(Long applicationId, Long versionId, List<Long> pageSetIds) {
        return null;
        //            List<AppResourcePagesetPageDO> pageSetPageDOs = pageSetPageRepository.findByPageSetIds(pageSetIds);
//            AppVersionResourceDO versionResourceDO = new AppVersionResourceDO();
//            versionResourceDO.setApplicationId(applicationId);
//            versionResourceDO.setVersionId(versionId);
//            versionResourceDO.setResType(ResTypeEnum.PAGE_SET_PAGE.getValue());
//            versionResourceDO.setResData(JsonUtils.toJsonString(pageSetPageDOs));
//            versionResourceRepository.save(versionResourceDO);
//            return pageSetPageDOs.stream().map(BaseEntity::getId).toList();
    }

    private List<Long> backupPage(Long applicationId, Long versionId, List<Long> pageIds) {
        List<AppResourcePageDO> pageDOs = pageRepository.listByIds(pageIds);
        AppVersionResourceDO versionResourceDO = new AppVersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.PAGE.name());
        versionResourceDO.setResData(JsonUtils.toJsonString(pageDOs));
        versionResourceRepository.save(versionResourceDO);
        return pageDOs.stream().map(BaseEntity::getId).toList();
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
//        AppVersionDO applicationVersionDO = validateApplicationVersionExist(versionId);
//        Long applicationId = applicationVersionDO.getApplicationId();
//        // 更新到主表
//        AppApplicationDO applicationDO = applicationRepository.getById(applicationId);
//        applicationDO.setVersionNumber(applicationVersionDO.getVersionNumber());
//        applicationRepository.updateById(applicationDO);
//        // 恢复菜单
//        restoreMenu(applicationDO.getId(), versionId);
    }

    private void restoreMenu(Long applicationId, Long versionId) {
        // 删除相关数据
        menuRepository.deleteByApplicationId(applicationId);
        // 恢复菜单
        AppVersionResourceDO resourceDOS = versionResourceRepository
                .findByApplicationIdAndVersionIdAndResType(applicationId, versionId, ResTypeEnum.MENU.getValue());
        List<AppMenuDO> menuDOS = JsonUtils.parseArray(resourceDOS.getResData(), AppMenuDO.class);
        prepareForBackup(menuDOS);
        menuRepository.saveBatch(menuDOS);
    }

    private void prepareForBackup(List<? extends BaseEntity> list) {
        list.forEach(v -> {
            // TODO clean方法待完善
            v.setId(null);
            v.setUpdater(null);
            v.setUpdateTime(null);
            v.setCreator(null);
            v.setCreateTime(null);
            v.setDeleted(null);
            //v.setLockVersion(null);
        });
    }

    @Override
    public void deleteApplicationVersion(Long versionId) {
        versionRepository.removeById(versionId);
        versionResourceRepository.deleteByVersionId(versionId);
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
