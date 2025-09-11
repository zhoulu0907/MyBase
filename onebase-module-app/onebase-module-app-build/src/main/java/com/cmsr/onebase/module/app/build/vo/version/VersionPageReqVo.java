package com.cmsr.onebase.module.app.build.vo.version;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import lombok.Data;

/**
 * @ClassName VersionListReqVo
 * @Description TODO
 * @Author mickey
 * @Date 2025/8/11 19:52
 */
@Data
public class VersionPageReqVo extends PageParam {
    private Long applicationId;
}
