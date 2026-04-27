package com.cmsr.onebase.module.metadata.core.service.query.impl;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryCondition;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MetadataQueryServiceImplTest {

    @Resource
    private MetadataQueryServiceImpl metadataQueryService;

    @MockBean
    private MetadataDataMethodCoreService dataMethodCoreService;

    @MockBean
    private MetadataEntityFieldCoreService entityFieldCoreService;

    @Test
    void shouldKeepOperatorsAndAllConditions() {
        Long entityId = 46999363287089152L;

        MetadataEntityFieldDO nameField = new MetadataEntityFieldDO();
        nameField.setId(46999569445519360L);
        nameField.setFieldName("name");
        nameField.setDisplayName("名称");
        nameField.setFieldType("STRING");

        MetadataEntityFieldDO ageField = new MetadataEntityFieldDO();
        ageField.setId(50028191407505411L);
        ageField.setFieldName("age");
        ageField.setDisplayName("年龄");
        ageField.setFieldType("NUMBER");

        MetadataEntityFieldDO enrollDateField = new MetadataEntityFieldDO();
        enrollDateField.setId(50026937276661762L);
        enrollDateField.setFieldName("enrollment_date");
        enrollDateField.setDisplayName("入学日期");
        enrollDateField.setFieldType("DATE");

        when(entityFieldCoreService.getEntityFieldListByEntityId(entityId))
            .thenReturn(List.of(nameField, ageField, enrollDateField));

        when(dataMethodCoreService.getDataPage(anyLong(), anyInt(), anyInt(), any(), any(), anyMap(), any(), any()))
            .thenReturn(null);

        QueryCondition c1 = new QueryCondition(); c1.setFieldId(nameField.getId()); c1.setOperator("CONTAINS"); c1.setFieldValues(List.of("年级"));
        QueryCondition c2 = new QueryCondition(); c2.setFieldId(ageField.getId()); c2.setOperator("GREATER_THAN"); c2.setFieldValues(List.of("10"));
        QueryCondition c3 = new QueryCondition(); c3.setFieldId(nameField.getId()); c3.setOperator("CONTAINS"); c3.setFieldValues(List.of("班"));
        QueryCondition c4 = new QueryCondition(); c4.setFieldId(enrollDateField.getId()); c4.setOperator("EARLIER_THAN"); c4.setFieldValues(List.of("1990-09-09"));

        QueryRequest req = new QueryRequest();
        req.setEntityId(entityId);
        req.setConditionGroups(List.of(List.of(c1, c2), List.of(c3, c4)));
        req.setLimit(1);

        metadataQueryService.queryByConditions(req);

        // 捕获第一次 OR 组的 filters
        var filtersCaptor = org.mockito.ArgumentCaptor.forClass(Map.class);
        verify(dataMethodCoreService, atLeastOnce()).getDataPage(
                eq(entityId), anyInt(), anyInt(), any(), any(), filtersCaptor.capture(), any(), any());

        Map<String, Object> filters = filtersCaptor.getValue();
        assertThat(filters.values()).hasSize(2);

        boolean hasNameContains = filters.values().stream().anyMatch(v ->
                v instanceof Map && "name".equals(((Map<?, ?>) v).get("fieldName"))
                        && "CONTAINS".equals(((Map<?, ?>) v).get("operator"))
        );
        boolean hasAgeGreater = filters.values().stream().anyMatch(v ->
                v instanceof Map && "age".equals(((Map<?, ?>) v).get("fieldName"))
                        && "GREATER_THAN".equals(((Map<?, ?>) v).get("operator"))
        );
        assertThat(hasNameContains).isTrue();
        assertThat(hasAgeGreater).isTrue();
    }
}
