package com.cmsr.map.server;

import com.cmsr.api.map.MapApi;
import com.cmsr.api.map.vo.AreaNode;
import com.cmsr.map.manage.MapManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class MapServer implements MapApi {
    @Resource
    private MapManage mapManage;

    @Override
    public AreaNode getWorldTree() {
        return mapManage.getWorldTree();
    }
}
