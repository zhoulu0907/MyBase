package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileDO;

import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePageReqVO;
import org.anyline.data.param.init.DefaultConfigStore;
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
     * @param md5 MD5值
     * @return 文件信息
     */
    public FileDO findByMd5(String md5) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FileDO.COLUMN_MD5, md5);
        return findOne(configStore);
    }
}
