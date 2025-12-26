package com.cmsr.onebase.module.dashboard.build.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.dashboard.build.common.base.BaseController;
import com.cmsr.onebase.module.dashboard.build.common.domain.AjaxResult;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard/sys")
public class DashboardOssController extends BaseController {


	// @Autowired
	// private V2Config v2Config;


	@ApiOperation(value = "获取oss地址", notes = "获取oss地址")
	@GetMapping("/getOssInfo")
	@ResponseBody
	@PermitAll
	@ApiSignIgnore
	@TenantIgnore
	public AjaxResult getOssInfo() {
		Map<String, Object> ossInfo = new HashMap<>();
		ossInfo.put("bucketName", "oss");
		// ossInfo.put("requestUrl", v2Config.getHttpurl());
		// ossInfo.put("fileBasePath", v2Config.getFileurl());
		// ossInfo.put("defaultFormat", v2Config.getDefaultFormat());
		// ossInfo.put("xnljmap", v2Config.getXnljmap());
		ossInfo.put("requestUrl", "");
		ossInfo.put("fileBasePath", "");
		ossInfo.put("defaultFormat", "");
		ossInfo.put("xnljmap", "");

		return successData(0, ossInfo);
	}
}
