package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileConfigDO;
import com.cmsr.onebase.module.infra.dal.vo.file.config.FileConfigPageReqVO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.Order;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Repository;

/**
 * 文件配置数据访问层
 * <p>
 * 负责文件配置相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class FileConfigDataRepositoryOld extends DataRepository<FileConfigDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public FileConfigDataRepositoryOld() {
        super(FileConfigDO.class);
    }

    /**
     * 根据master状态查询文件配置
     *
     * @param master 是否为主配置
     * @return 文件配置对象
     */
    public FileConfigDO findByMaster(Boolean master) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FileConfigDO.COLUMN_MASTER, master);
        return findOne(configStore);
    }

    /**
     * 分页查询文件配置
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<FileConfigDO> findPage(FileConfigPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FileConfigDO.COLUMN_NAME, pageReqVO.getName())
                .eq(FileConfigDO.COLUMN_STORAGE, pageReqVO.getStorage());

        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            configStore.ge(FileConfigDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            configStore.le(FileConfigDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
        }
        configStore.order(FileConfigDO.CREATE_TIME, Order.TYPE.DESC);
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 更新所有配置为非master状态
     */
    public void updateAllToNotMaster() {
        // 更新所有记录
        DataRow row = new DataRow();
        row.put(FileConfigDO.COLUMN_MASTER, false);
        updateByConfig(row, new DefaultConfigStore().isNotNull(FileConfigDO.ID));
    }

    /**
     * 更新指定配置为master状态
     *
     * @param id 配置ID
     */
    public void updateToMaster(Long id) {
        FileConfigDO updateObj = new FileConfigDO().setId(id).setMaster(NumberUtils.INTEGER_ONE);
        update(updateObj);
    }
}
