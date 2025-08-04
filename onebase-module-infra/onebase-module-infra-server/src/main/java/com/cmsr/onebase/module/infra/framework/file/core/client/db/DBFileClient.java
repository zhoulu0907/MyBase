package com.cmsr.onebase.module.infra.framework.file.core.client.db;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileContentDO;
import com.cmsr.onebase.module.infra.framework.file.core.client.AbstractFileClient;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;

import java.util.Comparator;
import java.util.List;

/**
 * 基于 DB 存储的文件客户端的配置类
 *
 */
public class DBFileClient extends AbstractFileClient<DBFileClientConfig> {

    @Resource
    private DataRepository dataRepository;

    public DBFileClient(Long id, DBFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {}

    @Override
    public String upload(byte[] content, String path, String type) {
        FileContentDO contentDO = new FileContentDO().setConfigId(getId())
                .setPath(path).setContent(content);
        dataRepository.insert(contentDO);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        dataRepository.deleteByConfig(FileContentDO.class, new DefaultConfigStore()
                .eq("id",getId()).eq("path", path));
    }

    @Override
    public byte[] getContent(String path) {

        List<FileContentDO> list = dataRepository.findAll(FileContentDO.class,new DefaultConfigStore()
                .eq("id",getId()).eq("path", path));
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        // 排序后，拿 id 最大的，即最后上传的
        list.sort(Comparator.comparing(FileContentDO::getId));
        return CollUtil.getLast(list).getContent();
    }

}
