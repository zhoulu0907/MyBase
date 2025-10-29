package com.cmsr.onebase.module.app.core.dal.database.tag;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.tag.TagDO;
import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:19
 */
@Repository
public class AppTagRepository extends DataRepository<TagDO> {

    public AppTagRepository() {
        super(TagDO.class);
    }

    public List<TagDO> findAllTags() {
        ConfigStore configs = new DefaultConfigStore();
        return findAllByConfig(configs);
    }

    public List<TagDO> findByTagNameLike(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        if (StringUtils.isNotEmpty(tagName)) {
            configs.like("tag_name", tagName);
        }
        configs.order("tag_name", Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    public Long countByTagName(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("tag_name", tagName);
        return countByConfig(configs);
    }

    public List<TagGroupCountVO> groupCount() {
        ConfigStore configs = new DefaultConfigStore();
        String sql = """
                select
                	id,
                	tag_name,
                	sum(one) as counts
                from
                	(
                	select
                		tag.id,
                		tag.tag_name,
                		case
                			when aat.application_id is null then 0
                			else 1
                		end as one
                	from
                		app_application_tag aat
                	full outer join app_tag tag on
                		aat.tag_id = tag.id
                	where
                		tag.id is not null and tag.tenant_id = #{tenant_id}
                		and
                	(aat.deleted = 0
                			or aat.deleted is null)
                		and (tag.deleted = 0
                			or tag.deleted is null)
                ) as subquery
                where
                	id is not null
                group by
                	id,
                	tag_name
                order by
                	counts desc
            """;
        DataSet dataSet = this.querys(sql, configs);
        return dataSet.stream().map(row -> {
                    TagGroupCountVO tagGroupCountVO = new TagGroupCountVO();
                    tagGroupCountVO.setId(row.getLong("id"));
                    tagGroupCountVO.setTagName(row.getString("tag_name"));
                    tagGroupCountVO.setTagCount(row.getInt("counts"));
                    return tagGroupCountVO;
                }
        ).toList();
    }


}
