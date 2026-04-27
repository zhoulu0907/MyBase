package com.cmsr.controller;

import com.cmsr.common.util.R;
import com.cmsr.service.QwenVLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class VisionController {

    @Autowired(required = false)
    private QwenVLService qwenService;

    @PostMapping("/describe")
    public R describeImage(@RequestBody Map map) {
        if (qwenService == null) {
            return new R().put("code", 503).put("msg", "图片描述服务未启用");
        }
        String imageUrl = (String) map.get("imageUrl");
        String prompt = (String) map.get("prompt");
        String res = qwenService.generateDescription(imageUrl, prompt);
        Map resMap = new HashMap();
        resMap.put("text",res);
        return new R().put("data",resMap);
    }
}
