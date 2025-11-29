package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import com.cmsr.onebase.module.app.core.provider.resource.ComponentServiceProvider;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private AppComponentRepository appComponentDataRepository;

    @Resource
    private ComponentServiceProvider componentServiceProvider;

    @Override
    public Boolean deleteComponent(Long id) {
        appComponentDataRepository.removeById(id);
        return true;
    }

    @Override
    public List<ComponentDTO> listComponent(Long pageId) {
        return componentServiceProvider.listComponent(pageId);
    }

}
