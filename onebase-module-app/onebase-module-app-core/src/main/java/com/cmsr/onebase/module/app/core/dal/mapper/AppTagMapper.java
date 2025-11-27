package com.cmsr.onebase.module.app.core.dal.mapper;

import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import com.mybatisflex.core.BaseMapper;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppTagDO;

import java.util.List;

/**
 *  映射层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public interface AppTagMapper extends BaseMapper<AppTagDO> {

    List<TagGroupCountVO> selectNameCounts();
}
