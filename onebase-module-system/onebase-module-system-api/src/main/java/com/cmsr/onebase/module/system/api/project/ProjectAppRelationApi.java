package com.cmsr.onebase.module.system.api.project;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 项目应用关联")
public interface ProjectAppRelationApi {

    String PREFIX = ApiConstants.PREFIX + "/project-app-relation";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建项目应用关联")
    @Parameter(name = "projectId", description = "项目ID", example = "1024", required = true)
    @Parameter(name = "applicationId", description = "应用ID", example = "2048", required = true)
    CommonResult<Long> createProjectAppRelation(@RequestParam("projectId") String projectId,
                                                 @RequestParam("applicationId") Long applicationId);
    @PostMapping(PREFIX + "/remove")
    @Operation(summary = "删除项目应用关联")
    @Parameter(name = "applicationId", description = "应用ID", example = "2048", required = true)
    CommonResult<Boolean> removeProjectAppRelation(@RequestParam("applicationId") Long applicationId);

    @PostMapping(PREFIX + "/list-application-ids")
    @Operation(summary = "根据项目ID查询所有应用ID列表")
    @Parameter(name = "projectId", description = "项目ID", example = "1024", required = true)
    CommonResult<List<Long>> listApplicationIdsByProjectId(@RequestParam("projectId") String projectId);

}
