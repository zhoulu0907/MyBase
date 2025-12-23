package com.cmsr.onebase.module.system.api.sms;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.cmsr.onebase.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.cmsr.onebase.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import com.cmsr.onebase.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME) // TODO 开发者：fallbackFactory =
@Tag(name = "RPC 服务 - 短信验证码")
public interface SmsCodeApi {

    String PREFIX = ApiConstants.PREFIX + "/oauth2/sms/code";

    @PostMapping(PREFIX + "/send")
    @Operation(summary = "创建短信验证码，并进行发送")
    CommonResult<Boolean> sendSmsCode(@Valid @RequestBody SmsCodeSendReqDTO reqDTO);

    @PostMapping(PREFIX + "/use")
    @Operation(summary = "验证短信验证码，并进行使用")
    CommonResult<Boolean> useSmsCode(@Valid @RequestBody SmsCodeUseReqDTO reqDTO);

    @GetMapping(PREFIX + "/validate")
    @Operation(summary = "检查验证码是否有效")
    CommonResult<Boolean> validateSmsCode(@Valid @RequestBody SmsCodeValidateReqDTO reqDTO);

    @GetMapping(PREFIX + "/exists")
    @Operation(summary = "检查验证码是否存在")
    CommonResult<Boolean> existsCode(@Valid @RequestBody SmsCodeSendReqDTO reqDTO);
}
