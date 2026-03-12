package com.cmsr.onebase.module.metadata.core.service.datamethod.validator;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRangeRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl.RequiredValidationService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ValidationManagerPrefetchTest {

    @Test
    void validateEntity_prefetchRequired_usesBatchQuery() {
        MetadataValidationRequiredRepository requiredRepo = mock(MetadataValidationRequiredRepository.class);
        MetadataValidationUniqueRepository uniqueRepo = mock(MetadataValidationUniqueRepository.class);
        MetadataValidationLengthRepository lengthRepo = mock(MetadataValidationLengthRepository.class);
        MetadataValidationFormatRepository formatRepo = mock(MetadataValidationFormatRepository.class);
        MetadataValidationRangeRepository rangeRepo = mock(MetadataValidationRangeRepository.class);

        AutoNumberService autoNumberService = mock(AutoNumberService.class);
        when(autoNumberService.hasAutoNumber(anyString())).thenReturn(false);

        MetadataValidationRequiredDO r = new MetadataValidationRequiredDO();
        r.setFieldUuid("F1");
        r.setIsEnabled(1);
        r.setPromptMessage("必须填写");

        when(requiredRepo.findByFieldUuids(any())).thenReturn(List.of(r));
        when(uniqueRepo.findByFieldUuids(any())).thenReturn(List.of());
        when(lengthRepo.findByFieldUuids(any())).thenReturn(List.of());
        when(formatRepo.findByFieldUuids(any())).thenReturn(List.of());
        when(rangeRepo.findByFieldUuids(any())).thenReturn(List.of());

        RequiredValidationService requiredService = new RequiredValidationService(requiredRepo);
        ValidationManager manager = new ValidationManager(List.of(requiredService));
        setField(manager, "autoNumberService", autoNumberService);
        setField(manager, "requiredRepository", requiredRepo);
        setField(manager, "uniqueRepository", uniqueRepo);
        setField(manager, "lengthRepository", lengthRepo);
        setField(manager, "formatRepository", formatRepo);
        setField(manager, "rangeRepository", rangeRepo);

        MetadataEntityFieldDO f = new MetadataEntityFieldDO();
        f.setFieldUuid("F1");
        f.setFieldName("name");
        f.setDisplayName("姓名");
        f.setFieldType("TEXT");
        f.setIsSystemField(0);
        f.setIsPrimaryKey(0);

        assertThrows(RuntimeException.class, () -> manager.validateEntity("E1", List.of(f), Map.of(), List.of(), MetadataDataMethodOpEnum.CREATE));

        verify(requiredRepo, times(1)).findByFieldUuids(argThat((Set<String> s) -> s.contains("F1")));
        verify(requiredRepo, never()).findByFieldUuid(anyString());
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
