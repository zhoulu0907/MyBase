package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.infra.dal.dataflexdo.file.FileConfigDO;
import com.cmsr.onebase.module.infra.dal.mapper.file.FileConfigMapper;
import com.cmsr.onebase.module.infra.dal.vo.file.config.FileConfigPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Repository;

/**
 * 文件配置数据访问层
 * <p>
 * 负责文件配置相关的数据操作，基于MyBatis-Flex实现
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class FileConfigDataRepository extends ServiceImpl<FileConfigMapper, FileConfigDO> {

    /**
     * 根据master状态查询文件配置
     *
     * @param master 是否为主配置
     * @return 文件配置对象
     */
    public FileConfigDO findByMaster(Integer master) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FileConfigDO.COLUMN_MASTER, master, master != null);
        return getOne(queryWrapper);
    }

    /**
     * 分页查询文件配置
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<FileConfigDO> findPage(FileConfigPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FileConfigDO.COLUMN_NAME, pageReqVO.getName())
                .eq(FileConfigDO.COLUMN_STORAGE, pageReqVO.getStorage());

        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            queryWrapper.ge(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            queryWrapper.le(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
        }
        
        queryWrapper.orderBy(BaseDO.CREATE_TIME, false);
        
        Page<FileConfigDO> page = this.page(new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    /**
     * 更新所有配置为非master状态
     */
    public void updateAllToNotMaster() {
        // 更新所有记录
        FileConfigDO updateObj = new FileConfigDO();
        updateObj.setMaster(0); // Changed from updateObj.setMaster(false);
        this.update(updateObj, new QueryWrapper().isNotNull(BaseDO.ID));
    }

    /**
     * 更新指定配置为master状态
     *
     * @param id 配置ID
     */
    public void updateToMaster(Long id) {
        FileConfigDO updateObj = new FileConfigDO();
        updateObj.setId(id);
        updateObj.setMaster(NumberUtils.INTEGER_ONE);
        this.updateById(updateObj);
    }
}