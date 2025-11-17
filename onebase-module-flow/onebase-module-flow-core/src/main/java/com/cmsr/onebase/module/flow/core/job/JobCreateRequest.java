package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.module.flow.core.flow.RemoteCallRequest;
import lombok.Data;

/**
 * {"startTime":"2025-10-14 00:00:00","endTime":"2125-10-14 00:00:00","crontab":"0 0 9 25 10 ? 2025","timezoneId":"Asia/Shanghai"}
 *
 * @Author：huangjie
 * @Date：2025/9/22 12:52
 */
@Data
public class JobCreateRequest {

    private String startTime;

    private String endTime;

    private String crontab;

    private RemoteCallRequest remoteCallRequest;

}
