package com.cmsr.onebase.module.app.service.tag;

import com.cmsr.onebase.module.app.controller.admin.tag.vo.TagRespVO;

import java.util.List;

/**
 * @Author：mickey
 * @Date：2025/7/23 13:40
 */
public interface AppTagService {

    List<TagRespVO> listTags(String tagName);

    void createTag(String tagName);
}
