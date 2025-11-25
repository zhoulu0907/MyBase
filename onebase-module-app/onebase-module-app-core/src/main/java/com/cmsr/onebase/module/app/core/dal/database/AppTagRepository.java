package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.TagDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppTagMapper;
import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppApplicationTagTableDef.APP_APPLICATION_TAG;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppTagTableDef.APP_TAG;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:19
 */
@Repository
public class AppTagRepository extends ServiceImpl<AppTagMapper, TagDO> {

    public List<TagDO> findAllTags() {
        return this.list(this.query().orderBy(TagDO::getTagName, true));
    }

    public List<TagDO> findByTagNameLike(String tagName) {
        QueryWrapper queryWrapper = this.query()
                .like(TagDO::getTagName, tagName, StringUtils::isNotBlank)
                .orderBy(TagDO::getTagName, true);
        return list(queryWrapper);
    }

    public Long countByTagName(String tagName) {
        return count(this.query().eq(TagDO::getTagName, tagName));
    }

    public List<TagGroupCountVO> groupCount() {
        QueryWrapper queryWrapper = this.query()
                .select(
                        new QueryColumn("id"),
                        new QueryColumn("tag_name"),
                        QueryMethods.sum("one").as("counts")
                )
                .from(
                        this.query()
                                .select(
                                        APP_TAG.ID,
                                        APP_TAG.TAG_NAME,
                                        QueryMethods.case_().when(APP_APPLICATION_TAG.APPLICATION_ID.isNull()).then(0)
                                                .else_(1).end().as("one")
                                )
                                .from(APP_APPLICATION_TAG)
                                .fullJoin(APP_TAG)
                                .on(APP_APPLICATION_TAG.TAG_ID.eq(APP_TAG.ID))
                                .where(APP_TAG.ID.isNotNull())
                )
                .groupBy(new QueryColumn("id"), new QueryColumn("tag_name"))
                .orderBy(new QueryColumn("counts"), true);
        return this.listAs(queryWrapper, TagGroupCountVO.class);
    }


}
