package com.cmsr.onebase.module.app.core.dal.database.tag;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppTagDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppTagMapper;
import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import com.cmsr.onebase.module.app.core.vo.tag.TagRespVO;
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
public class AppTagRepository extends ServiceImpl<AppTagMapper, AppTagDO> {

    public List<AppTagDO> findAllTags() {
        return this.list(this.query().orderBy(AppTagDO::getTagName, true));
    }

    public List<AppTagDO> findByTagNameLike(String tagName) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_TAG.TAG_NAME.like(tagName).when(StringUtils.isNotBlank(tagName)))
                .orderBy(APP_TAG.TAG_NAME, true);
        return list(queryWrapper);
    }

    public Long countByTagName(String tagName) {
        return count(this.query().eq(AppTagDO::getTagName, tagName));
    }

    public List<TagGroupCountVO> selectNameCounts(Long tenantId) {
        return this.mapper.selectNameCounts(tenantId);
    }

    public List<TagRespVO> selectTagVoByAppIds(List<Long> appIds) {
        QueryWrapper queryWrapper = this.query()
                .select(APP_APPLICATION_TAG.APPLICATION_ID, APP_TAG.ID, APP_TAG.TAG_NAME)
                .from(APP_APPLICATION_TAG)
                .leftJoin(APP_TAG).on(APP_APPLICATION_TAG.TAG_ID.eq(APP_TAG.ID))
                .orderBy(APP_TAG.TAG_NAME, true);
        return this.listAs(queryWrapper, TagRespVO.class);
    }
}
