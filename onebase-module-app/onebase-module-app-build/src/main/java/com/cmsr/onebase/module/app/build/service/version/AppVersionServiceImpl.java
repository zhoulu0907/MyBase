package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetLabelRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionResourceRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetLabelDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.version.VersionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.version.VersionResourceDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.version.ResTypeEnum;
import com.cmsr.onebase.module.app.build.vo.version.VersionCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
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

    @Resource
    private AppPageSetLabelRepository pageSetLabelRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageRepository;

    @Resource
    private AppPageRepository pageRepository;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    public PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo) {
        PageResult<VersionDO> pageResult = versionRepository.selectPage(listReqVo.getApplicationId(), listReqVo);
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
        Long applicationId = applicationDO.getId();
        Long versionId = versionDO.getId();
        // 备份 Menu
        List<Long> menuIds = backupMenu(applicationId, versionId);
        // 备份 pageset
        List<Long> pageSetCodes = backupPageSet(applicationId, versionId, menuIds);
        // 备份 pageset label
        backupPageSetLabel(applicationId, versionId, pageSetCodes);
        // 备份 PageSet Page
        List<Long> pageCodes = backupPageSetPage(applicationId, versionId, pageSetCodes);
        // 备份 page
        backupPage(applicationId, versionId, pageCodes);
        // 备份 page ref router
    }

    private List<Long> backupMenu(Long applicationId, Long versionId) {
        List<MenuDO> menuDOS = menuRepository.findByApplicationId(applicationId);
        VersionResourceDO versionResourceDO = new VersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.MENU.getValue());
        versionResourceDO.setResData(JsonUtils.toJsonString(menuDOS));
        versionResourceRepository.insert(versionResourceDO);
        return menuDOS.stream().map(menuDO -> menuDO.getId()).toList();
    }

    private List<Long> backupPageSet(Long applicationId, Long versionId, List<Long> menuIds) {
        List<PageSetDO> pageSetDOS = pageSetRepository.findByMenuIds(menuIds);
        VersionResourceDO versionResourceDO = new VersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.PAGE_SET.getValue());
        versionResourceDO.setResData(JsonUtils.toJsonString(pageSetDOS));
        versionResourceRepository.insert(versionResourceDO);
        return pageSetDOS.stream().map(v -> v.getId()).toList();
    }

    private void backupPageSetLabel(Long applicationId, Long versionId, List<Long> pageSetIds) {
        List<PageSetLabelDO> pageSetLabelDOS = pageSetLabelRepository.findByPageSetIds(pageSetIds);
        VersionResourceDO versionResourceDO = new VersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.PAGE_SET_LABEL.getValue());
        versionResourceDO.setResData(JsonUtils.toJsonString(pageSetLabelDOS));
        versionResourceRepository.insert(versionResourceDO);
    }

    private List<Long> backupPageSetPage(Long applicationId, Long versionId, List<Long> pageSetIds) {
        List<PageSetPageDO> pageSetPageDOs = pageSetPageRepository.findByPageSetIds(pageSetIds);
        VersionResourceDO versionResourceDO = new VersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.PAGE_SET_PAGE.getValue());
        versionResourceDO.setResData(JsonUtils.toJsonString(pageSetPageDOs));
        versionResourceRepository.insert(versionResourceDO);
        return pageSetPageDOs.stream().map(v -> v.getId()).toList();
    }

    private List<Long> backupPage(Long applicationId, Long versionId, List<Long> pageIds) {
        List<PageDO> pageDOs = pageRepository.findByPageIds(pageIds);
        VersionResourceDO versionResourceDO = new VersionResourceDO();
        versionResourceDO.setApplicationId(applicationId);
        versionResourceDO.setVersionId(versionId);
        versionResourceDO.setResType(ResTypeEnum.PAGE.name());
        versionResourceDO.setResData(JsonUtils.toJsonString(pageDOs));
        versionResourceRepository.insert(versionResourceDO);
        return pageDOs.stream().map(v -> v.getId()).toList();
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
        VersionDO applicationVersionDO = validateApplicationVersionExist(versionId);
        Long applicationId = applicationVersionDO.getApplicationId();
        // 更新到主表
        ApplicationDO applicationDO = applicationRepository.findById(applicationId);
        applicationDO.setVersionNumber(applicationVersionDO.getVersionNumber());
        applicationRepository.update(applicationDO);
        // 恢复菜单
        restoreMenu(applicationDO.getId(), versionId);
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
            // TODO clean方法待完善
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

    @Override
    public Map<Long, VersionDO> findVersionMapByAppIds(List<Long> appIds) {
        List<VersionDO>  allVersions= versionRepository.findVersionList(appIds);
        Map<Long, VersionDO> latestVersionMap = allVersions.stream()
                .sorted(Comparator.comparing(VersionDO::getUpdateTime).reversed())
                .collect(Collectors.toMap(
                        VersionDO::getApplicationId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
        return latestVersionMap;
    }

    private VersionDO validateApplicationVersionExist(Long id) {
        VersionDO versionDO = versionRepository.findById(id);
        if (versionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        return versionDO;
    }

}
