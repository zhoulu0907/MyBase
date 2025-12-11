package com.cmsr.map.server;

import com.cmsr.api.map.CustomGeoApi;
import com.cmsr.api.map.vo.AreaNode;
import com.cmsr.api.map.vo.CustomGeoArea;
import com.cmsr.api.map.vo.CustomGeoSubArea;
import com.cmsr.map.manage.MapManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customGeo")
public class CustomGeoServer implements CustomGeoApi {

    @Resource
    private MapManage mapManage;

    @Override
    public List<CustomGeoArea> listCustomGeoArea() {
        return mapManage.listCustomGeoArea();
    }

    @Override
    public List<CustomGeoSubArea> getCustomGeoArea(String id) {
        return mapManage.getCustomGeoArea(id);
    }

    @Override
    public void deleteCustomGeoArea(String id) {
        mapManage.deleteCustomGeoArea(id);
    }

    @Override
    public void saveCustomGeoArea(CustomGeoArea geoArea) {
        mapManage.saveCustomGeoArea(geoArea);
    }

    @Override
    public void deleteCustomGeoSubArea(long id) {
        mapManage.deleteCustomGeoSubArea(id);
    }

    @Override
    public void saveCustomGeoSubArea(CustomGeoSubArea geoSubArea) {
        mapManage.saveCustomGeoSubArea(geoSubArea);
    }

    @Override
    public List<AreaNode> getCustomGeoSubAreaOptions() {
        return mapManage.getCustomGeoSubAreaOptions();
    }
}
