package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorHttpRepository;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * FlowConnectorHttpServiceImpl 单元测试
 * <p>
 * 测试 HTTP 连接器动作 Service 的业务逻辑
 *
 * @author zhoulu
 * @since 2026-01-17
 */
@ExtendWith(MockitoExtension.class)
class FlowConnectorHttpServiceImplTest {

    @Mock
    private FlowConnectorHttpRepository repository;

    @InjectMocks
    private FlowConnectorHttpServiceImpl service;

    private FlowConnectorHttpDO testData;
    private CreateHttpActionReqVO createReqVO;
    private UpdateHttpActionReqVO updateReqVO;
    private PageConnectorHttpReqVO pageReqVO;

    /**
     * 初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 创建测试DO对象
        testData = new FlowConnectorHttpDO();
        testData.setId(1L);
        testData.setApplicationId(1L);
        testData.setConnectorUuid("test-connector-uuid");
        testData.setHttpUuid("test-http-uuid");
        testData.setHttpName("测试HTTP动作");
        testData.setHttpCode("TEST_HTTP_ACTION");
        testData.setDescription("测试描述");
        testData.setRequestMethod("GET");
        testData.setRequestPath("/api/test");
        testData.setTimeout(5000);
        testData.setRetryCount(0);
        testData.setActiveStatus(1);
        testData.setSortOrder(0);

        // 创建创建请求VO
        createReqVO = new CreateHttpActionReqVO();
        createReqVO.setConnectorUuid("test-connector-uuid");
        createReqVO.setHttpName("新建HTTP动作");
        createReqVO.setRequestMethod("POST");
        createReqVO.setRequestPath("/api/create");

        // 创建更新请求VO
        updateReqVO = new UpdateHttpActionReqVO();
        updateReqVO.setId(1L);
        updateReqVO.setHttpName("更新HTTP动作");
        updateReqVO.setDescription("更新描述");

        // 创建分页查询请求VO
        pageReqVO = new PageConnectorHttpReqVO();
        pageReqVO.setConnectorUuid("test-connector-uuid");
        pageReqVO.setHttpName("测试");
    }

    /**
     * 测试创建HTTP动作 - 成功
     */
    @Test
    void testCreateHttpAction_Success() {
        // Given
        when(repository.save(any(FlowConnectorHttpDO.class))).thenAnswer(invocation -> {
            FlowConnectorHttpDO saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // When
        Long id = service.createHttpAction(createReqVO);

        // Then
        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(1L);
        verify(repository).save(any(FlowConnectorHttpDO.class));
    }

    /**
     * 测试创建HTTP动作 - 设置默认值
     */
    @Test
    void testCreateHttpAction_WithDefaultValues() {
        // Given - createReqVO 不设置 activeStatus 和 sortOrder
        when(repository.save(any(FlowConnectorHttpDO.class))).thenAnswer(invocation -> {
            FlowConnectorHttpDO saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // When
        service.createHttpAction(createReqVO);

        // Then - 验证默认值被设置
        verify(repository).save(argThat(doObj ->
                doObj.getActiveStatus() != null && doObj.getActiveStatus() == 1 &&
                        doObj.getSortOrder() != null && doObj.getSortOrder() == 0
        ));
    }

    /**
     * 测试创建HTTP动作 - 生成UUID
     */
    @Test
    void testCreateHttpAction_GeneratesUuid() {
        // Given
        when(repository.save(any(FlowConnectorHttpDO.class))).thenAnswer(invocation -> {
            FlowConnectorHttpDO saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // When
        service.createHttpAction(createReqVO);

        // Then - 验证UUID被生成且以"http-"开头
        verify(repository).save(argThat(doObj ->
                doObj.getHttpUuid() != null &&
                        doObj.getHttpUuid().startsWith("http-") &&
                        doObj.getHttpUuid().length() > 5
        ));
    }

    /**
     * 测试更新HTTP动作 - 成功
     */
    @Test
    void testUpdateHttpAction_Success() {
        // Given
        when(repository.getById(1L)).thenReturn(testData);
        when(repository.updateById(any(FlowConnectorHttpDO.class))).thenReturn(null);

        // When
        service.updateHttpAction(updateReqVO);

        // Then
        verify(repository).getById(1L);
        verify(repository).updateById(any(FlowConnectorHttpDO.class));
    }

    /**
     * 测试更新不存在的HTTP动作 - 抛出异常
     */
    @Test
    void testUpdateHttpAction_NotFound() {
        // Given
        when(repository.getById(999L)).thenReturn(null);

        updateReqVO.setId(999L);

        // When & Then
        assertThatThrownBy(() -> service.updateHttpAction(updateReqVO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP动作不存在");

        verify(repository, never()).updateById(any());
    }

    /**
     * 测试删除HTTP动作 - 软删除成功
     */
    @Test
    void testDeleteHttpAction_Success() {
        // Given
        when(repository.getById(1L)).thenReturn(testData);
        when(repository.updateById(any(FlowConnectorHttpDO.class))).thenReturn(null);

        // When
        service.deleteHttpAction(1L);

        // Then - 验证 activeStatus 被设置为 0
        verify(repository).getById(1L);
        verify(repository).updateById(argThat(doObj ->
                doObj.getId().equals(1L) && doObj.getActiveStatus() == 0
        ));
    }

    /**
     * 测试删除不存在的HTTP动作 - 抛出异常
     */
    @Test
    void testDeleteHttpAction_NotFound() {
        // Given
        when(repository.getById(999L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.deleteHttpAction(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP动作不存在");

        verify(repository).getById(999L);
        verify(repository, never()).updateById(any());
    }

    /**
     * 测试获取HTTP动作详情 - 成功
     */
    @Test
    void testGetHttpAction_Success() {
        // Given
        when(repository.getById(1L)).thenReturn(testData);

        // When
        HttpActionVO result = service.getHttpAction(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getHttpName()).isEqualTo("测试HTTP动作");
        assertThat(result.getHttpCode()).isEqualTo("TEST_HTTP_ACTION");
        verify(repository).getById(1L);
    }

    /**
     * 测试获取不存在的HTTP动作详情 - 抛出异常
     */
    @Test
    void testGetHttpAction_NotFound() {
        // Given
        when(repository.getById(999L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.getHttpAction(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP动作不存在");

        verify(repository).getById(999L);
    }

    /**
     * 测试分页查询HTTP动作列表 - 成功
     */
    @Test
    void testGetHttpActionPage_Success() {
        // Given
        com.mybatisflex.core.paginate.Page<FlowConnectorHttpDO> mockPage =
                new com.mybatisflex.core.paginate.Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testData));
        mockPage.setTotalRow(1L);

        when(repository.page(eq(com.mybatisflex.core.paginate.Page.of(1, 10)), any(com.mybatisflex.core.query.QueryWrapper.class))).thenReturn(mockPage);

        // When
        PageResult<HttpActionVO> result = service.getHttpActionPage(pageReqVO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getList().get(0).getHttpName()).isEqualTo("测试HTTP动作");

        verify(repository).page(eq(com.mybatisflex.core.paginate.Page.of(1, 10)), any(com.mybatisflex.core.query.QueryWrapper.class));
    }

    /**
     * 测试分页查询 - 空结果
     */
    @Test
    void testGetHttpActionPage_EmptyResult() {
        // Given
        com.mybatisflex.core.paginate.Page<FlowConnectorHttpDO> emptyPage =
                new com.mybatisflex.core.paginate.Page<>(1, 10);
        emptyPage.setRecords(List.of());
        emptyPage.setTotalRow(0L);

        when(repository.page(eq(com.mybatisflex.core.paginate.Page.of(1, 10)), any(com.mybatisflex.core.query.QueryWrapper.class))).thenReturn(emptyPage);

        // When
        PageResult<HttpActionVO> result = service.getHttpActionPage(pageReqVO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0L);
    }

    /**
     * 测试分页查询 - DO 正确转换为 VO
     */
    @Test
    void testGetHttpActionPage_DoToVoConversion() {
        // Given
        com.mybatisflex.core.paginate.Page<FlowConnectorHttpDO> mockPage =
                new com.mybatisflex.core.paginate.Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testData));
        mockPage.setTotalRow(1L);

        when(repository.page(eq(com.mybatisflex.core.paginate.Page.of(1, 10)), any(com.mybatisflex.core.query.QueryWrapper.class))).thenReturn(mockPage);

        // When
        PageResult<HttpActionVO> result = service.getHttpActionPage(pageReqVO);

        // Then - 验证字段正确转换
        HttpActionVO vo = result.getList().get(0);
        assertThat(vo.getId()).isEqualTo(testData.getId());
        assertThat(vo.getApplicationId()).isEqualTo(testData.getApplicationId());
        assertThat(vo.getConnectorUuid()).isEqualTo(testData.getConnectorUuid());
        assertThat(vo.getHttpUuid()).isEqualTo(testData.getHttpUuid());
        assertThat(vo.getHttpName()).isEqualTo(testData.getHttpName());
        assertThat(vo.getHttpCode()).isEqualTo(testData.getHttpCode());
        assertThat(vo.getRequestMethod()).isEqualTo(testData.getRequestMethod());
        assertThat(vo.getRequestPath()).isEqualTo(testData.getRequestPath());
    }

    /**
     * 测试创建HTTP动作 - 带所有字段
     */
    @Test
    void testCreateHttpAction_WithAllFields() {
        // Given - 设置所有字段
        createReqVO.setHttpCode("FULL_TEST");
        createReqVO.setDescription("完整测试");
        createReqVO.setRequestBodyType("JSON");
        createReqVO.setRequestBodyTemplate("{\"test\":\"data\"}");
        createReqVO.setTimeout(10000);
        createReqVO.setRetryCount(3);
        createReqVO.setSortOrder(5);

        when(repository.save(any(FlowConnectorHttpDO.class))).thenAnswer(invocation -> {
            FlowConnectorHttpDO saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        // When
        Long id = service.createHttpAction(createReqVO);

        // Then
        assertThat(id).isEqualTo(2L);
        verify(repository).save(argThat(doObj ->
                doObj.getHttpCode().equals("FULL_TEST") &&
                        doObj.getTimeout() == 10000 &&
                        doObj.getRetryCount() == 3 &&
                        doObj.getSortOrder() == 5
        ));
    }

    /**
     * 测试更新HTTP动作 - 部分字段更新
     */
    @Test
    void testUpdateHttpAction_PartialUpdate() {
        // Given - 只更新部分字段
        updateReqVO.setHttpName("只更新名称");
        updateReqVO.setActiveStatus(0);

        when(repository.getById(1L)).thenReturn(testData);
        when(repository.updateById(any(FlowConnectorHttpDO.class))).thenReturn(null);

        // When
        service.updateHttpAction(updateReqVO);

        // Then
        verify(repository).updateById(argThat(doObj ->
                doObj.getId().equals(1L) &&
                        doObj.getHttpName().equals("只更新名称") &&
                        doObj.getActiveStatus() == 0
        ));
    }
}
