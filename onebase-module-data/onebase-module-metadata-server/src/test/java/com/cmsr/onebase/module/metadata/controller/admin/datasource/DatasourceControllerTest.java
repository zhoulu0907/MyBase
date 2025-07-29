package com.cmsr.onebase.module.metadata.controller.admin.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.test.core.ut.BaseDbAndRedisUnitTest;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.*;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 数据源管理接口单元测试
 *
 * @author bty418
 * @date 2025-01-25
 */
@WebMvcTest(DatasourceController.class)
class DatasourceControllerTest extends BaseDbAndRedisUnitTest {

    private MockMvc mockMvc;

    @MockBean
    private MetadataDatasourceService datasourceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetDatasourceTypes() throws Exception {
        // 准备测试数据
        List<DatasourceTypeRespVO> mockTypes = Arrays.asList(
            createDatasourceTypeRespVO("MYSQL", "MySQL数据库", "支持MySQL 5.7及以上版本", 3306),
            createDatasourceTypeRespVO("POSTGRESQL", "PostgreSQL数据库", "支持PostgreSQL 9.6及以上版本", 5432)
        );

        // Mock服务方法
        when(datasourceService.getDatasourceTypes()).thenReturn(mockTypes);

        // 执行测试
        mockMvc.perform(get("/metadata/datasource/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].datasourceType").value("MYSQL"))
                .andExpect(jsonPath("$.data[0].displayName").value("MySQL数据库"))
                .andExpect(jsonPath("$.data[1].datasourceType").value("POSTGRESQL"))
                .andExpect(jsonPath("$.data[1].displayName").value("PostgreSQL数据库"));
    }

