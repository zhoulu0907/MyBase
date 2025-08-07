package com.cmsr.onebase.module.app.service.tag;

import java.util.List;

import com.cmsr.onebase.module.app.controller.admin.tag.vo.TagListRespVO;

/**
 * @Author：mickey
 * @Date：2025/7/23 13:40
 */
public interface AppTagService {

    List<TagListRespVO> listTags(String tagName);

    void createTag(String tagName);
}
