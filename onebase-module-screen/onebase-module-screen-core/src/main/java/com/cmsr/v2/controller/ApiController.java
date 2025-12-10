package com.cmsr.v2.controller;

import com.cmsr.v2.common.base.BaseController;
import com.cmsr.v2.common.config.V2Config;
import com.cmsr.v2.common.domain.AjaxResult;
import com.cmsr.v2.model.SysUser;
import com.cmsr.v2.service.ISysUserService;
import com.cmsr.v2.util.SaTokenUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/goview/sys")
public class ApiController  extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private V2Config v2Config;

	@ApiOperation(value = "登陆", notes = "登陆")
	@PostMapping("/login")
	@ResponseBody
	public AjaxResult APIlogin(@RequestBody SysUser user, HttpServletRequest request) {
		try {
			// 判断是否登陆
			if (StpUtil.isLogin()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userinfo", SaTokenUtil.getUser());
				map.put("token", StpUtil.getTokenInfo());
				return success().put("data", map);
			} else {
				if (StrUtil.isNotBlank(user.getUsername()) && StrUtil.isNotBlank(user.getPassword())) {
					// 直接查询用户
					LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
					queryWrapper.eq(SysUser::getUsername, user.getUsername());

					try {
						SysUser sysUser = iSysUserService.getOne(queryWrapper);

						if (sysUser != null) {
							// 验证密码 - 对用户输入的密码进行MD5加密，然后与数据库中存储的密码比较
							String inputPasswordMd5 = SecureUtil.md5(user.getPassword());
							if (inputPasswordMd5.equals(sysUser.getPassword())) {
								StpUtil.login(sysUser.getId());
								SaTokenUtil.setUser(sysUser);

								Map<String, Object> map = new HashMap<String, Object>();
								map.put("userinfo", sysUser);
								map.put("token", StpUtil.getTokenInfo());

								return success().put("data", map);
							}
						}
						return error(500, "账户或者密码错误");
					} catch (Exception e) {
						logger.error("数据库查询用户异常: " + e.getMessage(), e);
						return error(500, "数据库查询异常: " + e.getMessage());
					}
				} else {
					return error(500, "账户密码不能为空");
				}
			}
		} catch (Exception e) {
			logger.error("登录失败: " + e.getMessage(), e);
			return error(500, "登录失败：" + e.getMessage());
		}
	}


	@ApiOperation(value = "登陆", notes = "登陆")
	@GetMapping("/logout")
	@ResponseBody
	public AjaxResult logout() {
		// 判断是否登陆
		StpUtil.logout();
		return success();
	}


	@ApiOperation(value = "获取oss地址", notes = "获取oss地址")
	@GetMapping("/getOssInfo")
	@ResponseBody
	public AjaxResult getOssInfo() {
		Map<String, Object> ossInfo = new HashMap<>();
		ossInfo.put("bucketName", "oss");
		ossInfo.put("requestUrl", v2Config.getHttpurl());
		ossInfo.put("fileBasePath", v2Config.getFileurl());
		ossInfo.put("defaultFormat", v2Config.getDefaultFormat());
		ossInfo.put("xnljmap", v2Config.getXnljmap());

		return success().put("data", ossInfo);
	}
}
