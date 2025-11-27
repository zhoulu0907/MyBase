package com.cmsr.onebase.module.app.core.dal.database.tag;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppTagDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppTagMapper;
import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:19
 */
@Repository
public class AppTagRepository extends ServiceImpl<AppTagMapper, AppTagDO> {

    public List<AppTagDO> findAllTags() {
        return this.list(this.query().orderBy(AppTagDO::getTagName, true));
    }

    public List<AppTagDO> findByTagNameLike(String tagName) {
        QueryWrapper queryWrapper = this.query()
                .like(AppTagDO::getTagName, tagName, StringUtils::isNotBlank)
                .orderBy(AppTagDO::getTagName, true);
        return list(queryWrapper);
    }

    public Long countByTagName(String tagName) {
        return count(this.query().eq(AppTagDO::getTagName, tagName));
    }

    public List<TagGroupCountVO> selectNameCounts() {
        return this.mapper.selectNameCounts();
    }

}
