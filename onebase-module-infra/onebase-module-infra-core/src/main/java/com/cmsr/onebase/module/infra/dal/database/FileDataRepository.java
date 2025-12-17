package com.cmsr.onebase.module.infra.dal.database;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileDO;

import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePageReqVO;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

/**
 * 文件数据访问层
 *
 * 负责文件相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class FileDataRepository extends DataRepository<FileDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public FileDataRepository() {
        super(FileDO.class);
    }

    /**
     * 分页查询文件
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<FileDO> findPage(FilePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.like(FileDO.COLUMN_PATH, pageReqVO.getPath())
                .like(FileDO.COLUMN_TYPE, pageReqVO.getType());

        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            configStore.ge(FileDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            configStore.le(FileDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
        }
        configStore.order(FileDO.CREATE_TIME, Order.TYPE.DESC);
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 根据MD5查找文件
     *
     * @param md5       MD5值
     * @param visitMode
     * @return 文件信息
     */
    public FileDO findByMd5AndVisitMode(String md5, String visitMode) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FileDO.COLUMN_MD5, md5);
        configStore.eq(FileDO.COLUMN_VISIT_MODE, visitMode);
        return findOne(configStore);
    }

    /**
     * 根据id与visitMode查找文件
     *
     * @param id 文件id
     * @param visitMode 文件权限标识
     * @return 文件信息
     */
    public FileDO findByIdAndVisitMode(Long id, String visitMode) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FileDO.ID, id);
        if (StrUtil.isEmpty(visitMode)){
            configStore.and(Compare.NOT_EQUAL, FileDO.COLUMN_VISIT_MODE, FileVisitModeEnum.PERMISSION.getValue());
        } else {
            configStore.eq(FileDO.COLUMN_VISIT_MODE, visitMode);
        }
        return findOne(configStore);
    }
}
