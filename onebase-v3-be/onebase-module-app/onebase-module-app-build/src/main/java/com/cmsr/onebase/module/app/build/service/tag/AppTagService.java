package com.cmsr.onebase.module.app.build.service.tag;

import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import com.cmsr.onebase.module.app.core.vo.tag.TagRespVO;

import java.util.List;

/**
 * @Author：mickey
 * @Date：2025/7/23 13:40
 */
public interface AppTagService {

    List<TagRespVO> listTags(String tagName);

    void createTag(String tagName);

    void deleteTag(Long tagId);

    List<TagGroupCountVO> groupCount();

    void updateTags(List<TagRespVO> tagRespVOS);
}
