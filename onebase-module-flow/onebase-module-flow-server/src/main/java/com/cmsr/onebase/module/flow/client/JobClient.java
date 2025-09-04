package com.cmsr.onebase.module.flow.client;

import com.cmsr.onebase.module.flow.client.dto.JobCreateUpdateReqDTO;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:07
 */
//@Component
public interface JobClient {

    void createJob(JobCreateUpdateReqDTO reqDTO);

    void updateJob(JobCreateUpdateReqDTO reqDTO);

    void startJob(Long processId);

    void stopJob(Long processId);

    void deleteJob(Long processId);
}
