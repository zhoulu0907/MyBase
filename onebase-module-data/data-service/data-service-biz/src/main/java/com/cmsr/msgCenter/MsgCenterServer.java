package com.cmsr.msgCenter;

import com.cmsr.api.msgCenter.MsgCenterApi;
//import com.cmsr.license.config.XpackInteract;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg-center")
public class MsgCenterServer implements MsgCenterApi {
    @Override
    //@XpackInteract(value = "msgCenterServer", replace = true)
    public long count() {
        return 0;
    }
}
