package com.cmsr.api.xpack.component;

import com.cmsr.api.xpack.component.vo.XpackMenuVO;
import com.cmsr.extensions.datafilling.vo.XpackPluginsDfVO;
import com.cmsr.extensions.datasource.vo.XpackPluginsDatasourceVO;
import com.cmsr.extensions.view.vo.XpackPluginsViewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface XpackComponentApi {

    @GetMapping("/content/{name}")
    String content(@PathVariable("name") String name);

    @GetMapping("/contentPlugin/{name}")
    String pluginContent(@PathVariable("name") String name);

    @GetMapping("/menu")
    List<XpackMenuVO> menu();

    @GetMapping("/viewPlugins")
    List<XpackPluginsViewVO> viewPlugins();

    @GetMapping("/dsPlugins")
    List<XpackPluginsDatasourceVO> dsPlugins();

    @GetMapping("/dfPlugins")
    List<XpackPluginsDfVO> dfPlugins();

    @GetMapping("/pluginStaticInfo/{moduleName}")
    void pluginStaticInfo(@PathVariable("moduleName") String moduleName);
}