    @Test
    void testGetTablesByDatasourceId() throws Exception {
        // 准备测试数据
        List<TableInfoRespVO> mockTables = Arrays.asList(
            createTableInfoRespVO("users", "用户表", "系统用户信息表", "TABLE", "public", 1250L),
            createTableInfoRespVO("orders", "订单表", "订单信息表", "TABLE", "public", 3420L)
        );

        // Mock服务方法
        when(datasourceService.getTablesByDatasourceId(eq(1001L), anyString(), anyString()))
                .thenReturn(mockTables);

        // 执行测试
        mockMvc.perform(get("/metadata/datasource/1001/tables")
                        .param("schemaName", "public")
                        .param("keyword", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].tableName").value("users"))
                .andExpect(jsonPath("$.data[0].displayName").value("用户表"))
                .andExpect(jsonPath("$.data[1].tableName").value("orders"))
                .andExpect(jsonPath("$.data[1].displayName").value("订单表"));
    }

    @Test
    void testGetColumnsByTableName() throws Exception {
        // 准备测试数据
        List<ColumnInfoRespVO> mockColumns = Arrays.asList(
            createColumnInfoRespVO("id", "主键ID", "BIGINT", 20, 0, false, true, true, null, "主键ID", 1),
            createColumnInfoRespVO("username", "用户名", "VARCHAR", 50, 0, false, false, false, "", "用户登录名", 2)
        );

        // Mock服务方法
        when(datasourceService.getColumnsByTableName(eq(1001L), eq("users"), anyString()))
                .thenReturn(mockColumns);

        // 执行测试
        mockMvc.perform(get("/metadata/datasource/1001/tables/users/columns")
                        .param("schemaName", "public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].columnName").value("id"))
                .andExpect(jsonPath("$.data[0].isPrimaryKey").value(true))
                .andExpect(jsonPath("$.data[1].columnName").value("username"))
                .andExpect(jsonPath("$.data[1].isPrimaryKey").value(false));
    }

    @Test
    void testCreateDatasource() throws Exception {
        // 准备测试数据
        DatasourceSaveReqVO reqVO = createDatasourceSaveReqVO();
        Long mockId = 1001L;

        // Mock服务方法
        when(datasourceService.createDatasource(any(DatasourceSaveReqVO.class))).thenReturn(mockId);

        // 执行测试
        mockMvc.perform(post("/metadata/datasource/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1001));
    }

    @Test
    void testUpdateDatasource() throws Exception {
        // 准备测试数据
        DatasourceSaveReqVO reqVO = createDatasourceSaveReqVO();
        reqVO.setId(1001L);

        // 执行测试
        mockMvc.perform(put("/metadata/datasource/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testDeleteDatasource() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/metadata/datasource/delete")
                        .param("id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testGetDatasource() throws Exception {
        // 准备测试数据
        MetadataDatasourceDO mockDatasource = createMetadataDatasourceDO();

        // Mock服务方法
        when(datasourceService.getDatasource(1001L)).thenReturn(mockDatasource);

        // 执行测试
        mockMvc.perform(get("/metadata/datasource/get")
                        .param("id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetDatasourcePage() throws Exception {
        // 准备测试数据
        PageResult<MetadataDatasourceDO> mockPageResult = new PageResult<>();
        mockPageResult.setTotal(10L);
        mockPageResult.setList(Arrays.asList(createMetadataDatasourceDO()));

        // Mock服务方法
        when(datasourceService.getDatasourcePage(any(DatasourcePageReqVO.class))).thenReturn(mockPageResult);

        // 执行测试
        mockMvc.perform(get("/metadata/datasource/page")
                        .param("appId", "12345")
                        .param("pageNo", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testTestConnection() throws Exception {
        // 准备测试数据
        DatasourceTestConnectionReqVO reqVO = new DatasourceTestConnectionReqVO();
        reqVO.setDatasourceType("POSTGRESQL");
        // 设置其他必要字段...

        DatasourceTestConnectionRespVO mockResp = new DatasourceTestConnectionRespVO();
        mockResp.setSuccess(true);
        mockResp.setConnectionTime(150L);

        // Mock服务方法
        when(datasourceService.testConnection(any(DatasourceTestConnectionReqVO.class))).thenReturn(mockResp);

        // 执行测试
        mockMvc.perform(post("/metadata/datasource/test-connection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.connectionTime").value(150));
    }

    // 辅助方法 - 创建测试数据
    private DatasourceTypeRespVO createDatasourceTypeRespVO(String type, String displayName, String description, Integer defaultPort) {
        DatasourceTypeRespVO vo = new DatasourceTypeRespVO();
        vo.setDatasourceType(type);
        vo.setDisplayName(displayName);
        vo.setDescription(description);
        vo.setDefaultPort(defaultPort);
        return vo;
    }

    private TableInfoRespVO createTableInfoRespVO(String tableName, String displayName, String tableComment, 
                                                  String tableType, String schemaName, Long rowCount) {
        TableInfoRespVO vo = new TableInfoRespVO();
        vo.setTableName(tableName);
        vo.setDisplayName(displayName);
        vo.setTableComment(tableComment);
        vo.setTableType(tableType);
        vo.setSchemaName(schemaName);
        vo.setRowCount(rowCount);
        return vo;
    }

    private ColumnInfoRespVO createColumnInfoRespVO(String columnName, String displayName, String dataType,
                                                    Integer dataLength, Integer decimalPlaces, Boolean isNullable,
                                                    Boolean isPrimaryKey, Boolean isAutoIncrement, String defaultValue,
                                                    String columnComment, Integer ordinalPosition) {
        ColumnInfoRespVO vo = new ColumnInfoRespVO();
        vo.setColumnName(columnName);
        vo.setDisplayName(displayName);
        vo.setDataType(dataType);
        vo.setDataLength(dataLength);
        vo.setDecimalPlaces(decimalPlaces);
        vo.setIsNullable(isNullable);
        vo.setIsPrimaryKey(isPrimaryKey);
        vo.setIsAutoIncrement(isAutoIncrement);
        vo.setDefaultValue(defaultValue);
        vo.setColumnComment(columnComment);
        vo.setOrdinalPosition(ordinalPosition);
        return vo;
    }

    private DatasourceSaveReqVO createDatasourceSaveReqVO() {
        DatasourceSaveReqVO vo = new DatasourceSaveReqVO();
        vo.setDatasourceName("用户数据库");
        vo.setCode("user_db_001");
        vo.setDatasourceType("POSTGRESQL");
        vo.setDescription("用户管理系统数据库");
        vo.setAppId(12345L);
        return vo;
    }

    private MetadataDatasourceDO createMetadataDatasourceDO() {
        MetadataDatasourceDO datasource = new MetadataDatasourceDO();
        datasource.setId(1001L);
        datasource.setDatasourceName("用户数据库");
        datasource.setCode("user_db_001");
        datasource.setDatasourceType("POSTGRESQL");
        datasource.setDescription("用户管理系统数据库");
        datasource.setAppId(12345L);
        datasource.setCreateTime(LocalDateTime.now());
        return datasource;
    }
} 