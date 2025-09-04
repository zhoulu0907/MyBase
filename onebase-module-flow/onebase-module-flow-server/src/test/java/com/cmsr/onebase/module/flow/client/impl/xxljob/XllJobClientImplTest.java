package com.cmsr.onebase.module.flow.client.impl.xxljob;

import com.cmsr.onebase.module.flow.client.dto.JobCreateUpdateReqDTO;
import com.cmsr.onebase.module.flow.client.dto.JobScheduleTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/3 17:51
 */
public class XllJobClientImplTest {

    @Test
    public void createJob() {
        XllJobClientImpl xllJobClient = new XllJobClientImpl();
        xllJobClient.setUrl("http://localhost:9088/xxl-job-admin");
        xllJobClient.setPassword("123456");
        xllJobClient.setJobGroup(2L);

        JobCreateUpdateReqDTO reqDTO = new JobCreateUpdateReqDTO();
        reqDTO.setProcessId(10000L);
        reqDTO.setScheduleType(JobScheduleTypeEnum.FIX_RATE);
        reqDTO.setScheduleConf("500");
        reqDTO.setExecutorParam("{\"processId\":1}");
        xllJobClient.createJob(reqDTO);
    }

    @Test
    public void findJob() {
        XllJobClientImpl xllJobClient = new XllJobClientImpl();
        xllJobClient.setUrl("http://localhost:9088/xxl-job-admin");
        xllJobClient.setPassword("123456");
        xllJobClient.setJobGroup(2L);

        Map<String, Object> job = xllJobClient.findJob(10000L);
        System.out.println(job);
    }

    @Test
    public void updateJob() {
        XllJobClientImpl xllJobClient = new XllJobClientImpl();
        xllJobClient.setUrl("http://localhost:9088/xxl-job-admin");
        xllJobClient.setPassword("123456");
        xllJobClient.setJobGroup(2L);

        JobCreateUpdateReqDTO reqDTO = new JobCreateUpdateReqDTO();
        reqDTO.setProcessId(10000L);
        reqDTO.setScheduleType(JobScheduleTypeEnum.FIX_RATE);
        reqDTO.setScheduleConf("500");
        reqDTO.setExecutorParam("{\"processId\":1}");
        xllJobClient.updateJob(reqDTO);
    }

    @Test
    public void startJob() {
        XllJobClientImpl xllJobClient = new XllJobClientImpl();
        xllJobClient.setUrl("http://localhost:9088/xxl-job-admin");
        xllJobClient.setPassword("123456");
        xllJobClient.setJobGroup(2L);

        xllJobClient.startJob(10000L);
    }

    @Test
    public void stopJob() {
        XllJobClientImpl xllJobClient = new XllJobClientImpl();
        xllJobClient.setUrl("http://localhost:9088/xxl-job-admin");
        xllJobClient.setPassword("123456");
        xllJobClient.setJobGroup(2L);

        xllJobClient.stopJob(10000L);
    }

    @Test
    public void deleteJob() {
        XllJobClientImpl xllJobClient = new XllJobClientImpl();
        xllJobClient.setUrl("http://localhost:9088/xxl-job-admin");
        xllJobClient.setPassword("123456");
        xllJobClient.setJobGroup(2L);
        xllJobClient.deleteJob(10000L);
    }
}