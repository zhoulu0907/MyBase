package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileContentDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

/**
 * 文件内容数据访问层
 *
 * 负责文件内容相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class FileContentDataRepositoryOld extends DataRepository<FileContentDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public FileContentDataRepositoryOld() {
        super(FileContentDO.class);
    }

    /**
     * 根据配置ID和路径删除文件内容
     *
     * @param configId 配置ID
     * @param path 文件路径
     */
    public void deleteByConfigIdAndPath(Long configId, String path) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("configId", configId).eq("path", path);
        deleteByConfig(configStore);
    }

    /**
     * 根据配置ID和路径查询文件内容
     *
     * @param configId 配置ID
     * @param path 文件路径
     * @return 文件内容字节数组，如果不存在返回null
     */
    public byte[] getContentByConfigIdAndPath(Long configId, String path) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FileContentDO.COLUMN_CONFIG_ID, configId).eq(FileContentDO.COLUMN_PATH, path);

        List<FileContentDO> list = findAllByConfig(configStore);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        // 排序后，拿 id 最大的，即最后上传的
        list.sort(Comparator.comparing(FileContentDO::getId));
        return CollUtil.getLast(list).getContent();
    }
}
