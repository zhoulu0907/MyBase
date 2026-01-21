package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef.FLOW_CONNECTOR_HTTP;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * FlowConnectorHttpRepository 单元测试
 * <p>
 * 测试 HTTP 连接器动作配置 Repository 的数据访问功能
 *
 * @author zhoulu
 * @since 2026-01-17
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FlowConnectorHttpRepositoryTest {

    @Autowired
    private FlowConnectorHttpRepository repository;

    private FlowConnectorHttpDO testData;

    /**
     * 初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 注意：applicationId 是通过继承 BaseAppEntity 获得的，在查询时不直接使用
        // 清理测试数据 - 使用 deleted 字段进行软删除清理
        QueryWrapper cleanupQuery = QueryWrapper.create()
                .where(FLOW_CONNECTOR_HTTP.HTTP_UUID.like("test-%"));
        repository.remove(cleanupQuery);

        // 创建测试数据
        testData = new FlowConnectorHttpDO();
        // applicationId 由框架自动设置（从当前上下文获取）
        testData.setConnectorUuid("test-connector-uuid");
        testData.setHttpUuid("test-http-uuid");
        testData.setHttpName("测试HTTP动作");
        testData.setHttpCode("TEST_HTTP_ACTION");
        testData.setDescription("这是一个测试HTTP动作");
        testData.setRequestMethod("GET");
        testData.setRequestPath("/api/test");
        testData.setRequestBodyType("JSON");
        testData.setRequestBodyTemplate("{\"test\":\"data\"}");
        testData.setAuthType("NONE");
        testData.setTimeout(5000);
        testData.setRetryCount(0);
        testData.setActiveStatus(1);
        testData.setSortOrder(0);

        // 保存测试数据
        repository.save(testData);
    }

    /**
     * 测试根据应用ID和UUID查询HTTP动作配置
     */
    @Test
    void testFindByApplicationAndUuid_Success() {
        // When
        FlowConnectorHttpDO result = repository.findByApplicationAndUuid(null, "test-http-uuid");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getHttpUuid()).isEqualTo("test-http-uuid");
        assertThat(result.getHttpName()).isEqualTo("测试HTTP动作");
        assertThat(result.getRequestMethod()).isEqualTo("GET");
    }

    /**
     * 测试查询不存在的HTTP动作返回null
     */
    @Test
    void testFindByApplicationAndUuid_NotFound() {
        // When
        FlowConnectorHttpDO result = repository.findByApplicationAndUuid(null, "non-existent-uuid");

        // Then
        assertThat(result).isNull();
    }

    /**
     * 测试查询禁用状态的HTTP动作返回null
     */
    @Test
    void testFindByApplicationAndUuid_InactiveStatus() {
        // Given - 设置为禁用状态
        testData.setActiveStatus(0);
        repository.updateById(testData);

        // When
        FlowConnectorHttpDO result = repository.findByApplicationAndUuid(null, "test-http-uuid");

        // Then
        assertThat(result).isNull();
    }

    /**
     * 测试根据连接器UUID查询HTTP动作列表
     */
    @Test
    void testFindByConnectorUuid_Success() {
        // Given - 添加额外的测试数据
        FlowConnectorHttpDO extraData = new FlowConnectorHttpDO();
        extraData.setApplicationId(1L);
        extraData.setConnectorUuid("test-connector-uuid");
        extraData.setHttpUuid("extra-http-uuid");
        extraData.setHttpName("额外HTTP动作");
        extraData.setRequestMethod("POST");
        extraData.setRequestPath("/api/extra");
        extraData.setActiveStatus(1);
        extraData.setSortOrder(1);
        repository.save(extraData);

        // When
        List<FlowConnectorHttpDO> result = repository.findByConnectorUuid("test-connector-uuid");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getHttpUuid()).isEqualTo("test-http-uuid");
        assertThat(result.get(1).getHttpUuid()).isEqualTo("extra-http-uuid");
    }

    /**
     * 测试根据连接器UUID查询空列表
     */
    @Test
    void testFindByConnectorUuid_EmptyList() {
        // When
        List<FlowConnectorHttpDO> result = repository.findByConnectorUuid("non-existent-connector");

        // Then
        assertThat(result).isEmpty();
    }

    /**
     * 测试排序按照sortOrder升序
     */
    @Test
    void testFindByConnectorUuid_OrderedBySortOrder() {
        // Given - 添加多个测试数据，顺序打乱
        FlowConnectorHttpDO data1 = new FlowConnectorHttpDO();
        data1.setApplicationId(1L);
        data1.setConnectorUuid("order-test-connector");
        data1.setHttpUuid("order-1");
        data1.setHttpName("动作1");
        data1.setRequestMethod("GET");
        data1.setRequestPath("/api/1");
        data1.setActiveStatus(1);
        data1.setSortOrder(2);
        repository.save(data1);

        FlowConnectorHttpDO data2 = new FlowConnectorHttpDO();
        data2.setApplicationId(1L);
        data2.setConnectorUuid("order-test-connector");
        data2.setHttpUuid("order-2");
        data2.setHttpName("动作2");
        data2.setRequestMethod("GET");
        data2.setRequestPath("/api/2");
        data2.setActiveStatus(1);
        data2.setSortOrder(1);
        repository.save(data2);

        FlowConnectorHttpDO data3 = new FlowConnectorHttpDO();
        data3.setApplicationId(1L);
        data3.setConnectorUuid("order-test-connector");
        data3.setHttpUuid("order-3");
        data3.setHttpName("动作3");
        data3.setRequestMethod("GET");
        data3.setRequestPath("/api/3");
        data3.setActiveStatus(1);
        data3.setSortOrder(3);
        repository.save(data3);

        // When
        List<FlowConnectorHttpDO> result = repository.findByConnectorUuid("order-test-connector");

        // Then - 应该按 sortOrder 升序排列
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getSortOrder()).isEqualTo(1);
        assertThat(result.get(1).getSortOrder()).isEqualTo(2);
        assertThat(result.get(2).getSortOrder()).isEqualTo(3);
    }

    /**
     * 测试根据应用ID和连接器UUID查询HTTP动作列表
     */
    @Test
    void testFindByApplicationAndConnectorUuid_Success() {
        // Given - 添加不同应用的数据
        FlowConnectorHttpDO otherAppData = new FlowConnectorHttpDO();
        otherAppData.setConnectorUuid("test-connector-uuid");
        otherAppData.setHttpUuid("other-app-uuid");
        otherAppData.setHttpName("其他应用的动作");
        otherAppData.setRequestMethod("GET");
        otherAppData.setRequestPath("/api/other");
        otherAppData.setActiveStatus(1);
        repository.save(otherAppData);

        // When - 查询应用的动作
        List<FlowConnectorHttpDO> result = repository.findByApplicationAndConnectorUuid(null, "test-connector-uuid");

        // Then
        assertThat(result).hasSize(2); // 两个测试数据
    }

    /**
     * 测试根据应用ID和HTTP动作编码查询
     */
    @Test
    void testFindByApplicationAndCode_Success() {
        // When
        FlowConnectorHttpDO result = repository.findByApplicationAndCode(null, "TEST_HTTP_ACTION");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getHttpCode()).isEqualTo("TEST_HTTP_ACTION");
        assertThat(result.getHttpName()).isEqualTo("测试HTTP动作");
    }

    /**
     * 测试根据不存在的编码查询返回null
     */
    @Test
    void testFindByApplicationAndCode_NotFound() {
        // When
        FlowConnectorHttpDO result = repository.findByApplicationAndCode(null, "NON_EXISTENT_CODE");

        // Then
        assertThat(result).isNull();
    }

    /**
     * 测试查询忽略禁用状态的记录
     */
    @Test
    void testFindByApplicationAndCode_InactiveStatus() {
        // Given - 设置为禁用状态
        testData.setActiveStatus(0);
        repository.updateById(testData);

        // When
        FlowConnectorHttpDO result = repository.findByApplicationAndCode(null, "TEST_HTTP_ACTION");

        // Then
        assertThat(result).isNull();
    }

    /**
     * 测试基础CRUD操作 - 保存
     */
    @Test
    void testSave_Success() {
        // Given
        FlowConnectorHttpDO newData = new FlowConnectorHttpDO();
        newData.setApplicationId(1L);
        newData.setConnectorUuid("new-connector");
        newData.setHttpUuid("new-http-uuid");
        newData.setHttpName("新建HTTP动作");
        newData.setRequestMethod("POST");
        newData.setRequestPath("/api/new");
        newData.setActiveStatus(1);

        // When
        repository.save(newData);

        // Then
        FlowConnectorHttpDO saved = repository.findByApplicationAndUuid(1L, "new-http-uuid");
        assertThat(saved).isNotNull();
        assertThat(saved.getHttpName()).isEqualTo("新建HTTP动作");
    }

    /**
     * 测试基础CRUD操作 - 更新
     */
    @Test
    void testUpdateById_Success() {
        // Given
        testData.setHttpName("更新后的HTTP动作");
        testData.setDescription("更新后的描述");

        // When
        repository.updateById(testData);

        // Then
        FlowConnectorHttpDO updated = repository.findByApplicationAndUuid(1L, "test-http-uuid");
        assertThat(updated.getHttpName()).isEqualTo("更新后的HTTP动作");
        assertThat(updated.getDescription()).isEqualTo("更新后的描述");
    }

    /**
     * 测试基础CRUD操作 - 根据ID查询
     */
    @Test
    void testGetById_Success() {
        // When
        FlowConnectorHttpDO result = repository.getById(testData.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testData.getId());
        assertThat(result.getHttpUuid()).isEqualTo("test-http-uuid");
    }

    /**
     * 测试软删除 - 通过activeStatus=0实现
     */
    @Test
    void testSoftDelete_Success() {
        // Given - 设置为禁用状态
        testData.setActiveStatus(0);
        repository.updateById(testData);

        // When - 查询方法不应该返回禁用的数据
        FlowConnectorHttpDO result = repository.findByApplicationAndUuid(1L, "test-http-uuid");

        // Then
        assertThat(result).isNull();

        // 但通过 getById 仍然可以查到（软删除）
        FlowConnectorHttpDO deleted = repository.getById(testData.getId());
        assertThat(deleted).isNotNull();
        assertThat(deleted.getActiveStatus()).isEqualTo(0);
    }
}