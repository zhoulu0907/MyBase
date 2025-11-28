package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.permission.RuntimePermissionChecker;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component
public class PermissionValidator {
    @Resource
    private List<RuntimePermissionChecker> permissionCheckers;

    public void validate(RecordDTO recordDTO) {
        List<RuntimePermissionChecker> sorted = permissionCheckers.stream()
                .filter(c -> c.supports(recordDTO))
                .sorted(Comparator.comparingInt(RuntimePermissionChecker::getOrder))
                .toList();
        for (RuntimePermissionChecker c : sorted) {
            try {
                c.check(recordDTO);
            } catch (PermissionDeniedException e) {
                throw e;
            } catch (Exception e) {
                throw new PermissionDeniedException(c.getPermissionType(), "UNKNOWN", e.getMessage(), e);
            }
        }
    }
}
