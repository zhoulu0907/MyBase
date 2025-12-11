package com.cmsr.extensions.datasource.factory;

import com.cmsr.exception.DEException;
import com.cmsr.extensions.datasource.plugin.DataEaseDatasourcePlugin;
import com.cmsr.extensions.datasource.provider.Provider;
import com.cmsr.extensions.datasource.utils.SpringContextUtil;
import com.cmsr.extensions.datasource.vo.DatasourceConfiguration;
import com.cmsr.extensions.datasource.vo.XpackPluginsDatasourceVO;
//import com.cmsr.license.utils.LicenseUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Junjun
 */
public class ProviderFactory {

    public static Provider getProvider(String type) throws DEException {
        if (type.equalsIgnoreCase("es")) {
            return SpringContextUtil.getApplicationContext().getBean("esProvider", Provider.class);
        }
        List<String> list = Arrays.stream(DatasourceConfiguration.DatasourceType.values()).map(DatasourceConfiguration.DatasourceType::getType).toList();
        if (list.contains(type)) {
            return SpringContextUtil.getApplicationContext().getBean("calciteProvider", Provider.class);
        }
        Provider instance = getInstance(type);
        if (instance == null) {
            DEException.throwException("插件异常，请检查插件");
        }
        return instance;
    }

    public static Provider getDefaultProvider() {
        return SpringContextUtil.getApplicationContext().getBean("calciteProvider", Provider.class);
    }


    private static final Map<String, DataEaseDatasourcePlugin> templateMap = new ConcurrentHashMap<>();

    public static Provider getInstance(String type) {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        String key = type;
        return templateMap.get(key);
    }

    public static void loadPlugin(String type, DataEaseDatasourcePlugin plugin) {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        String key = type;
        if (templateMap.containsKey(key)) return;
        templateMap.put(key, plugin);
        try {
            // 移除未使用的变量
            // XpackPluginsDatasourceVO config = plugin.getConfig();
            // 移除对不存在的工厂类的调用
            // DataEasePluginFactory.loadTemplate(moduleName, plugin);
        } catch (Exception e) {
            // 替换为标准日志或直接抛出异常
            System.err.println("Error loading plugin: " + e.getMessage());
            DEException.throwException("Error loading plugin: " + e.getMessage());
        }
    }

    public static List<XpackPluginsDatasourceVO> getDsConfigList() {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        return templateMap.values().stream().map(DataEaseDatasourcePlugin::getConfig).toList();
    }
}
