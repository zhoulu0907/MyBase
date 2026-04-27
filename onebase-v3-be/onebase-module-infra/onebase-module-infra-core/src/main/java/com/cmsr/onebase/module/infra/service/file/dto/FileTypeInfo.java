package com.cmsr.onebase.module.infra.service.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传检查项配置DTO（Core模块内部使用）
 *
 * @author chengyuansen
 * @date 2025-11-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileTypeInfo {

    private String mimeType;
    private String magicNumber;
}
