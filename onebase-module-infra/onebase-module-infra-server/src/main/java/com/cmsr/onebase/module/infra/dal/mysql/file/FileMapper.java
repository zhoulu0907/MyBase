package com.cmsr.onebase.module.infra.dal.mysql.file;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.mybatis.core.mapper.BaseMapperX;
import com.cmsr.onebase.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.cmsr.onebase.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件操作 Mapper
 *
 */
@Mapper
public interface FileMapper extends BaseMapperX<FileDO> {

    default PageResult<FileDO> selectPage(FilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileDO>()
                .likeIfPresent(FileDO::getPath, reqVO.getPath())
                .likeIfPresent(FileDO::getType, reqVO.getType())
                .betweenIfPresent(FileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileDO::getId));
    }

}
