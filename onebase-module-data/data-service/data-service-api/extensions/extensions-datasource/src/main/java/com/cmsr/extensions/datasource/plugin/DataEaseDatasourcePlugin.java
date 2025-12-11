package com.cmsr.extensions.datasource.plugin;

//import com.cmsr.exception.DEException;
import com.cmsr.extensions.datasource.dto.DatasourceRequest;
import com.cmsr.extensions.datasource.factory.ProviderFactory;
import com.cmsr.extensions.datasource.provider.Provider;
import com.cmsr.extensions.datasource.vo.XpackPluginsDatasourceVO;
//import com.cmsr.license.utils.JsonUtil;
//import com.cmsr.plugins.template.DataEasePlugin;
//import com.cmsr.plugins.vo.DataEasePluginVO;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
//public abstract class DataEaseDatasourcePlugin extends Provider implements DataEasePlugin {

/**
 * @Author Junjun
 */
public abstract class DataEaseDatasourcePlugin extends Provider{
    private final String DEFAULT_FILE_PATH = "/opt/dataease2.0/drivers/plugin";


    @Override
    public List<String> getSchema(DatasourceRequest datasourceRequest) {
        return new ArrayList<>();
    }



    public void loadPlugin() {
        XpackPluginsDatasourceVO datasourceConfig = getConfig();
        ProviderFactory.loadPlugin(datasourceConfig.getType(), this);
        try {
            loadDriver();
        } catch (Exception e) {
            //DEException.throwException(e);
        }
    }

    private void loadDriver() throws Exception {
        XpackPluginsDatasourceVO config = getConfig();
        String localPath = StringUtils.isEmpty(config.getDriverPath()) ? DEFAULT_FILE_PATH : config.getDriverPath();
        ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
        URI uri = protectionDomain.getCodeSource().getLocation().toURI();
        try (JarFile jarFile = new JarFile(new File(uri))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (StringUtils.endsWith(name, ".jar")) {
                    File file = new File(localPath, Paths.get(name).getFileName().toString());
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    try (InputStream inputStream = jarFile.getInputStream(entry);
                         FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = inputStream.read(bytes)) >= 0) {
                            outputStream.write(bytes, 0, length);
                        }
                    }
                }
            }
        }
    }

    public XpackPluginsDatasourceVO getConfig() {
        XpackPluginsDatasourceVO vo = new XpackPluginsDatasourceVO();
/*         DataEasePluginVO pluginInfo = null;
        try {
            pluginInfo = getPluginInfo();
        } catch (Exception e) {
            DEException.throwException(e);
        }
        String config = pluginInfo.getConfig();
        XpackPluginsDatasourceVO vo = JsonUtil.parseObject(config, XpackPluginsDatasourceVO.class);
        vo.setIcon(pluginInfo.getIcon()); */
        return vo;
    }


    public void unloadPlugin() {
        try {
            ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
            URI uri = protectionDomain.getCodeSource().getLocation().toURI();
            try (JarFile jarFile = new JarFile(new File(uri))) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (StringUtils.endsWith(name, ".jar")) {
                        File file = new File(DEFAULT_FILE_PATH, Paths.get(name).getFileName().toString());
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            //DEException.throwException(e);
        }
    }
}
