package com.cmsr.onebase.module.metadata.runtime.semantic.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.module.infra.api.file.dto.FileCreateReqDTO;
import org.springframework.web.multipart.MultipartFile;
import com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionContextLoader;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.core.semantic.service.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

@RestController
@RequestMapping("/runtime/metadata/{tableName}/attachment")
@Validated
@Tag(name = "动态数据附件", description = "元数据附件下载接口")
public class SemanticAttachmentController {

    @Resource
    private SemanticMergeRecordAssembler semanticMergeRecordAssembler;
    @Resource
    private SemanticPermissionContextLoader semanticPermissionContextLoader;
    @Resource
    private SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    private SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    private SemanticDataCrudService semanticDataCrudService;
    @Resource
    private FileApi fileApi;

    @GetMapping("/download")
    @Operation(summary = "下载附件")
    public void download(@PathVariable("tableName") String tableName,
                         @RequestParam("menuId") Long menuId,
                         @RequestParam("id") Long id,
                         @RequestParam("fieldName") String fieldName,
                         @RequestParam("fileId") Long fileId,
                         HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) { traceId = UUID.randomUUID().toString(); }

        SemanticTargetBodyVO body = new SemanticTargetBodyVO();
        body.setId(id);
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(
                tableName,
                body,
                menuId,
                traceId,
                SemanticMethodCodeEnum.GET,
                SemanticDataMethodOpEnum.GET);

        semanticPermissionContextLoader.loadPermissionContext(record);
        semanticDataIntegrityValidator.validate(record);
        semanticPermissionValidator.validate(record);

        Map<String, Object> data = semanticDataCrudService.readById(record);
        if (data == null || data.isEmpty()) { throw exception(ErrorCodeConstants.DATA_NOT_EXISTS); }

        List<SemanticFieldSchemaDTO> fields = record.getEntitySchema().getFields();
        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.addAll(verifyMainTableAttachmentAndGetIds(data, fields, fieldName));
        attachmentIds.addAll(verifySubTablesAttachmentAndGetIds(record, fieldName));
        if (attachmentIds.isEmpty() || attachmentIds.stream().noneMatch(x -> Objects.equals(x, fileId))) {
            throw exception(ErrorCodeConstants.DATA_NOT_EXISTS);
        }

        fileApi.getFileContent(fileId, request, response);
        response.setHeader("X-Trace-Id", traceId);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传附件")
    public CommonResult<String> upload(@PathVariable("tableName") String tableName,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "directory", required = false) String directory,
                                       @RequestParam(value = "type", required = false) String type) throws Exception {
        String finalName = (name == null || name.isBlank()) ? file.getOriginalFilename() : name;
        String finalType = (type == null || type.isBlank()) ? file.getContentType() : type;
        FileCreateReqDTO req = new FileCreateReqDTO()
                .setName(finalName)
                .setDirectory(directory)
                .setType(finalType)
                .setContent(file.getBytes());
        return fileApi.createFile(req);
    }

    /**
     * 验证主表字段并解析附件ID列表
     * - 当主表存在同名附件字段(FILE/IMAGE)时，解析该字段附件ID
     * - 字段不存在或非附件类型时返回空列表
     */
    private List<Long> verifyMainTableAttachmentAndGetIds(Map<String, Object> data,
                                                          List<SemanticFieldSchemaDTO> fields,
                                                          String fieldName) {
        if (fields == null || fieldName == null) { return List.of(); }
        for (SemanticFieldSchemaDTO f : fields) {
            if (f == null) continue;
            if (fieldName.equals(f.getFieldName()) && isAttachmentField(f)) {
                Object raw = data.get(fieldName);
                return parseAttachmentIds(raw);
            }
        }
        return List.of();
    }

    /**
     * 验证子表（连接器）字段并解析附件ID列表
     * - 扫描所有连接器，若其关系属性存在同名附件字段(FILE/IMAGE)则解析其附件ID
     * - 不存在匹配字段或详情缺失时返回空列表
     */
    private List<Long> verifySubTablesAttachmentAndGetIds(SemanticRecordDTO record,
                                                          String fieldName) {
        List<Long> out = new ArrayList<>();
        List<SemanticRelationSchemaDTO> cons = record.getEntitySchema().getConnectors();
        if (cons == null || cons.isEmpty() || fieldName == null) { return List.of(); }
        SemanticEntityValueDTO val = record.getResultValue();
        if (val == null) { return List.of(); }
        for (SemanticRelationSchemaDTO c : cons) {
            if (c == null || c.getRelationAttributes() == null) continue;
            boolean matches = c.getRelationAttributes().stream()
                    .anyMatch(a -> a != null && fieldName.equals(a.getFieldName()) && isAttachmentField(a));
            if (!matches) continue;
            String connectorName = c.getTargetEntityTableName();
            if (val.isConnectorOne(connectorName)) {
                SemanticFieldValueDTO<Object> fv = val.getConnectorFieldValue(connectorName, fieldName);
                Object raw = fv == null ? null : fv.getRawValue();
                List<Long> ids = parseAttachmentIds(raw);
                if (!ids.isEmpty()) { out.addAll(ids); }
            } else if (val.isConnectorMany(connectorName)) {
                List<Map<String, SemanticFieldValueDTO<Object>>> rows = val.getConnectorDTOList(connectorName);
                if (rows != null) {
                    for (Map<String, SemanticFieldValueDTO<Object>> row : rows) {
                        if (row == null) continue;
                        SemanticFieldValueDTO<Object> fv = row.get(fieldName);
                        Object raw = fv == null ? null : fv.getRawValue();
                        List<Long> ids = parseAttachmentIds(raw);
                        if (!ids.isEmpty()) { out.addAll(ids); }
                    }
                }
            }
        }
        return out;
    }

    private boolean isAttachmentField(SemanticFieldSchemaDTO f) {
        if (f == null || f.getFieldTypeEnum() == null) { return false; }
        String code = f.getFieldTypeEnum().getCode();
        return "FILE".equalsIgnoreCase(code) || "IMAGE".equalsIgnoreCase(code);
    }

    private List<Long> parseAttachmentIds(Object raw) {
        if (raw == null) { return List.of(); }
        List<Long> out = new ArrayList<>();
        if (raw instanceof Number num) {
            out.add(num.longValue());
            return out;
        }
        if (raw instanceof Collection<?> col) {
            for (Object o : col) { Long v = toLong(o); if (v != null) out.add(v); }
            return out;
        }
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) { return List.of(); }
        if (s.startsWith("[") && s.endsWith("]")) {
            String inner = s.substring(1, s.length() - 1);
            String[] parts = inner.split(",");
            for (String p : parts) {
                String t = p.trim();
                if (t.startsWith("\"") && t.endsWith("\"")) { t = t.substring(1, t.length() - 1); }
                Long v = toLong(t);
                if (v != null) out.add(v);
            }
            return out;
        }
        if (s.contains(",")) {
            String[] parts = s.split(",");
            for (String p : parts) { Long v = toLong(p.trim()); if (v != null) out.add(v); }
            return out;
        }
        Long single = toLong(s);
        if (single != null) { out.add(single); }
        return out;
    }

    private Long toLong(Object v) {
        if (v == null) { return null; }
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) { return null; }
        try { return Long.parseLong(s); } catch (Exception ignored) { return null; }
    }
}
