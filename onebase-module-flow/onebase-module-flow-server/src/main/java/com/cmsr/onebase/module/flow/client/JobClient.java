package com.cmsr.onebase.module.flow.client;

import com.cmsr.onebase.module.flow.client.dto.JobCreateUpdateReqDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:07
 */
@Component
public class JobClient {

    @Value("${flow.job.token}")
    private String token;

    @Value("${flow.job.url}")
    private String url;

    public String createJob(JobCreateUpdateReqDTO reqDTO) {
        return null;
    }
}
