package com.cmsr.onebase.module.build.service.tag;

import com.cmsr.onebase.module.build.controller.tag.vo.TagRespVO;

import java.util.List;

/**
 * @Author：mickey
 * @Date：2025/7/23 13:40
 */
public interface AppTagService {

    List<TagRespVO> listTags(String tagName);

    void createTag(String tagName);

    void deleteTag(Long tagId);
}
