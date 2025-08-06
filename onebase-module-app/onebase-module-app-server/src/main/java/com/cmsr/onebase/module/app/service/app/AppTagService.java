package com.cmsr.onebase.module.app.service.app;

import java.util.List;

import com.cmsr.onebase.module.app.controller.admin.app.vo.TagListRespVO;

/**
 * @Author：mickey
 * @Date：2025/7/23 13:40
 */
public interface AppTagService {

    List<TagListRespVO> listTags(String tagName);

    void createTag(String tagName);
}
