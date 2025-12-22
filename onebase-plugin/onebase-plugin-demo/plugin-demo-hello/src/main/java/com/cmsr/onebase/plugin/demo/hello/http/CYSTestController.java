package com.cmsr.onebase.plugin.demo.hello.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/plugin/hello-plugin")
public class CYSTestController implements HttpHandler {
    /**
     * Hello World接口
     * <p>访问路径：GET /plugin/hello-plugin/hello?name=xxx</p>
     */
    @GetMapping("/cysinfo")
    public Map<String, Object> hello(@RequestParam(defaultValue = "World") String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "this is cys test, " + name + "!");
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        result.put("plugin", "hello-plugin");

        return result;
    }
}
