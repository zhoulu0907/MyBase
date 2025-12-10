package cmsr.controller;

import cmsr.common.util.R;
import cmsr.service.QwenVLService;
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

    @Autowired
    private QwenVLService qwenService;

    @PostMapping("/describe")
    public R describeImage(@RequestBody Map map) {
        String imageUrl = (String) map.get("imageUrl");
        String prompt = (String) map.get("prompt");
        System.out.println(prompt);
        String res = qwenService.generateDescription(imageUrl, prompt);
        Map resMap = new HashMap();
        resMap.put("text",res);
        return new R().put("data",resMap);
    }
}
