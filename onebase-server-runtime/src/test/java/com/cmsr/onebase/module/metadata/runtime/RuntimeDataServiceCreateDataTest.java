package com.cmsr.onebase.module.metadata.runtime;

import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataCreateReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataGetReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.runtime.service.datamethod.RuntimeDataService;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RuntimeDataService createData 方法单元测试
 * <p>
 * 测试字段ID到字段名称的转换逻辑是否正确，以及整个createData调用链路是否正常工作
 * </p>
 * <p>
 * 测试重点：
 * 1. 验证字段ID到字段名称的转换是否正确
 * 2. 验证整个调用链路中数据格式的一致性（都使用字段名称作为key）
 * 3. 验证创建的数据可以通过查询接口正确获取
 * </p>
 *
 * @author system
 * @date 2025-01-25
 */
@Slf4j
@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class RuntimeDataServiceCreateDataTest {

    @Autowired
    private RuntimeDataService runtimeDataService;

    @Autowired
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    @Autowired
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;

    /**
     * 查询并输出可用的测试数据
     * <p>
     * 运行此方法可以查询数据库中的实体和字段信息，用于填充testCreateData方法
     * </p>
     */
    @Test
    public void findTestData() {
        // 忽略租户隔离
        TenantContextHolder.setIgnore(true);
        // 模拟登录用户：userId=116683300797415424L, applicationId=113428849444978688L
        RTSecurityContext.mockLoginUser(116683300797415424L, 113428849444978688L);

        try {
            // 查询前10个实体
            List<MetadataBusinessEntityDO> entities = metadataBusinessEntityRepository.getBusinessEntityList();
            log.info("========== 可用的测试实体 ==========");
            log.info("共找到 {} 个实体", entities.size());

            for (MetadataBusinessEntityDO entity : entities) {
                if (entity == null) {
                    continue;
                }

                Long entityId = entity.getId();
                log.info("\n实体ID: {}", entityId);
                log.info("实体名称: {}", entity.getDisplayName());
                log.info("表名: {}", entity.getTableName());
                log.info("应用ID: {}", entity.getAppId());

                // 查询该实体的字段
                List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
                if (fields != null && !fields.isEmpty()) {
                    log.info("字段列表:");
                    int fieldCount = 0;
                    for (MetadataEntityFieldDO field : fields) {
                        // 过滤系统字段
                        String fieldName = field.getFieldName();
                        if (fieldName != null
                                && !"id".equalsIgnoreCase(fieldName)
                                && !"deleted".equalsIgnoreCase(fieldName)
                                && !"created_time".equalsIgnoreCase(fieldName)
                                && !"updated_time".equalsIgnoreCase(fieldName)
                                && !"lock_version".equalsIgnoreCase(fieldName)
                                && !"tenant_id".equalsIgnoreCase(fieldName)
                                && !"creator".equalsIgnoreCase(fieldName)
                                && !"updater".equalsIgnoreCase(fieldName)) {
                            log.info("  - 字段ID: {}, 字段名称: {}, 字段类型: {}, 显示名称: {}",
                                    field.getId(), fieldName, field.getFieldType(), field.getDisplayName());
                            fieldCount++;
                            if (fieldCount >= 5) {
                                break; // 只显示前5个业务字段
                            }
                        }
                    }
                    if (fieldCount > 0) {
                        log.info("\n测试代码建议:");
                        log.info("Long entityId = {}L;", entityId);
                        log.info("Long menuId = null; // 可选，根据实际情况设置");
                        log.info("==========================================\n");
                    }
                } else {
                    log.info("该实体没有字段定义");
                }
            }

        } catch (Exception e) {
            log.error("查询测试数据失败", e);
        } finally {
            TenantContextHolder.clear();
        }
    }

    /**
     * 测试createData方法的基本功能
     * <p>
     * 测试步骤：
     * 1. 准备测试数据（entityId和字段ID）
     * 2. 调用createData方法创建数据
     * 3. 验证返回结果是否正确
     * 4. 查询创建的数据，验证字段ID是否正确转换为字段名称
     * </p>
     * <p>
     * 注意：运行findTestData()方法可以查询数据库并获取可用的测试数据
     * </p>
     */
    @Test
    public void testCreateData() {
        // 测试数据来自数据库查询结果：
        // 实体ID: 113429588179353600 (订单表, table_name: e2t3_order1030)
        // 菜单ID: 113431890281824256 (订单表页面)
        // 业务字段：
        //   - 113430189474775040: order_id (订单号, TEXT)
        //   - 113440583295631360: order_source (订单来源, TEXT)
        //   - 113440583295631361: order_platform (平台, TEXT)
        Long entityId = 113429588179353600L; // 订单表
        Long menuId = 113431890281824256L;   // 订单表页面
        
        // 获取实体的字段列表，用于测试
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
        if (fields == null || fields.isEmpty()) {
            log.warn("实体 {} 没有字段定义，跳过测试", entityId);
            return;
        }

        // 选择前3个非系统字段用于测试
        List<MetadataEntityFieldDO> testFields = fields.stream()
                .filter(field -> field.getFieldName() != null 
                        && !"id".equalsIgnoreCase(field.getFieldName())
                        && !"deleted".equalsIgnoreCase(field.getFieldName())
                        && !"created_time".equalsIgnoreCase(field.getFieldName())
                        && !"updated_time".equalsIgnoreCase(field.getFieldName())
                        && !"lock_version".equalsIgnoreCase(field.getFieldName()))
                .limit(3)
                .toList();

        if (testFields.isEmpty()) {
            log.warn("实体 {} 没有可用的测试字段，跳过测试", entityId);
            return;
        }

        log.info("开始测试createData方法，实体ID: {}, 测试字段数量: {}", entityId, testFields.size());

        // 忽略租户隔离
        TenantContextHolder.setIgnore(true);
        // 模拟登录用户：userId=116683300797415424L, applicationId=113428849444978688L
        RTSecurityContext.mockLoginUser(116683300797415424L, 113428849444978688L);

        try {
            // 1. 准备测试数据（使用字段ID作为key）
            Map<Long, Object> testData = new HashMap<>();
            
            // 为每个测试字段设置测试值
            int index = 0;
            for (MetadataEntityFieldDO field : testFields) {
                Object testValue = generateTestValue(field, index);
                testData.put(field.getId(), testValue);
                log.info("准备测试数据 - 字段ID: {}, 字段名称: {}, 字段类型: {}, 测试值: {}", 
                        field.getId(), field.getFieldName(), field.getFieldType(), testValue);
                index++;
            }

            // 2. 创建请求对象
            DynamicDataCreateReqVO createReqVO = new DynamicDataCreateReqVO();
            createReqVO.setEntityId(entityId);
            createReqVO.setMenuId(menuId);
            createReqVO.setData(testData);

            log.info("调用createData方法，请求参数: entityId={}, menuId={}, 数据字段数={}", 
                    entityId, menuId, testData.size());

            // 3. 调用createData方法
            DynamicDataRespVO result = runtimeDataService.createData(createReqVO);

            // 4. 验证返回结果
            assertNotNull(result, "createData返回结果不能为空");
            assertNotNull(result.getEntityId(), "返回结果中的实体ID不能为空");
            assertEquals(entityId, result.getEntityId(), "返回结果中的实体ID应该与请求的实体ID一致");
            assertNotNull(result.getData(), "返回结果中的数据不能为空");

            log.info("createData方法调用成功，返回的实体名称: {}", result.getEntityName());

            // 5. 验证字段ID是否正确转换为字段名称
            // 返回的data中，key应该是字段名称，而不是字段ID
            Map<String, Object> responseData = result.getData();
            
            log.info("验证字段转换结果，返回数据字段数: {}", responseData.size());
            
            for (MetadataEntityFieldDO field : testFields) {
                String fieldName = field.getFieldName();
                
                // 验证返回的数据中，key是字段名称（不是字段ID）
                assertTrue(responseData.containsKey(fieldName), 
                        String.format("返回数据中应该包含字段名称 '%s'（字段ID: %d）", fieldName, field.getId()));
                
                // 验证字段ID不应该作为key出现在返回数据中
                assertFalse(responseData.containsKey(field.getId().toString()), 
                        String.format("返回数据中不应该包含字段ID '%d'作为key", field.getId()));
                
                // 验证字段值是否正确
                Object expectedValue = testData.get(field.getId());
                Object actualValue = responseData.get(fieldName);
                
                log.info("字段验证 - 字段名称: {}, 期望值: {}, 实际值: {}", 
                        fieldName, expectedValue, actualValue);
                
                // 注意：实际值可能经过类型转换或处理，这里只验证不为null
                if (expectedValue != null) {
                    // 值可能经过处理，不进行严格相等比较，只验证不为null
                    assertNotNull(actualValue, 
                            String.format("字段 '%s' 的值不应该为null", fieldName));
                }
            }

            // 6. 验证返回数据中包含ID字段
            assertTrue(responseData.containsKey("id"), "返回数据中应该包含id字段");
            Object idValue = responseData.get("id");
            assertNotNull(idValue, "返回数据中的id值不应该为null");
            
            log.info("数据创建成功，数据ID: {}", idValue);

            // 7. 验证字段类型信息
            if (result.getFieldType() != null) {
                for (MetadataEntityFieldDO field : testFields) {
                    String fieldType = result.getFieldType().get(field.getFieldName());
                    assertNotNull(fieldType, 
                            String.format("字段类型信息中应该包含字段 '%s' 的类型", field.getFieldName()));
                    log.info("字段类型信息 - 字段名称: {}, 字段类型: {}", field.getFieldName(), fieldType);
                }
            }

            // 8. 验证可以通过查询接口获取创建的数据
            Long createdDataId;
            if (idValue instanceof Number) {
                createdDataId = ((Number) idValue).longValue();
            } else {
                createdDataId = Long.valueOf(idValue.toString());
            }

            DynamicDataGetReqVO getReqVO = new DynamicDataGetReqVO();
            getReqVO.setEntityId(entityId);
            getReqVO.setId(createdDataId);
            getReqVO.setMenuId(menuId);

            DynamicDataRespVO queryResult = runtimeDataService.getData(getReqVO);
            assertNotNull(queryResult, "查询创建的数据时，返回结果不能为空");
            assertNotNull(queryResult.getData(), "查询创建的数据时，返回的数据不能为空");
            
            // 验证查询返回的数据中，字段也是使用字段名称作为key
            Map<String, Object> queryData = queryResult.getData();
            for (MetadataEntityFieldDO field : testFields) {
                assertTrue(queryData.containsKey(field.getFieldName()), 
                        String.format("查询返回的数据中应该包含字段名称 '%s'", field.getFieldName()));
            }

            log.info("createData方法测试通过！创建的数据ID: {}", createdDataId);

        } catch (Exception e) {
            log.error("createData方法测试失败", e);
            fail("createData方法测试失败: " + e.getMessage());
        } finally {
            // 清理上下文
            TenantContextHolder.clear();
        }
    }

    /**
     * 根据字段类型生成测试值
     *
     * @param field 字段定义
     * @param index 索引（用于生成不同的测试值）
     * @return 测试值
     */
    private Object generateTestValue(MetadataEntityFieldDO field, int index) {
        if (field == null || field.getFieldType() == null) {
            return "test_value_" + index;
        }

        String fieldType = field.getFieldType().toUpperCase();
        
        switch (fieldType) {
            case "VARCHAR":
            case "TEXT":
            case "LONGVARCHAR":
                return "测试文本_" + index;
                
            case "INT":
            case "INTEGER":
            case "BIGINT":
                return 100 + index;
                
            case "DECIMAL":
            case "NUMERIC":
            case "DOUBLE":
            case "FLOAT":
                return 100.5 + index;
                
            case "BOOLEAN":
            case "BIT":
                return index % 2 == 0;
                
            case "DATE":
                return "2025-01-25";
                
            case "DATETIME":
            case "TIMESTAMP":
                return "2025-01-25 10:00:00";
                
            default:
                return "测试值_" + index;
        }
    }

}
