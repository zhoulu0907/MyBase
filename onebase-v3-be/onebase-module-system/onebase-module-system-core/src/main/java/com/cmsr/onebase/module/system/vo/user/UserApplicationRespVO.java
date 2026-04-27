package com.cmsr.onebase.module.system.vo.user;


import com.cmsr.onebase.module.app.api.app.dto.TagVO;
import com.cmsr.onebase.module.system.vo.corp.CorpAppVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserApplicationRespVO {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "姓名")
    private String nickName;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "来源")
    private String createSource;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "授权应用")
    private List<UserAppVO> userApplicationList;

}
