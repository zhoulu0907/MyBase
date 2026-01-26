package com.cmsr.onebase.module.app.build.service.api;

import com.cmsr.onebase.module.app.api.app.ApplicationApi;
import com.cmsr.onebase.module.app.build.service.app.AppApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationApiImpl implements ApplicationApi {

    @Autowired
    private AppApplicationService appApplicationService;

    @Override
    public void deleteApplication(Long id, String name) {
        appApplicationService.deleteApplication(id, name);
    }
}