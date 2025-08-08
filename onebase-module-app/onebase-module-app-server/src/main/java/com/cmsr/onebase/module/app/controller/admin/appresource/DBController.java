package com.cmsr.onebase.module.app.controller.admin.appresource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.util.db.TableInitializer;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/app/resource/db")
public class DBController {
    @Resource
    private TableInitializer tableInitializer;

    @PostMapping("/init")
    public CommonResult<Boolean> init() throws Exception {

        // 执行表初始化
        tableInitializer.initTables();
        tableInitializer.initDML();

        return CommonResult.success(true);
    }
}
