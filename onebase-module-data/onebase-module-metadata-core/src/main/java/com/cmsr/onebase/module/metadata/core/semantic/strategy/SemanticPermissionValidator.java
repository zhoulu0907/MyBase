package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.permission.SemanticRuntimePermissionChecker;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component
public class SemanticPermissionValidator {
    @Resource
    private List<SemanticRuntimePermissionChecker> semanticPermissionCheckers;

    public void validate(SemanticRecordDTO recordDTO) {
        List<SemanticRuntimePermissionChecker> sorted = semanticPermissionCheckers.stream()
                .filter(c -> c.supports(recordDTO))
                .sorted(Comparator.comparingInt(SemanticRuntimePermissionChecker::getOrder))
                .toList();
        for (SemanticRuntimePermissionChecker c : sorted) {
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
