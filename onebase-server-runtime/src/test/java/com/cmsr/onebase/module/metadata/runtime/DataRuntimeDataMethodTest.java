package com.cmsr.onebase.module.metadata.runtime;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataCreateReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataGetReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataUpdateReqVO;
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
 * 动态数据操作方法测试类
 *
 * @author matianyu
 * @date 2025-11-15
 */
@Slf4j
@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class DataRuntimeDataMethodTest {

    @Autowired
    private RuntimeDataService runtimeDataService;

    /**
     * 测试 DATA_SELECTION 字段的存储和读取功能
     * 
     * 测试步骤：
     * 1. 创建包含 DATA_SELECTION 字段的数据（源实体）
     * 2. 验证存储是否正确（目标实体表中的关联字段是否正确更新）
     * 3. 查询数据并验证读取是否正确（DATA_SELECTION 字段应该返回选择的数据ID列表）
     * 4. 更新数据并验证更新是否正确
     * 
     * 注意：此测试需要预先准备以下测试数据：
     * - 源实体（包含 DATA_SELECTION 字段）
     * - 目标实体（包含关联字段）
     * - 关联关系（MetadataEntityRelationshipDO）
     * - 目标实体中的一些测试数据
     * 
     * 测试数据示例：
     * - 源实体ID: 需要根据实际测试环境配置
     * - DATA_SELECTION 字段ID: 需要根据实际测试环境配置（用于创建/更新数据时设置字段值）
     * - DATA_SELECTION 字段名称: 需要根据实际测试环境配置（用于从响应中获取字段值）
     * - 目标实体ID: 需要根据实际测试环境配置
     * - 目标字段ID: 需要根据实际测试环境配置
     * - 目标实体中的测试数据ID: 需要根据实际测试环境配置（目标实体中已存在的测试数据ID列表）
     */
    @Test
    public void testDataSelectionFieldStorageAndRead() {

        TenantContextHolder.setIgnore(true);
        RTSecurityContext.mockLoginUser(116683300797415424L, 113428849444978688L);


        // TODO: 根据实际测试环境配置以下参数
        Long sourceEntityId = null; // 源实体ID（包含 DATA_SELECTION 字段的实体）
        Long dataSelectionFieldId = null; // DATA_SELECTION 字段的ID
        String dataSelectionFieldName = null; // DATA_SELECTION 字段的名称（用于从响应中获取数据）
        Long targetEntityId = null; // 目标实体ID
        Long targetFieldId = null; // 目标实体中的关联字段ID
        List<Long> selectedDataIds = null; // 目标实体中已存在的测试数据ID列表
        
        // 如果测试数据未配置，跳过测试
        if (sourceEntityId == null || dataSelectionFieldId == null || dataSelectionFieldName == null ||
            targetEntityId == null || targetFieldId == null || 
            selectedDataIds == null || selectedDataIds.isEmpty()) {
            log.warn("DATA_SELECTION 字段测试数据未配置，跳过测试");
            return;
        }

        try {
            // 设置租户上下文（如果需要）
            TenantContextHolder.setTenantId(1L);
            
            // 1. 创建包含 DATA_SELECTION 字段的数据
            DynamicDataCreateReqVO createReqVO = new DynamicDataCreateReqVO();
            createReqVO.setEntityId(sourceEntityId);
            
            // 构建数据，DATA_SELECTION 字段的值应该是选择的数据ID列表
            Map<Long, Object> data = new HashMap<>();
            // 假设还有其他字段需要设置
            // data.put(otherFieldId, otherFieldValue);
            
            // 设置 DATA_SELECTION 字段的值（可以是单个ID或ID列表）
            if (selectedDataIds.size() == 1) {
                // 单选：直接设置ID
                data.put(dataSelectionFieldId, selectedDataIds.get(0));
            } else {
                // 多选：设置ID列表
                data.put(dataSelectionFieldId, selectedDataIds);
            }
            
            createReqVO.setData(data);
            
            log.info("创建数据，实体ID: {}, DATA_SELECTION 字段ID: {}, 选择的数据ID: {}", 
                    sourceEntityId, dataSelectionFieldId, selectedDataIds);
            
            DynamicDataRespVO createResp = runtimeDataService.createData(createReqVO);
            assertNotNull(createResp, "创建数据响应不能为空");
            assertNotNull(createResp.getData(), "创建的数据不能为空");
            
            // 从 data Map 中获取主键字段（通常是 "id"）的值
            Object idValue = createResp.getData().get("id");
            assertNotNull(idValue, "创建的数据ID不能为空");
            
            Long createdDataId;
            if (idValue instanceof Number) {
                createdDataId = ((Number) idValue).longValue();
            } else {
                createdDataId = Long.valueOf(idValue.toString());
            }
            log.info("数据创建成功，数据ID: {}", createdDataId);
            
            // 2. 查询数据并验证 DATA_SELECTION 字段是否正确读取
            DynamicDataGetReqVO getReqVO = new DynamicDataGetReqVO();
            getReqVO.setEntityId(sourceEntityId);
            getReqVO.setId(createdDataId);
            
            log.info("查询数据，实体ID: {}, 数据ID: {}", sourceEntityId, createdDataId);
            
            DynamicDataRespVO getResp = runtimeDataService.getData(getReqVO);
            assertNotNull(getResp, "查询数据响应不能为空");
            assertNotNull(getResp.getData(), "查询的数据不能为空");
            
            // 验证 DATA_SELECTION 字段的值（使用字段名称作为key）
            Object dataSelectionValue = getResp.getData().get(dataSelectionFieldName);
            assertNotNull(dataSelectionValue, "DATA_SELECTION 字段的值不能为空");
            
            log.info("查询到的 DATA_SELECTION 字段值: {}", dataSelectionValue);
            
            // 验证返回的值是否包含选择的数据ID
            if (selectedDataIds.size() == 1) {
                // 单选：应该返回单个ID
                assertEquals(selectedDataIds.get(0).toString(), dataSelectionValue.toString(), 
                        "DATA_SELECTION 字段的值应该等于选择的数据ID");
            } else {
                // 多选：应该返回ID列表
                assertTrue(dataSelectionValue instanceof List, 
                        "多选情况下，DATA_SELECTION 字段的值应该是列表类型");
                @SuppressWarnings("unchecked")
                List<Object> returnedIds = (List<Object>) dataSelectionValue;
                assertEquals(selectedDataIds.size(), returnedIds.size(), 
                        "返回的数据ID数量应该等于选择的数据ID数量");
                
                // 验证每个ID是否都在返回列表中
                for (Long selectedId : selectedDataIds) {
                    boolean found = returnedIds.stream()
                            .anyMatch(id -> id.toString().equals(selectedId.toString()));
                    assertTrue(found, "选择的数据ID " + selectedId + " 应该在返回列表中");
                }
            }
            
            // 3. 更新数据，修改 DATA_SELECTION 字段的值
            DynamicDataUpdateReqVO updateReqVO = new DynamicDataUpdateReqVO();
            updateReqVO.setEntityId(sourceEntityId);
            updateReqVO.setId(createdDataId);
            
            // 更新为只选择第一个数据
            Map<Long, Object> updateData = new HashMap<>();
            updateData.put(dataSelectionFieldId, selectedDataIds.get(0));
            updateReqVO.setData(updateData);
            
            log.info("更新数据，实体ID: {}, 数据ID: {}, 新的 DATA_SELECTION 字段值: {}", 
                    sourceEntityId, createdDataId, selectedDataIds.get(0));
            
            DynamicDataRespVO updateResp = runtimeDataService.updateData(updateReqVO);
            assertNotNull(updateResp, "更新数据响应不能为空");
            
            // 4. 再次查询数据，验证更新是否正确
            DynamicDataRespVO getRespAfterUpdate = runtimeDataService.getData(getReqVO);
            assertNotNull(getRespAfterUpdate, "更新后查询数据响应不能为空");
            
            Object updatedDataSelectionValue = getRespAfterUpdate.getData().get(dataSelectionFieldName);
            assertNotNull(updatedDataSelectionValue, "更新后 DATA_SELECTION 字段的值不能为空");
            
            log.info("更新后查询到的 DATA_SELECTION 字段值: {}", updatedDataSelectionValue);
            
            // 验证更新后的值应该是单个ID
            assertEquals(selectedDataIds.get(0).toString(), updatedDataSelectionValue.toString(), 
                    "更新后 DATA_SELECTION 字段的值应该等于新选择的数据ID");
            
            log.info("DATA_SELECTION 字段测试通过！");
            
        } catch (Exception e) {
            log.error("DATA_SELECTION 字段测试失败", e);
            fail("DATA_SELECTION 字段测试失败: " + e.getMessage());
        } finally {
            // 清理租户上下文
            TenantContextHolder.clear();
        }
    }
}
