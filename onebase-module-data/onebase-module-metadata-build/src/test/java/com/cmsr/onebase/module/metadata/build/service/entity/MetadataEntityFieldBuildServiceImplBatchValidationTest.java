package com.cmsr.onebase.module.metadata.build.service.entity;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldUpsertItemVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRuleGroupBuildService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_DISPLAY_NAME_DUPLICATE;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_NAME_DUPLICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MetadataEntityFieldBuildServiceImplBatchValidationTest {

    @Test
    void createFieldNameDuplicatedWithExisting_throwsServiceException() {
        List<MetadataEntityFieldDO> existing = List.of(
                field(1L, "F1", 1001L, "E1", "username", "用户名")
        );
        Map<Long, MetadataEntityFieldDO> fieldsById = new HashMap<>();

        EntityFieldUpsertItemVO create = new EntityFieldUpsertItemVO();
        create.setFieldName("username");
        create.setDisplayName("新用户名");

        ServiceException ex = assertThrows(ServiceException.class, () ->
                MetadataEntityFieldBuildServiceImpl.validateBatchFieldUniqueness("E1", "1001", List.of(create), existing, fieldsById)
        );
        assertEquals(ENTITY_FIELD_NAME_DUPLICATE.getCode(), ex.getCode());
    }

    @Test
    void updateWithoutChangingNameOrDisplayName_passes() {
        MetadataEntityFieldDO f1 = field(1L, "F1", 1001L, "E1", "username", "用户名");
        List<MetadataEntityFieldDO> existing = List.of(f1);
        Map<Long, MetadataEntityFieldDO> fieldsById = Map.of(1L, f1);

        EntityFieldUpsertItemVO update = new EntityFieldUpsertItemVO();
        update.setId("1");

        MetadataEntityFieldBuildServiceImpl.validateBatchFieldUniqueness("E1", "1001", List.of(update), existing, fieldsById);
    }

    @Test
    void deleteThenRecreateSameFieldName_passes() {
        MetadataEntityFieldDO f1 = field(1L, "F1", 1001L, "E1", "username", "用户名");
        List<MetadataEntityFieldDO> existing = List.of(f1);
        Map<Long, MetadataEntityFieldDO> fieldsById = Map.of(1L, f1);

        EntityFieldUpsertItemVO delete = new EntityFieldUpsertItemVO();
        delete.setId("1");
        delete.setIsDeleted(true);

        EntityFieldUpsertItemVO create = new EntityFieldUpsertItemVO();
        create.setFieldName("username");
        create.setDisplayName("新用户名");

        MetadataEntityFieldBuildServiceImpl.validateBatchFieldUniqueness("E1", "1001", List.of(delete, create), existing, fieldsById);
    }

    @Test
    void createDisplayNameDuplicatedWithExisting_throwsServiceException() {
        List<MetadataEntityFieldDO> existing = List.of(
                field(1L, "F1", 1001L, "E1", "username", "用户名")
        );
        Map<Long, MetadataEntityFieldDO> fieldsById = new HashMap<>();

        EntityFieldUpsertItemVO create = new EntityFieldUpsertItemVO();
        create.setFieldName("username2");
        create.setDisplayName("用户名");

        ServiceException ex = assertThrows(ServiceException.class, () ->
                MetadataEntityFieldBuildServiceImpl.validateBatchFieldUniqueness("E1", "1001", List.of(create), existing, fieldsById)
        );
        assertEquals(ENTITY_FIELD_DISPLAY_NAME_DUPLICATE.getCode(), ex.getCode());
    }

    @Test
    void syncValidationRules_requiredCreate_usesBatchQueryAndSavesOnce() {
        MetadataValidationRequiredRepository requiredRepo = mock(MetadataValidationRequiredRepository.class);
        MetadataValidationUniqueRepository uniqueRepo = mock(MetadataValidationUniqueRepository.class);
        MetadataValidationLengthRepository lengthRepo = mock(MetadataValidationLengthRepository.class);
        MetadataValidationRuleGroupBuildService groupService = mock(MetadataValidationRuleGroupBuildService.class);

        when(requiredRepo.findByFieldUuids(ArgumentMatchers.any())).thenReturn(List.of());
        when(groupService.createValidationRuleGroup(ArgumentMatchers.any())).thenReturn(10L);

        MetadataEntityFieldBuildServiceImpl service = newService(requiredRepo, uniqueRepo, lengthRepo, groupService);

        MetadataBusinessEntityDO entity = new MetadataBusinessEntityDO();
        entity.setDisplayName("学生信息表");
        entity.setTableName("t_student");

        MetadataEntityFieldDO f = field(1L, "F1", 1001L, "E1", "username", "用户名");
        f.setIsRequired(1);
        Map<String, MetadataEntityFieldDO> fieldsByUuid = Map.of("F1", f);
        Set<String> required = new TreeSet<>(Set.of("F1"));

        service.syncValidationRulesForFields(entity, fieldsByUuid, required, Set.of(), Set.of());

        verify(requiredRepo, times(1)).findByFieldUuids(required);
        verify(requiredRepo, times(1)).saveOrUpdate(ArgumentMatchers.argThat(x -> "F1".equals(x.getFieldUuid())));
        verify(groupService, times(1)).createValidationRuleGroup(ArgumentMatchers.any());
        verifyNoInteractions(uniqueRepo);
        verifyNoInteractions(lengthRepo);
    }

    @Test
    void syncValidationRules_uniqueCreate_usesBatchQueryAndCreatesGroupOnce() {
        MetadataValidationRequiredRepository requiredRepo = mock(MetadataValidationRequiredRepository.class);
        MetadataValidationUniqueRepository uniqueRepo = mock(MetadataValidationUniqueRepository.class);
        MetadataValidationLengthRepository lengthRepo = mock(MetadataValidationLengthRepository.class);
        MetadataValidationRuleGroupBuildService groupService = mock(MetadataValidationRuleGroupBuildService.class);

        when(uniqueRepo.findByFieldUuids(ArgumentMatchers.any())).thenReturn(List.of());
        when(groupService.createValidationRuleGroup(ArgumentMatchers.any())).thenReturn(11L);
        MetadataValidationRuleGroupDO groupDO = new MetadataValidationRuleGroupDO();
        groupDO.setId(11L);
        groupDO.setGroupUuid("G11");
        when(groupService.getValidationRuleGroup(11L)).thenReturn(groupDO);

        MetadataEntityFieldBuildServiceImpl service = newService(requiredRepo, uniqueRepo, lengthRepo, groupService);

        MetadataBusinessEntityDO entity = new MetadataBusinessEntityDO();
        entity.setDisplayName("学生信息表");
        entity.setTableName("t_student");

        MetadataEntityFieldDO f = field(1L, "F1", 1001L, "E1", "username", "用户名");
        f.setIsUnique(1);
        Map<String, MetadataEntityFieldDO> fieldsByUuid = Map.of("F1", f);
        Set<String> unique = new TreeSet<>(Set.of("F1"));

        service.syncValidationRulesForFields(entity, fieldsByUuid, Set.of(), unique, Set.of());

        verify(uniqueRepo, times(1)).findByFieldUuids(unique);
        verify(uniqueRepo, times(1)).saveOrUpdate(ArgumentMatchers.argThat(x -> "F1".equals(x.getFieldUuid()) && "G11".equals(x.getGroupUuid())));
        verify(groupService, times(1)).createValidationRuleGroup(ArgumentMatchers.any());
        verify(groupService, times(1)).getValidationRuleGroup(11L);
        verifyNoInteractions(requiredRepo);
        verifyNoInteractions(lengthRepo);
    }

    @Test
    void syncValidationRules_lengthUpdate_existingRule_updatesOnce() {
        MetadataValidationRequiredRepository requiredRepo = mock(MetadataValidationRequiredRepository.class);
        MetadataValidationUniqueRepository uniqueRepo = mock(MetadataValidationUniqueRepository.class);
        MetadataValidationLengthRepository lengthRepo = mock(MetadataValidationLengthRepository.class);
        MetadataValidationRuleGroupBuildService groupService = mock(MetadataValidationRuleGroupBuildService.class);

        MetadataValidationLengthDO existing = new MetadataValidationLengthDO();
        existing.setId(100L);
        existing.setFieldUuid("F1");
        existing.setIsEnabled(0);
        existing.setMaxLength(10);
        when(lengthRepo.findByFieldUuids(ArgumentMatchers.any())).thenReturn(List.of(existing));

        MetadataEntityFieldBuildServiceImpl service = newService(requiredRepo, uniqueRepo, lengthRepo, groupService);

        MetadataBusinessEntityDO entity = new MetadataBusinessEntityDO();
        entity.setDisplayName("学生信息表");
        entity.setTableName("t_student");

        MetadataEntityFieldDO f = field(1L, "F1", 1001L, "E1", "username", "用户名");
        f.setDataLength(20);
        Map<String, MetadataEntityFieldDO> fieldsByUuid = Map.of("F1", f);
        Set<String> length = new TreeSet<>(Set.of("F1"));

        service.syncValidationRulesForFields(entity, fieldsByUuid, Set.of(), Set.of(), length);

        verify(lengthRepo, times(1)).findByFieldUuids(length);
        verify(lengthRepo, times(1)).updateById(ArgumentMatchers.argThat(x -> x.getId().equals(100L) && x.getIsEnabled() == 1 && x.getMaxLength().equals(20)));
        verify(groupService, never()).createValidationRuleGroup(ArgumentMatchers.any());
    }

    @Test
    void syncValidationRules_lengthDelete_deletesGroupWhenSingleReference() {
        MetadataValidationRequiredRepository requiredRepo = mock(MetadataValidationRequiredRepository.class);
        MetadataValidationUniqueRepository uniqueRepo = mock(MetadataValidationUniqueRepository.class);
        MetadataValidationLengthRepository lengthRepo = mock(MetadataValidationLengthRepository.class);
        MetadataValidationRuleGroupBuildService groupService = mock(MetadataValidationRuleGroupBuildService.class);

        MetadataValidationLengthDO existing = new MetadataValidationLengthDO();
        existing.setId(100L);
        existing.setFieldUuid("F1");
        existing.setIsEnabled(1);
        existing.setGroupUuid("G1");
        existing.setMaxLength(10);
        when(lengthRepo.findByFieldUuids(ArgumentMatchers.any())).thenReturn(List.of(existing));
        when(lengthRepo.findByGroupUuid("G1")).thenReturn(List.of(existing));

        MetadataEntityFieldBuildServiceImpl service = newService(requiredRepo, uniqueRepo, lengthRepo, groupService);

        MetadataBusinessEntityDO entity = new MetadataBusinessEntityDO();
        entity.setDisplayName("学生信息表");
        entity.setTableName("t_student");

        MetadataEntityFieldDO f = field(1L, "F1", 1001L, "E1", "username", "用户名");
        f.setDataLength(null);
        Map<String, MetadataEntityFieldDO> fieldsByUuid = Map.of("F1", f);
        Set<String> length = new TreeSet<>(Set.of("F1"));

        service.syncValidationRulesForFields(entity, fieldsByUuid, Set.of(), Set.of(), length);

        verify(lengthRepo, times(1)).deleteByFieldUuid("F1");
        verify(groupService, times(1)).safeDeleteGroupDirect("G1");
    }

    private static MetadataEntityFieldDO field(Long id, String fieldUuid, Long applicationId, String entityUuid, String fieldName,
            String displayName) {
        MetadataEntityFieldDO f = new MetadataEntityFieldDO();
        f.setId(id);
        f.setFieldUuid(fieldUuid);
        f.setApplicationId(applicationId);
        f.setEntityUuid(entityUuid);
        f.setFieldName(fieldName);
        f.setDisplayName(displayName);
        return f;
    }

    private static MetadataEntityFieldBuildServiceImpl newService(MetadataValidationRequiredRepository requiredRepo,
            MetadataValidationUniqueRepository uniqueRepo,
            MetadataValidationLengthRepository lengthRepo,
            MetadataValidationRuleGroupBuildService groupService) {
        MetadataEntityFieldBuildServiceImpl service = new MetadataEntityFieldBuildServiceImpl();
        setField(service, "validationRequiredRepository", requiredRepo);
        setField(service, "validationUniqueRepository", uniqueRepo);
        setField(service, "validationLengthRepository", lengthRepo);
        setField(service, "validationRuleGroupService", groupService);
        return service;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
