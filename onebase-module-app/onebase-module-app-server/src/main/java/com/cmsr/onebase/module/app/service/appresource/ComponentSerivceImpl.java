package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.dal.database.appresource.AppComponentRepository;

import jakarta.annotation.Resource;

@Service
public class ComponentSerivceImpl implements ComponentSerivce {

    @Resource
    private AppComponentRepository appComponentDataRepository;

    @Override
    public Boolean deleteComponent(Long id) {
        appComponentDataRepository.deleteById(id);
        return true;
    }

}
