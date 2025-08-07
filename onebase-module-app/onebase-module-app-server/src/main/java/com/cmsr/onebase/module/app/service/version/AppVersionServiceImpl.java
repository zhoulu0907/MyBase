package com.cmsr.onebase.module.app.service.version;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionListRespVO;
import com.cmsr.onebase.module.app.dal.database.app.*;
import com.cmsr.onebase.module.app.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionMenuRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.dal.database.version.AppVersionResourceRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.*;
import com.cmsr.onebase.module.app.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionDO;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionMenuDO;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionResourceDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.util.VersionUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Resource
    private AppVersionRepository versionRepository;

    @Resource
    private AppVersionMenuRepository versionMenuRepository;

    @Resource
    private AppVersionResourceRepository versionResourceRepository;

    @Resource
    private AppApplicationRepository applicationRepository;

    @Resource
    private AppMenuRepository menuRepository;

    @Resource
    private AppResourceRepository resourceRepository;

    @Resource
    private AppCommonService appCommonService;

    @Override
    public List<VersionListRespVO> listApplicationVersion(Long applicationId) {
        List<VersionDO> dos = versionRepository.findByApplicationId(applicationId);
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(dos);
        return dos.stream().map(v -> {
            VersionListRespVO vo = BeanUtils.toBean(v, VersionListRespVO.class);
            vo.setCreatorName(userHelper.getUserName(v.getCreator()));
            return vo;
        }).toList();
    }

    @Transactional
    @Override
    public void createApplicationVersion(VersionCreateReqVO createReqVO) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        //先备份老的相关数据
        //创建新版本
        final VersionDO applicationVersionDO = new VersionDO();
        applicationVersionDO.setApplicationId(applicationDO.getId());
        applicationVersionDO.setVersionName(createReqVO.getVersionName());
        applicationVersionDO.setVersionNumber(createReqVO.getVersionNumber());
        versionRepository.insert(applicationVersionDO);
        //备份菜单
        List<MenuDO> menuDOS = menuRepository.findByApplicationId(applicationDO.getId());
        List<VersionMenuDO> versionMenuDOS = BeanUtils.toBean(menuDOS, VersionMenuDO.class,
                versionMenuDO -> versionMenuDO.setVersionId(applicationVersionDO.getId())
        );
        versionMenuRepository.insertBatch(versionMenuDOS);
        //备份资源
        List<ResourceDO> resourceDOS = resourceRepository.findByApplicationId(applicationDO.getId());
        List<VersionResourceDO> versionResourceDOS = BeanUtils.toBean(resourceDOS, VersionResourceDO.class,
                versionResourceDO -> versionResourceDO.setVersionId(applicationVersionDO.getId())
        );
        versionResourceRepository.insertBatch(versionResourceDOS);
        //主表版本升级
        String newVersionNumber = VersionUtils.increaseVersionNumber(createReqVO.getVersionNumber());
        applicationDO.setVersionNumber(newVersionNumber);
        applicationRepository.update(applicationDO);
    }

    @Transactional
    @Override
    public void restoreApplicationVersion(Long versionId) {
        VersionDO applicationVersionDO = validateApplicationVersionExist(versionId);
        Long applicationId = applicationVersionDO.getApplicationId();
        //删除相关数据
        menuRepository.deleteByApplicationId(applicationId);
        resourceRepository.deleteByApplicationId(applicationId);
        //恢复菜单
        List<VersionMenuDO> versionMenuDOS = versionMenuRepository.findByApplicationIdAndVersionId(applicationId, versionId);
        List<MenuDO> menuDOS = BeanUtils.toBean(versionMenuDOS, MenuDO.class,
                menuDO -> menuDO.setId(null)
        );
        menuRepository.insertBatch(menuDOS);
        //恢复资源
        List<VersionResourceDO> versionResourceDOS = versionResourceRepository.findByApplicationIdAndVersionId(applicationId, versionId);
        List<ResourceDO> resourceDOS = BeanUtils.toBean(versionResourceDOS, ResourceDO.class,
                resourceDO -> resourceDO.setId(null)
        );
        resourceRepository.insertBatch(resourceDOS);
    }

    @Override
    public void deleteApplicationVersion(Long versionId) {
        versionRepository.deleteById(VersionDO.class, versionId);
        versionMenuRepository.deleteByVersionId(versionId);
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
