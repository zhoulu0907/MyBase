package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.storage;

import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategy;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.ComplexObjectFieldValueStorageStrategy;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户字段存储/查询策略
 *
 * @author matianyu
 * @date 2025-11-15
 */
@Slf4j
@Component
public class UserFieldValueStorageStrategy implements FieldValueStorageStrategy {

    private static final Set<String> USER_TYPES = Set.of("USER", "MULTI_USER");

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private ComplexObjectFieldValueStorageStrategy complexObjectStrategy;

    @Override
    public boolean supports(String fieldType) {
        return StringUtils.hasText(fieldType) && USER_TYPES.contains(fieldType.toUpperCase());
    }

    @Override
    public Object transform(Object rawValue, FieldValueTransformMode mode) {
        if (mode == FieldValueTransformMode.STORE) {
            return complexObjectStrategy.transform(rawValue, FieldValueTransformMode.STORE);
        }
        List<Long> userIds = parseUserIds(rawValue);
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        try {
            Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(new LinkedHashSet<>(userIds));
            if (CollectionUtils.isEmpty(userMap)) {
                return null;
            }
            boolean multi = userIds.size() > 1;
            if (multi) {
                return userIds.stream()
                        .map(userMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            return userMap.get(userIds.get(0));
        } catch (Exception ex) {
            log.warn("查询用户信息失败，用户ID：{}，原因：{}", userIds, ex.getMessage());
            return rawValue;
        }
    }

    private List<Long> parseUserIds(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        LinkedHashSet<Long> result = new LinkedHashSet<>();
        collectUserIds(value, result);
        return new ArrayList<>(result);
    }

    private void collectUserIds(Object value, LinkedHashSet<Long> result) {
        if (value == null) {
            return;
        }
        if (value instanceof Number number) {
            result.add(number.longValue());
            return;
        }
        if (value instanceof AdminUserRespDTO userRespDTO) {
            if (userRespDTO.getId() != null) {
                result.add(userRespDTO.getId());
            }
            return;
        }
        if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                collectUserIds(item, result);
            }
            return;
        }
        if (value instanceof Map<?, ?> mapValue) {
            Object idVal = mapValue.get("id");
            if (idVal != null) {
                collectUserIds(idVal, result);
            }
            return;
        }
        String text = value.toString();
        if (!StringUtils.hasText(text)) {
            return;
        }
        String[] segments = text.split(",");
        for (String segment : segments) {
            String trimmed = segment.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                result.add(Long.parseLong(trimmed));
            } catch (NumberFormatException ex) {
                log.debug("无法解析的用户ID：{}", trimmed);
            }
        }
    }
}

