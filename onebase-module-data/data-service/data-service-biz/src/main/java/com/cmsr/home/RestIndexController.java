package com.cmsr.home;

import com.cmsr.home.manage.DeIndexManage;
import com.cmsr.utils.ModelUtils;
import com.cmsr.utils.RsaUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RestIndexController {


    @Resource
    private DeIndexManage deIndexManage;

    @GetMapping("/dekey")
    @ResponseBody
    public String dekey() {
        return RsaUtils.publicKey();
    }

    @GetMapping("/symmetricKey")
    @ResponseBody
    public String symmetricKey() {
        return RsaUtils.generateSymmetricKey();
    }


    @GetMapping("/model")
    @ResponseBody
    public boolean model() {
        return ModelUtils.isDesktop();
    }


    @GetMapping("/xpackModel")
    @ResponseBody
    public Boolean xpackModel() {
        return deIndexManage.xpackModel();
    }

}
