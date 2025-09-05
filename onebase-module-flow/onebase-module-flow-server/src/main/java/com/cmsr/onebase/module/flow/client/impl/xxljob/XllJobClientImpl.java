package com.cmsr.onebase.module.flow.client.impl.xxljob;

import com.cmsr.onebase.module.flow.client.JobClient;
import com.cmsr.onebase.module.flow.client.dto.JobCreateUpdateReqDTO;
import kong.unirest.core.Cookies;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/3 17:29
 */
@Setter
public class XllJobClientImpl implements JobClient {

    private static final String EXECUTOR_HANDLER = "timeFlowProcessJob";
    private static final int SUCCESS_CODE = 200;
    private static final int LOGIN_TIMEOUT_HOURS = 1;

    @Value("${flow.flow.url}")
    private String url;

    @Value("${flow.flow.jobGroup:1}")
    private Long jobGroup;

    @Value("${flow.flow.password}")
    private String password;

    private Cookies cookies;
    private LocalDateTime loginTime;

    public XllJobClientImpl() {
        Unirest.config().verifySsl(false);
    }

    /**
     * 登录XXL-JOB
     */
    public void longin() {
        HttpResponse<Map> response = Unirest.post(url + "/login")
                .field("userName", "admin")
                .field("password", password)
                .asObject(Map.class);

        executeRequest(response, "登录失败");

        this.cookies = response.getCookies();
        this.loginTime = LocalDateTime.now();
    }

    /**
     * 确保已登录
     */
    private void ensureLogin() {
        if (cookies == null ||
                loginTime == null ||
                LocalDateTime.now().isAfter(loginTime.plusHours(LOGIN_TIMEOUT_HOURS))) {
            longin();
        }
    }

    /**
     * 执行HTTP请求并处理通用错误
     */
    private HttpResponse<Map> executeRequest(HttpResponse<Map> response, String errorMessage) {
        if (response.getStatus() != SUCCESS_CODE) {
            throw new RuntimeException(errorMessage + ": HTTP " + response.getStatus() + " - " + response.getBody());
        }

        Map<String, Object> body = response.getBody();
        if (MapUtils.getInteger(body, "code") != SUCCESS_CODE) {
            throw new RuntimeException(errorMessage + ": " + body);
        }

        return response;
    }


    /**
     * 根据processId查找任务并获取其ID
     */
    private Long getJobIdByProcessId(Long processId) {
        Map<String, Object> jobData = findJob(processId);
        if (jobData == null) {
            throw new RuntimeException("任务不存在: " + processId);
        }
        return MapUtils.getLong(jobData, "id");
    }


    @Override
    public void createJob(JobCreateUpdateReqDTO reqDTO) {
        ensureLogin();
        HttpResponse<Map> response = Unirest.post(url + "/jobinfo/add")
                .fields(toMap(reqDTO))
                .cookie(cookies)
                .asObject(Map.class);
        executeRequest(response, "创建任务失败");
    }

    @Override
    public void updateJob(JobCreateUpdateReqDTO reqDTO) {
        Long jobId = getJobIdByProcessId(reqDTO.getProcessId());
        Map<String, Object> newData = toMap(reqDTO);
        newData.put("id", jobId);
        ensureLogin();
        HttpResponse<Map> response = Unirest.post(url + "/jobinfo/update")
                .fields(newData)
                .cookie(cookies)
                .asObject(Map.class);
        executeRequest(response, "更新任务失败");
    }

    @Override
    public void startJob(Long processId) {
        Long jobId = getJobIdByProcessId(processId);

        ensureLogin();
        HttpResponse<Map> response = Unirest.post(url + "/jobinfo/start")
                .field("id", jobId)
                .cookie(cookies)
                .asObject(Map.class);
        executeRequest(response, "启动任务失败");
    }

    @Override
    public void stopJob(Long processId) {
        Long jobId = getJobIdByProcessId(processId);

        ensureLogin();
        HttpResponse<Map> response = Unirest.post(url + "/jobinfo/stop")
                .field("id", jobId)
                .cookie(cookies)
                .asObject(Map.class);
        executeRequest(response, "停止任务失败");
    }

    @Override
    public void deleteJob(Long processId) {
        Long jobId = getJobIdByProcessId(processId);

        ensureLogin();
        HttpResponse<Map> response = Unirest.post(url + "/jobinfo/remove")
                .field("id", jobId)
                .cookie(cookies)
                .asObject(Map.class);
        executeRequest(response, "删除任务失败");
    }

    /**
     * 根据processId查找任务
     */
    public Map<String, Object> findJob(Long processId) {
        Map<String, Object> queryMap = createJobQueryMap(processId);
        ensureLogin();
        HttpResponse<Map> response = Unirest.post(url + "/jobinfo/pageList")
                .fields(queryMap)
                .cookie(cookies)
                .asObject(Map.class);
        Map responseBody = response.getBody();
        int recordsTotal = MapUtils.getIntValue(responseBody, "recordsTotal", 0);
        if (recordsTotal == 0) {
            return null;
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }

        return data.get(0);
    }

    /**
     * 创建任务查询参数
     */
    private Map<String, Object> createJobQueryMap(Long processId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("jobGroup", jobGroup);
        queryMap.put("triggerStatus", -1);
        queryMap.put("jobDesc", "");
        queryMap.put("executorHandler", EXECUTOR_HANDLER);
        queryMap.put("author", processId);
        queryMap.put("start", 0);
        queryMap.put("length", 10);
        return queryMap;
    }

    /**
     * 将DTO转换为请求参数Map
     */
    private Map<String, Object> toMap(JobCreateUpdateReqDTO reqDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobGroup", jobGroup);
        map.put("jobDesc", reqDTO.getProcessId());
        map.put("author", reqDTO.getProcessId());
        map.put("scheduleType", reqDTO.getScheduleType().getValue());
        map.put("scheduleConf", reqDTO.getScheduleConf());
        map.put("glueType", "BEAN");
        map.put("executorHandler", EXECUTOR_HANDLER);
        map.put("executorParam", reqDTO.getExecutorParam());
        map.put("executorRouteStrategy", "ROUND");
        map.put("misfireStrategy", "FIRE_ONCE_NOW");
        map.put("executorBlockStrategy", "SERIAL_EXECUTION");
        map.put("executorTimeout", 0);
        map.put("executorFailRetryCount", 0);
        map.put("glueRemark", "");
        return map;
    }
}
