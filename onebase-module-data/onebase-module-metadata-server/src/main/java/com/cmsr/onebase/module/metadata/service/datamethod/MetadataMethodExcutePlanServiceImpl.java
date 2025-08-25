package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataMethodExcutePlanDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataMethodExcutePlanRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MetadataMethodExcutePlanServiceImpl implements MetadataMethodExcutePlanService {

    @Resource
    private MetadataMethodExcutePlanRepository repository;

    @Override
    public MetadataMethodExcutePlanDO getEnabledByMethodCode(String methodCode) {
        return repository.getEnabledByMethodCode(methodCode);
    }
}
