package com.cmsr.onebase.framework.remote.dto.project;

import lombok.Data;

/**
 * 项目更新请求参数 DTO
 * 对应 DolphinScheduler 3.3.1 的 PUT /v2/projects/{code} 接口
 *
 * @author matianyu
 * @date 2025-10-16
 */
@Data
public class ProjectUpdateReqDTO {

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String description;
}
