package com.cmsr.onebase.module.infra.dal.dataflex;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.infra.dal.dataflexdo.file.FileDO;
import com.cmsr.onebase.module.infra.dal.mapper.file.FileMapper;
import com.cmsr.onebase.module.infra.dal.vo.file.file.FilePageReqVO;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 文件数据访问层
 *
 * 负责文件相关的数据操作，基于MyBatis-Flex实现
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class FileDataRepository extends ServiceImpl<FileMapper, FileDO> {

    /**
     * 分页查询文件
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<FileDO> findPage(FilePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        queryWrapper.like(FileDO.COLUMN_PATH, pageReqVO.getPath())
                .like(FileDO.COLUMN_TYPE, pageReqVO.getType());

        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            queryWrapper.ge(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            queryWrapper.le(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
        }
        
        queryWrapper.orderBy(BaseDO.CREATE_TIME, false);
        
        Page<FileDO> page = this.page(new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    /**
     * 根据MD5查找文件
     *
     * @param md5       MD5值
     * @param visitMode
     * @return 文件信息
     */
    public FileDO findByMd5AndVisitMode(String md5, String visitMode) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FileDO.COLUMN_MD5, md5);
        queryWrapper.eq(FileDO.COLUMN_VISIT_MODE, visitMode);
        return getOne(queryWrapper);
    }

    /**
     * 根据id与visitMode查找文件
     *
     * @param id 文件id
     * @param visitMode 文件权限标识
     * @return 文件信息
     */
    public FileDO findByIdAndVisitMode(Long id, String visitMode) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BaseDO.ID, id);
        if (StrUtil.isEmpty(visitMode)){
            queryWrapper.ne(FileDO.COLUMN_VISIT_MODE, FileVisitModeEnum.PERMISSION.getValue());
        } else {
            queryWrapper.eq(FileDO.COLUMN_VISIT_MODE, visitMode);
        }
        return getOne(queryWrapper);
    }
}