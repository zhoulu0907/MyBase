package com.cmsr.onebase.module.app.core.impl.app;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;
import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.core.dal.dataobject.tag.TagDO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:36
 */
@Setter
@Service
public class AppApplicationApiImpl implements AppApplicationApi {

    @Resource
    private AppApplicationRepository appApplicationRepository;

    @Resource
    private AppSqlQueryRepository appSqlQueryRepository;


    @Resource
    private AppTagRepository tagRepository;


    @Resource
    private AppApplicationTagRepository applicationTagRepository;

    @Override
    public Long countApplicationByTenantId(Long tenantId) {
        Long count = appApplicationRepository.countByTenantId(tenantId);
        return count;
    }

    @Override
    public List<ApplicationDTO> findAppApplicationByAppName(String appName) {
        List<ApplicationDO> applicationList = appApplicationRepository.findAppApplicationByAppName(appName);
        return applicationList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO>  findAppApplicationByAppIds(Collection<Long> appIds) {
        List<ApplicationDO> applicationList = appApplicationRepository.findAppApplicationByAppIds(appIds);
        return applicationList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ApplicationDTO convertToDTO(ApplicationDO applicationDO) {
        return BeanUtils.toBean(applicationDO, ApplicationDTO.class);
    }

    @Override
    @TenantIgnore
    public Map<Integer, Integer> findAppApplicationAll() {
        List<ApplicationDO> allApplications = appApplicationRepository.finAppApplicationAll();
        return allApplications.stream()
                .collect(Collectors.groupingBy(
                        app -> app.getTenantId().intValue(),  // Long转Integer
                        Collectors.summingInt(app -> 1)       // Integer计数替代Long计数
                ));
    }
    @Override
    public void updateAppTimeById(Long appId) {
        // 更新修改日期 没有别的字段更新，不写不生效
        DataRow row = new DataRow();
        row.put(BaseDO.UPDATE_TIME,  LocalDateTime.now());
        appApplicationRepository.updateByConfig(row, new DefaultConfigStore().eq(ApplicationDO.ID, appId));
    }

    @Override
    public Map<Long, List<TagVO>> queryAppTags(List<Long> appIds) {
        Map<Long, List<TagVO>> tagListMap =new HashMap<>();
        for (int i = 0; i <appIds.size() ; i++) {
            Long appId=appIds.get(i);
            List<Long> tagIds = applicationTagRepository.findTagIdsByApplicationId(appId);
            List<TagDO> tagDOList =tagRepository.findAllByIds(tagIds) ;
            List<TagVO> tagVOList = tagDOList.stream()
                    .map(tagDO -> BeanUtils.toBean(tagDO, TagVO.class))
                    .collect(Collectors.toList());
            tagListMap.put(appId,tagVOList);
        }
        return tagListMap;
    }

}
