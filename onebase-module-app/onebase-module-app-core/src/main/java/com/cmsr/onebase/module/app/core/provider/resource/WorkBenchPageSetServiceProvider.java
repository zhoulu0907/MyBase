package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppWorkbenchComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppWorkbenchPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import com.cmsr.onebase.module.app.core.dto.resource.PageDTO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Service
public class WorkBenchPageSetServiceProvider {

    @Resource
    private AppWorkbenchPageRepository appWorkbenchPageRepository;

    @Resource
    private AppWorkbenchComponentRepository appWorkbenchComponentRepository;

    public LoadPageSetRespVO loadWorkbenchPageSet(AppResourcePagesetDO pageSetDO) {

        List<AppResourceWorkbenchPageDO> pageDOs;

        // 兼容旧数据：如果页面集-页面关联表中没有数据，直接通过pageSetId查询工作台页面
        pageDOs = appWorkbenchPageRepository.findByPageSetUuid(pageSetDO.getApplicationId(), pageSetDO.getPageSetUuid());

        LoadPageSetRespVO loadPageSetRespVO = new LoadPageSetRespVO();
        loadPageSetRespVO.setId(pageSetDO.getId());
        loadPageSetRespVO.setPageSetType(pageSetDO.getPageSetType());
        List<PageDTO> pageDTOs = new ArrayList<>();

        // 读取每个页面的组件和配置
        pageDOs.forEach(pageDO -> {
            List<AppResourceWorkbenchComponentDO> componentDOs = appWorkbenchComponentRepository.findByPageUuid(pageDO.getApplicationId(), pageDO.getPageUuid());

            PageDTO pageDTO = BeanUtils.toBean(pageDO, PageDTO.class);
            pageDTO.setComponents(componentDOs.stream()
                    .map(componentDO -> BeanUtils.toBean(componentDO, ComponentDTO.class))
                    .toList());
            pageDTOs.add(pageDTO);
        });

        loadPageSetRespVO.setPages(pageDTOs);
        loadPageSetRespVO.setMainMetadata(pageSetDO.getMainMetadata());

        return loadPageSetRespVO;
    }


}
