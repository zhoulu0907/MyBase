package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorTableDef.FLOW_CONNECTOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * FlowConnectorRepository 单元测试
 * <p>
 * 测试连接器 Repository 的数据访问功能
 *
 * @author kanten
 * @since 2026-01-21
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FlowConnectorRepositoryTest {

    @Autowired
    private FlowConnectorRepository repository;

    private FlowConnectorDO testData;

    /**
     * 初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 清理测试数据
        QueryWrapper cleanupQuery = QueryWrapper.create()
                .where(FLOW_CONNECTOR.CONNECTOR_UUID.like("test-%"));
        repository.remove(cleanupQuery);

        // 创建测试数据
        testData = new FlowConnectorDO();
        testData.setConnectorUuid("test-connector-uuid");
        testData.setConnectorName("测试连接器");
        testData.setCode("TEST_CONNECTOR");
        testData.setTypeCode("HTTP");
        testData.setDescription("这是一个测试连接器");
        testData.setConfig("{}");
        testData.setConfigJson("{\"type\":\"object\",\"properties\":{\"action1\":{},\"action2\":{}}}");
        repository.save(testData);
    }

    /**
     * 测试根据 connectorUuid 查询连接器 - 成功场景
     */
    @Test
    void testSelectByConnectorUuid_Success() {
        // When
        FlowConnectorDO result = repository.selectByConnectorUuid("test-connector-uuid");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getConnectorUuid()).isEqualTo("test-connector-uuid");
        assertThat(result.getConnectorName()).isEqualTo("测试连接器");
        assertThat(result.getTypeCode()).isEqualTo("HTTP");
    }

    /**
     * 测试根据不存在的 connectorUuid 查询 - 返回 null
     */
    @Test
    void testSelectByConnectorUuid_NotFound() {
        // When
        FlowConnectorDO result = repository.selectByConnectorUuid("non-existent-uuid");

        // Then
        assertThat(result).isNull();
    }

    /**
     * 测试根据 null connectorUuid 查询 - 返回 null
     */
    @Test
    void testSelectByConnectorUuid_NullInput() {
        // When
        FlowConnectorDO result = repository.selectByConnectorUuid(null);

        // Then
        assertThat(result).isNull();
    }
}
