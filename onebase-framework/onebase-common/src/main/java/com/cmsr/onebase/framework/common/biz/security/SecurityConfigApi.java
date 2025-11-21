package com.cmsr.onebase.framework.common.biz.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.biz.security.dto.PasswordExpiryCheckDTO;
import com.cmsr.onebase.framework.common.biz.security.dto.LoginFailureResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
     * 检查并限制设备数
     * 
     * 登录时调用，如果超过maxOnlineDevices，踢出最早登录的设备
     * 返回被踢出的Token列表，调用方需要删除这些Token
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param newAccessToken 新登录的AccessToken
     * @return 被踢出的Token列表（可能为空）
     */
    @PostMapping("/check-device-limit")
    CommonResult<List<String>> checkAndLimitDevices(@RequestParam("userId") Long userId,
                                                     @RequestParam("deviceId") String deviceId,
                                                     @RequestParam("newAccessToken") String newAccessToken);

    /**
     * 添加Token到在线设备列表
     * 
     * RefreshToken刷新时调用，将新Token添加到在线设备列表
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param accessToken AccessToken
     * @return 操作结果
     */
    @PostMapping("/add-online-device")
    CommonResult<Boolean> addOnlineDevice(@RequestParam("userId") Long userId,
                                           @RequestParam("deviceId") String deviceId,
                                           @RequestParam("accessToken") String accessToken);

    /**
     * 清理在线设备记录
     * 
     * 用户登出或Token过期时调用
     *
     * @param userId 用户ID
     * @param accessToken AccessToken
     * @return 操作结果
     */
    @PostMapping("/remove-online-device")
    CommonResult<Boolean> removeOnlineDevice(@RequestParam("userId") Long userId,
                                              @RequestParam("accessToken") String accessToken);

    /**
     * 通过Token反查设备ID
     * 
     * RefreshToken场景下用于获取旧Token对应的deviceId
     *
     * @param userId 用户ID
     * @param accessToken AccessToken
     * @return 设备ID，未找到返回null
     */
    @PostMapping("/find-device-id")
    CommonResult<String> findDeviceIdByToken(@RequestParam("userId") Long userId,
                                              @RequestParam("accessToken") String accessToken);

    /**
     * 创建会话空闲Redis Key
     * 
     * 用户登录成功后调用，初始化会话空闲检测
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 操作结果
     */
    @PostMapping("/session-idle/create")
    CommonResult<Boolean> createSessionIdleKey(@RequestParam("userId") Long userId,
                                                @RequestParam("deviceId") String deviceId);

    /**
     * 更新会话空闲Redis Key
     * 
     * 用户每次操作时调用（在拦截器或Filter中），更新会话活跃时间和TTL
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return true-更新成功，false-会话已过期或不存在
     */
    @PostMapping("/session-idle/update")
    CommonResult<Boolean> updateSessionIdleKey(@RequestParam("userId") Long userId,
                                                @RequestParam("deviceId") String deviceId);

    /**
     * 检查会话空闲Redis Key是否存在
     * 
     * AccessToken过期使用RefreshToken刷新前调用
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return true-会话有效，false-会话已过期
     */
    @PostMapping("/session-idle/exist")
    CommonResult<Boolean> existSessionIdleKey(@RequestParam("userId") Long userId,
                                               @RequestParam("deviceId") String deviceId);

}
