package com.cmsr.traffic.starter;

import com.cmsr.traffic.dao.mapper.CoreApiTrafficMapper;
import com.cmsr.utils.LogUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DeTrafficStarter implements ApplicationRunner {

    @Resource
    private CoreApiTrafficMapper coreApiTrafficMapper;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            coreApiTrafficMapper.cleanTraffic();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), new Throwable(e));
        }
    }
}
