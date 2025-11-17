package com.cmsr.onebase.module.bpm.runtime.utils;

import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.bpm.core.dto.PageViewDTO;
import com.cmsr.onebase.module.bpm.core.dto.PageViewGroupDTO;
import com.cmsr.onebase.module.bpm.core.enums.PageViewModeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 页面视图工具类
 *
 * @author liyang
 * @date 2025-11-14
 */
@Component
public class PageViewUtil {
    @Resource
    private AppResourceApi appResourceApi;

    public PageViewGroupDTO findPageViewGroup(Long pageSetId){
        // 详情视图页面和编辑视图页面
        PageViewDTO editPageView = null;
        PageViewDTO detailPageView = null;

        // 查出页面列表
        List<PageRespDTO> pageRespDTOs = appResourceApi.findPageListByPageSetId(pageSetId);

        for (PageRespDTO pageRespDTO : pageRespDTOs) {
            // 只取表单类型
            if (!Objects.equals(pageRespDTO.getPageType(), "form")) {
                continue;
            }

            if (Objects.equals(pageRespDTO.getIsDefaultEditViewMode(), 1)) {
                editPageView = new PageViewDTO();
                editPageView.setViewName(pageRespDTO.getPageName());
                editPageView.setViewId(pageRespDTO.getId());
                editPageView.setViewMode(PageViewModeEnum.EDIT.getCode());
            }

            if (Objects.equals(pageRespDTO.getIsDefaultDetailViewMode(), 1)) {
                detailPageView = new PageViewDTO();
                detailPageView.setViewName(pageRespDTO.getPageName());
                detailPageView.setViewId(pageRespDTO.getId());
                detailPageView.setViewMode(PageViewModeEnum.DETAIL.getCode());
            }

            if (editPageView != null && detailPageView != null) {
                break;
            }
        }

        if (editPageView != null && detailPageView != null) {
            PageViewGroupDTO pageViewGroupDTO = new PageViewGroupDTO();
            pageViewGroupDTO.setEditPageView(editPageView);
            pageViewGroupDTO.setDetailPageView(detailPageView);
            return pageViewGroupDTO;
        }

        return null;
    }
}
