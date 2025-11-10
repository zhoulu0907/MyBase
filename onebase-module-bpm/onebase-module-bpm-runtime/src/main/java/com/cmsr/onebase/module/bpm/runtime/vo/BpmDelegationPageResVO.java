package com.cmsr.onebase.module.bpm.runtime.vo;

import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "流程代理列表VO")
@Data
public class BpmDelegationPageResVO {
    /**
     * 代理人信息
     */
    private UserBasicInfoVO delegate;
    /**
     * 被代理人信息
     */
    private UserBasicInfoVO principal;
    /**
     * 代理时间
     */
    private String delegateTime;
    /**
     * 代理状态
     */
    private String delegateStatus;
    /**
     * 创建人
     */
    private UserBasicInfoVO creator;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
