package com.cmsr.onebase.framework.dolphins;

import com.cmsr.onebase.framework.dolphins.api.ProjectApi;
import com.cmsr.onebase.framework.dolphins.dto.model.ProjectDTO;
import com.cmsr.onebase.framework.dolphins.dto.request.ProjectCreateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.request.ProjectUpdateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.response.*;
import com.cmsr.onebase.framework.dolphins.config.DolphinSchedulerProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import retrofit2.Response;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.Socket;
import java.util.Optional;

/**
 * ProjectApi 真实环境集成测试
 *
 * 测试目标：
 * 1. 验证接口连通性
 * 2. 验证请求体和响应体字段映射与类型（关键字段）
 * 3. 不使用 Mock，真实请求与响应
 * 4. 完整业务流（创建→查询→更新→分页→列表→授权相关→删除）
 *
 * 运行前准备：
 * - 在 src/test/resources/application-test.yml 配置 onebase.dolphinscheduler.baseUrl 与 token
 * - 如需跑授权相关接口，请设置 test.user-id（可在 yml 或 -Dtest.user-id=xxx 注入）
 *
 * 提示：接口返回结构通常包含 code/msg/data，成功 code 多为 0。
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectApiTest {

    @Resource
    private ProjectApi projectApi;

    @Value("${test.user-id:}")
    private String testUserIdProp;

    @Resource
    private DolphinSchedulerProperties dsProps;

    private static Long createdProjectCode;
    private static String createdProjectName;


    @Test
    @Order(1)
    public void test_01_createProject() throws Exception {
        ProjectCreateRequestDTO req = new ProjectCreateRequestDTO();
        createdProjectName = "it-proj-" + System.currentTimeMillis();
        req.setProjectName(createdProjectName);
        req.setDescription("集成测试创建的项目");

        Response<ProjectCreateResponseDTO> resp = projectApi.createProject(req).execute();
        Assertions.assertNotNull(resp, "HTTP 响应不能为空");
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        ProjectCreateResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        ProjectDTO proj = body.getData();
        Assertions.assertNotNull(proj, "data 不应为空");
        // 关键字段类型与映射校验
        Assertions.assertNotNull(proj.getId(), "id 不能为空");
        Assertions.assertNotNull(proj.getCode(), "code 不能为空");
        Assertions.assertEquals(createdProjectName, proj.getName(), "name 应与请求一致");
        // 可为空字段不进行强制断言：description/createTime/updateTime/perm/defCount 等

        createdProjectCode = proj.getCode();
        log.info("创建项目成功 code={} name={}", createdProjectCode, createdProjectName);
    }

    @BeforeAll
    void checkConnectivityOrSkip() {
        // 在受限网络（CI、公司内网）环境下，先做 TCP 直连探测以决定是否跳过全部集成测试
        try {
            URI uri = URI.create(dsProps.getBaseUrl());
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80);
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
            }
            log.info("DolphinScheduler 可达: {}:{}", host, port);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "网络不可达，跳过 ProjectApi 集成测试: " + ex.getMessage());
        }
    }

    @Test
    @Order(2)
    public void test_02_queryProjectByCode() throws Exception {
        Assumptions.assumeTrue(createdProjectCode != null, "前置创建项目失败，跳过");
        Response<ProjectQueryResponseDTO> resp = projectApi.queryProjectByCode(createdProjectCode).execute();
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        ProjectQueryResponseDTO body = resp.body();
        Assertions.assertNotNull(body, "响应体不能为空");
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        ProjectDTO proj = body.getData();
        Assertions.assertNotNull(proj, "项目数据不应为空");
        Assertions.assertEquals(createdProjectCode, proj.getCode(), "返回的 code 应匹配");
        Assertions.assertEquals(createdProjectName, proj.getName(), "返回的 name 应匹配");
    }

    @Test
    @Order(3)
    public void test_03_updateProject() throws Exception {
        Assumptions.assumeTrue(createdProjectCode != null, "前置创建项目失败，跳过");
        ProjectUpdateRequestDTO req = new ProjectUpdateRequestDTO();
        String newName = createdProjectName + "-upd";
        req.setProjectName(newName);
        req.setDescription("集成测试更新项目");

        Response<ProjectUpdateResponseDTO> resp = projectApi.updateProject(createdProjectCode, req).execute();
        Assertions.assertTrue(resp.isSuccessful(), "HTTP 状态应成功");
        ProjectUpdateResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());

        ProjectDTO proj = body.getData();
        Assertions.assertNotNull(proj);
        Assertions.assertEquals(createdProjectCode, proj.getCode());
        Assertions.assertEquals(newName, proj.getName());
        // 更新成功后，后续使用更新后的名称
        createdProjectName = newName;
    }

    @Test
    @Order(4)
    public void test_04_queryProjectListPaging() throws Exception {
        Response<ProjectPageResponseDTO> resp = projectApi.queryProjectListPaging("", 1, 10).execute();
        Assertions.assertTrue(resp.isSuccessful());
        ProjectPageResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        Assertions.assertNotNull(body.getData(), "分页 data 不应为空");
        // 分页字段基本断言
        Assertions.assertNotNull(body.getData().getPageNo(), "pageNo 不应为空");
        Assertions.assertNotNull(body.getData().getPageSize(), "pageSize 不应为空");
        // 列表可以为空，但字段结构需要正确
    }

    @Test
    @Order(5)
    public void test_05_queryAllProjectList() throws Exception {
        Response<ProjectListResponseDTO> resp = projectApi.queryAllProjectList().execute();
        Assertions.assertTrue(resp.isSuccessful());
        ProjectListResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        // data 可为空或空数组，但结构需正确
        Assertions.assertNotNull(body.getData(), "data 列表不应为 null，可为 empty");
    }

    @Test
    @Order(6)
    public void test_06_queryAllProjectListForDependent() throws Exception {
        Response<ProjectListResponseDTO> resp = projectApi.queryAllProjectListForDependent().execute();
        Assertions.assertTrue(resp.isSuccessful());
        ProjectListResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        Assertions.assertNotNull(body.getData());
    }

    @Test
    @Order(7)
    public void test_07_queryProjectCreatedAndAuthorizedByUser() throws Exception {
        Response<ProjectListResponseDTO> resp = projectApi.queryProjectCreatedAndAuthorizedByUser().execute();
        Assertions.assertTrue(resp.isSuccessful());
        ProjectListResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        Assertions.assertNotNull(body.getData());
    }

    @Test
    @Order(8)
    public void test_08_queryAuthorizedProject() throws Exception {
        Optional<Integer> userIdOpt = parseUserId();
        Assumptions.assumeTrue(userIdOpt.isPresent(), "未提供 test.user-id，跳过授权项目查询");
        Response<ProjectListResponseDTO> resp = projectApi.queryAuthorizedProject(userIdOpt.get()).execute();
        Assertions.assertTrue(resp.isSuccessful());
        ProjectListResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        Assertions.assertNotNull(body.getData());
    }

    @Test
    @Order(9)
    public void test_09_queryUnauthorizedProject() throws Exception {
        Optional<Integer> userIdOpt = parseUserId();
        Assumptions.assumeTrue(userIdOpt.isPresent(), "未提供 test.user-id，跳过未授权项目查询");
        Response<ProjectListResponseDTO> resp = projectApi.queryUnauthorizedProject(userIdOpt.get()).execute();
        Assertions.assertTrue(resp.isSuccessful());
        ProjectListResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        Assertions.assertNotNull(body.getData());
    }

    @Test
    @Order(10)
    public void test_10_queryAuthorizedUser() throws Exception {
        Assumptions.assumeTrue(createdProjectCode != null, "前置创建项目失败，跳过");
        Response<UserListResponseDTO> resp = projectApi.queryAuthorizedUser(createdProjectCode).execute();
        Assertions.assertTrue(resp.isSuccessful());
        UserListResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        Assertions.assertNotNull(body.getData());
    }

    @Test
    @Order(11)
    public void test_11_deleteProject() throws Exception {
        Assumptions.assumeTrue(createdProjectCode != null, "前置创建项目失败，跳过");
        Response<ProjectDeleteResponseDTO> resp = projectApi.deleteProject(createdProjectCode).execute();
        Assertions.assertTrue(resp.isSuccessful());
        ProjectDeleteResponseDTO body = resp.body();
        Assertions.assertNotNull(body);
        assertSuccess(body.getCode(), body.getSuccess(), body.getMsg());
        // data 为布尔
        Assertions.assertNotNull(body.getData());
        log.info("删除项目 {} 结果: {}", createdProjectCode, body.getData());
    }

    // ------------------------
    // 辅助断言与工具方法
    // ------------------------
    /**
     * DolphinScheduler 通用成功判断：
     * - 优先 code == 0
     * - 其次 success == true
     * - 再次 msg 包含 "success"（部分版本返回）
     */
    private static void assertSuccess(Integer code, Boolean success, String msg) {
        boolean ok = (code != null && code == 0)
                || Boolean.TRUE.equals(success)
                || (msg != null && msg.toLowerCase().contains("success"));
        Assertions.assertTrue(ok, () -> "接口返回失败: code=" + code + ", success=" + success + ", msg=" + msg);
    }

    private Optional<Integer> parseUserId() {
        try {
            if (testUserIdProp == null || testUserIdProp.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(testUserIdProp.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
