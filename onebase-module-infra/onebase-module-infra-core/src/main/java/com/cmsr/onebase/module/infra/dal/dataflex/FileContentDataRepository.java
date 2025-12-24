package com.cmsr.onebase.module.infra.dal.dataflex;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.module.infra.dal.dataflexdo.file.FileContentDO;
import com.cmsr.onebase.module.infra.dal.mapper.file.FileContentMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

/**
 * 文件内容数据访问层
 *
 * 负责文件内容相关的数据操作，基于MyBatis-Flex实现
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class FileContentDataRepository extends ServiceImpl<FileContentMapper, FileContentDO> {

    /**
     * 根据配置ID和路径删除文件内容
     *
     * @param configId 配置ID
     * @param path 文件路径
     */
    public void deleteByConfigIdAndPath(Long configId, String path) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FileContentDO.COLUMN_CONFIG_ID, configId)
                .eq(FileContentDO.COLUMN_PATH, path);
        this.remove(queryWrapper);
    }

    /**
     * 根据配置ID和路径查询文件内容
     *
     * @param configId 配置ID
     * @param path 文件路径
     * @return 文件内容字节数组，如果不存在返回null
     */
    public byte[] getContentByConfigIdAndPath(Long configId, String path) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FileContentDO.COLUMN_CONFIG_ID, configId)
                .eq(FileContentDO.COLUMN_PATH, path);

        List<FileContentDO> list = list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        // 排序后，拿 id 最大的，即最后上传的
        list.sort(Comparator.comparing(FileContentDO::getId));
        return CollUtil.getLast(list).getContent();
    }
}