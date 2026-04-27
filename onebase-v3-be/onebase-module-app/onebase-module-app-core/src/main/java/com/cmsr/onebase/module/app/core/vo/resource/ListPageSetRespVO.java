package com.cmsr.onebase.module.app.core.vo.resource;

import com.cmsr.onebase.module.app.core.dto.resource.PageSetRespDTO;
import lombok.Data;

import java.util.List;

/**
 * 页面集响应VO
 *
 * @author liyang
 * @date 2025-11-11
 *
 */
@Data
public class ListPageSetRespVO {
    private List<PageSetVO> pageSets;

    @Data
    public static class PageSetVO extends PageSetRespDTO {
        private Integer pageSetType;
    }
}
