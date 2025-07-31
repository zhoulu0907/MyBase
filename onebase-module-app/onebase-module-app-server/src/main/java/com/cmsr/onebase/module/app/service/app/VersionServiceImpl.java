package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationVersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationVersionListRespVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.*;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.util.VersionUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
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
public class  VersionServiceImpl implements VersionService {

    @Resource
    private DataRepository dataRepository;

    @Resource
    private AppCommonService appCommonService;

    @Override
    public List<ApplicationVersionListRespVO> listApplicationVersion(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        List<VersionDO> dos = dataRepository.findAll(VersionDO.class, configs);
        return dos.stream().map(v -> {
            ApplicationVersionListRespVO vo = BeanUtils.toBean(v, ApplicationVersionListRespVO.class);
            vo.setCreatorName(appCommonService.getUserName(v.getCreator()));
            return vo;
        }).toList();
    }

    @Transactional
    @Override
    public void createApplicationVersion(ApplicationVersionCreateReqVO createReqVO) {
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        //先备份老的相关数据
        //创建新版本
        final VersionDO applicationVersionDO = new VersionDO();
        applicationVersionDO.setApplicationId(applicationDO.getId());
        applicationVersionDO.setVersionName(createReqVO.getVersionName());
        applicationVersionDO.setVersionNumber(createReqVO.getVersionNumber());
        dataRepository.insert(applicationVersionDO);
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationDO.getId());
        //备份菜单
        List<ApplicationMenuDO> menuDOS = dataRepository.findAll(ApplicationMenuDO.class, configs);
        List<VersionMenuDO> versionMenuDOS = menuDOS.stream().map(v -> {
            VersionMenuDO versionMenuDO = BeanUtils.toBean(v, VersionMenuDO.class);
            versionMenuDO.setVersionId(applicationVersionDO.getId());
            return versionMenuDO;
        }).toList();
        dataRepository.insertBatch(versionMenuDOS);
        //备份资源
        List<ApplicationResourceDO> resourceDOS = dataRepository.findAll(ApplicationResourceDO.class, configs);
        List<VersionResourceDO> versionResourceDOS = resourceDOS.stream().map(v -> {
            VersionResourceDO versionResourceDO = BeanUtils.toBean(v, VersionResourceDO.class);
            versionResourceDO.setVersionId(applicationVersionDO.getId());
            return versionResourceDO;
        }).toList();
        dataRepository.insertBatch(versionResourceDOS);
        //主表版本升级
        String newVersionNumber = VersionUtils.increaseVersionNumber(createReqVO.getVersionNumber());
        applicationDO.setVersionNumber(newVersionNumber);
        dataRepository.update(applicationDO);
    }

    @Override
    public void restoreApplicationVersion(Long versionId) {
        VersionDO applicationVersionDO = validateApplicationVersionExist(versionId);
        //删除主表数据
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationVersionDO.getApplicationId());
        dataRepository.deleteByConfig(ApplicationMenuDO.class, configs);
        dataRepository.deleteByConfig(ApplicationResourceDO.class, configs);
        //查询版本数据
        configs = new DefaultConfigStore();
        configs.eq("version_id", versionId);
        //恢复菜单
        List<VersionMenuDO> versionMenuDOS = dataRepository.findAll(VersionMenuDO.class, configs);
        List<ApplicationMenuDO> menuDOS = versionMenuDOS.stream().map(v -> {
            ApplicationMenuDO menuDO = BeanUtils.toBean(v, ApplicationMenuDO.class);
            menuDO.setId(null);
            return menuDO;
        }).toList();
        dataRepository.insertBatch(menuDOS);
        //恢复资源
        List<VersionResourceDO> versionResourceDOS = dataRepository.findAll(VersionResourceDO.class, configs);
        List<ApplicationResourceDO> resourceDOS = versionResourceDOS.stream().map(v -> {
            ApplicationResourceDO resourceDO = BeanUtils.toBean(v, ApplicationResourceDO.class);
            resourceDO.setId(null);
            return resourceDO;
        }).toList();
        dataRepository.insertBatch(resourceDOS);
    }

    @Override
    public void deleteApplicationVersion(Long versionId) {
        dataRepository.deleteById(VersionDO.class, versionId);
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("version_id", versionId);
        dataRepository.deleteByConfig(VersionMenuDO.class, configs);
        dataRepository.deleteByConfig(VersionResourceDO.class, configs);
    }

    private VersionDO validateApplicationVersionExist(Long id) {
        VersionDO applicationVersionDO = dataRepository.findById(VersionDO.class, id);
        if (applicationVersionDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_VERSION_NOT_EXIST);
        }
        return applicationVersionDO;
    }


}
