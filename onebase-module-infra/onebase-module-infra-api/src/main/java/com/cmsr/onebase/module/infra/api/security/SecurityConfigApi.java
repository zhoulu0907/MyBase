package com.cmsr.onebase.module.infra.api.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.security.dto.PasswordExpiryCheckDTO;
import com.cmsr.onebase.module.infra.api.security.dto.LoginFailureResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 密码校验API接口
 * 
 * 提供跨模块的密码校验能力
 * 其他模块可通过Feign调用此接口，对密码进行强度检查
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@RequestMapping("/infra/api/security-config")
@Tag(name = "基础设施 - 密码校验")
@FeignClient(name = "infra-service")
public interface SecurityConfigApi {

    /**
     * 校验密码强度
     * 
     * 基于当前租户的密码策略配置，对密码进行强度检查
     * 如果密码不符合要求，返回相应的错误信息
     *
     * @param password 待校验的密码
     * @return 校验结果，成功返回success，失败返回error及错误信息
     */
    @PostMapping("/validate")
    CommonResult<Boolean> validatePassword(@RequestParam("password") String password);

    /**
     * 校验密码历史
     * 
     * 检查新密码是否与历史密码重复
     * 基于当前租户的historyLimit配置，比对历史密码记录
     * 如果新密码与历史密码重复，抛出业务异常
     *
     * @param userId   用户ID
     * @param password 待校验的新密码（明文）
     * @return 校验结果，成功返回success，失败返回error及错误信息
     */
    @PostMapping("/validate-history")
    CommonResult<Boolean> validatePasswordHistory(@RequestParam("userId") Long userId,
                                                   @RequestParam("password") String password);

    /**
     * 保存密码历史
     * 
     * 在用户修改密码后，将加密后的新密码保存到历史记录表
     * 如果历史记录数超过historyLimit，自动删除最旧的记录
     *
     * @param userId          用户ID
     * @param encodedPassword 加密后的密码
     * @return 保存结果
     */
    @PostMapping("/save-history")
    CommonResult<Boolean> savePasswordHistory(@RequestParam("userId") Long userId,
                                               @RequestParam("encodedPassword") String encodedPassword);

    /**
     * 检查密码有效期
     * 
     * 查询用户最近一次密码记录的创建时间，计算密码年龄
     * 与租户配置的expiryDays比较，判断密码是否已过期
     *
     * @param userId 用户ID
     * @return 检查结果DTO，包含type(expired/valid)、过期天数、提示信息等
     */
    @PostMapping("/check-expiry")
    CommonResult<PasswordExpiryCheckDTO> checkPasswordExpiry(@RequestParam("userId") Long userId);

    /**
     * 检查账号是否被锁定
     * 
     * 检查指定用户账号是否因登录失败次数过多而被锁定
     * 如果已锁定，返回剩余锁定时间（秒）
     *
     * @param userId 用户ID
     * @return 剩余锁定时间（秒），null表示未锁定
     */
    @PostMapping("/check-locked")
    CommonResult<Long> checkAccountLocked(@RequestParam("userId") Long userId);

    /**
     * 记录登录失败
     * 
     * 记录用户登录失败，增加失败次数
     * 如果失败次数达到阈值，自动锁定账号
     * 返回处理结果，包含是否锁定、剩余尝试次数、剩余锁定时间等信息
     *
     * @param userId 用户ID
     * @return 失败处理结果
     */
    @PostMapping("/record-failure")
    CommonResult<LoginFailureResultDTO> recordLoginFailure(@RequestParam("userId") Long userId);

    /**
     * 清除登录失败记录
     * 
     * 用户登录成功后，清除失败次数记录和锁定状态
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/clear-failure")
    CommonResult<Boolean> clearLoginFailureRecord(@RequestParam("userId") Long userId);

    /**
     * 获取验证码有效期配置
     * 
     * 从当前租户的安全配置中读取验证码有效期（秒）
     * 用于aj-captcha在生成验证码时设置TTL
     *
     * @return 有效期（秒），默认600秒
     */
    @PostMapping("/captcha/expiry-seconds")
    CommonResult<Integer> getCaptchaExpirySeconds();

    /**
     * 检查是否可以刷新验证码
     * 
     * 根据当前租户的刷新间隔配置，检查是否允许刷新
     * 如果刷新过快，抛出业务异常
     *
     * @param sessionKey 会话标识（IP+UserAgent）
     * @return 检查结果，true=可以刷新
     */
    @PostMapping("/captcha/check-refresh")
    CommonResult<Boolean> checkCanRefreshCaptcha(@RequestParam("sessionKey") String sessionKey);

    /**
     * 检查场景是否启用验证码
     * 
     * 从当前租户的安全配置中读取启用场景列表
     * 检查指定场景是否需要验证码
     *
     * @param scenario 场景标识（login/pwdreset/register/unlock/bind）
     * @return true=启用验证码，false=不启用验证码
     */
    @PostMapping("/captcha/check-scenario")
    CommonResult<Boolean> isCaptchaEnabledForScenario(@RequestParam("scenario") String scenario);

}
