// ===== 导入 begin =====
import React, { useState, useEffect, useMemo } from 'react';
import { Upload, Button, Message, Spin, Image, Space, Typography, Card, Form, Input, Popover } from '@arco-design/web-react';
import { IconUpload, IconCamera, IconFileImage, IconDelete, IconEye, IconCloseCircle, IconPlus, IconImage, IconEdit } from '@arco-design/web-react/icon';
import { OCR_TYPES } from './constants';
import './index.css';
import {
  genId,
  WIDTH_VALUES,
  WIDTH_OPTIONS,
  STATUS_OPTIONS,
  STATUS_VALUES,
  isHidden,
  computeInteractive,
  formItemStyle,
  wrapperStyle
} from '@ob/plugin/sdk';
// ===== 导入 end =====

// ===== 接口定义 begin =====
interface PluginOCRProps {
  sdk: any;
  value?: any;
  onChange?: (value: any) => void;
  [key: string]: any;
}

interface OCRUploaderProps {
  value?: any;
  onChange?: (value: any) => void;
  disabled?: boolean;
  previewEnabled?: boolean;
  description?: string;
  title?: string;
  onUpload?: (file: File) => Promise<any>;
  displayMode?: 'click' | 'list' | 'card';
  triggerMode?: 'auto' | 'button';
  externalLoading?: boolean;
}
// ===== 接口定义 end =====

// ===== 内部组件 begin =====
const OCRUploader = React.forwardRef((props: OCRUploaderProps, ref: any) => {
  const { value, onChange, disabled, previewEnabled, description, title, onUpload, displayMode = 'click', triggerMode = 'button', externalLoading } = props;
  const [loading, setLoading] = useState(false);
  
  // Directly derive ocrResult from value to avoid useEffect synchronization loop
  const ocrResult = value?.result || null;

  // Compute fileList from value directly (Controlled Component)
  const fileList = useMemo(() => {
    if (value && value.file) {
      return [value.file];
    }
    return [];
  }, [value]);

  const previewSrc = useMemo(() => {
    if (fileList.length === 0) return '';
    const file = fileList[0];
    
    if (file.url) return file.url;
    if (file.originFile) {
      try {
        const url = URL.createObjectURL(file.originFile);
        return url;
      } catch (e) {
        console.error('[OCRUploader] Failed to create object URL:', e);
        return '';
      }
    }
    return '';
  }, [fileList]);

  const handleUpload = async (files: any[], file: any) => {
    // console.log('[OCRUploader] handleUpload triggered:', files);
    if (files.length === 0) return;
    const fileItem = file || files[0];
    
    // 立即更新 UI
    const newValue = {
      file: fileItem,
      result: null
    };
    
    onChange?.(newValue);
    
    setLoading(true);
    
    try {
      if (onUpload && triggerMode === 'auto') {
        const result = await onUpload(fileItem.originFile);
        onChange?.({
          file: fileItem,
          result: result
        });
      }
    } catch (error) {
      // 错误由父组件处理，这里仅重置结果
    } finally {
      setLoading(false);
    }
  };

  const handleClear = (e: React.MouseEvent) => {
    e.stopPropagation();
    onChange?.(null);
  };

  const fileName = fileList.length > 0 ? fileList[0].name : '';

  const renderUploadList = (files: any[], listProps: any) => {
    if (files.length === 0) return null;
    const file = files[0];

    if (displayMode === 'click' || displayMode === 'list') {
      return (
        <div className="uploadImgList-text">
          <div key={file.uid} className="uploadImgList-text-item">
            <div style={{ marginRight: 8, display: 'inline-flex', alignItems: 'center', height: 32 }}>
              {/* <IconFileImage style={{ fontSize: 16, color: '#165DFF' }} /> */}
              <Image
                src={previewSrc}
                width={32}
                height={32}
                preview={previewEnabled}
                alt={file.name}
                style={{ borderRadius: 2 }}
              />
              {(loading || externalLoading) && <Spin style={{ marginLeft: 8 }} />}
            </div>
            <div className="uploadImgList-text-item-name" title={file.name}>{file.name}</div>
            <div className="uploadImgList-text-item-opera">
              <IconDelete onClick={handleClear} />
            </div>
          </div>
        </div>
      );
    }

    if (displayMode === 'card') {
      return (
        <div className="uploadImgList-card">
          <div className="uploadImgList-card-item" onClick={(e) => e.stopPropagation()}>
            <div className="uploadImgList-card-item-img">
              <Image
                src={previewSrc}
                width="100%"
                height="100%"
                preview={previewEnabled}
                alt={file.name}
              />
            </div>
            {!(loading || externalLoading) && (
              <div className="uploadImgList-card-item-actions">
                <Upload
                  showUploadList={false}
                  customRequest={(option) => { option.onSuccess({}); }}
                  onChange={(f, v) => handleUpload(f, v)}
                  accept=".png,.jpg,.jpeg,.bmp"
                  disabled={disabled}
                >
                  <div className="uploadImgList-card-item-action" title="重新选择">
                    <IconEdit />
                  </div>
                </Upload>
                <div className="uploadImgList-card-item-action" onClick={handleClear}>
                  <IconDelete />
                </div>
              </div>
            )}
            {(loading || externalLoading) && (
              <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }}>
                <Spin />
              </div>
            )}
          </div>
        </div>
      );
    }
    return null;
  };

  return (
    <div className="plugin-ocr" style={{ width: '100%' }} ref={ref}>
      <Upload
        fileList={fileList}
        onChange={handleUpload}
        showUploadList={true}
        renderUploadList={renderUploadList}
        customRequest={(option) => {
          const { onSuccess } = option;
          onSuccess?.({});
        }}
        accept=".png,.jpg,.jpeg,.bmp"
        disabled={disabled}
        drag={false}
        className={fileList.length > 0 ? 'uploadHasFile' : 'uploadEmpty'}
      >
        {fileList.length === 0 ? (
          <div className="uploadTrigger">
            {(displayMode === 'click' || displayMode === 'list') && (
              <Button icon={<IconPlus />}>{title || '点击上传'}</Button>
            )}
            {displayMode === 'card' && (
              <div className="uploadTriggerPicture">
                <div className="uploadTriggerPicture-content">
                  <IconCamera style={{ fontSize: 24 }} />
                  <div className="uploadTriggerPicture-tips">{title || '图片上传'}</div>
                </div>
              </div>
            )}
          </div>
        ) : null}
      </Upload>
      
      {/* {description && (
        <div style={{ fontSize: 12, color: '#86909C', marginTop: 4 }}>
          {description}
        </div>
      )} */}
    </div>
  );
});

// ===== 内部组件 end =====

const DualUploaderWrapper = (props: any) => {
  const { value, onChange, displayMode, isListMode, wrapperWidth, previewEnabled, triggerMode, onUpload, externalLoading } = props;
  const values = Array.isArray(value) ? value : [];

  const handleChange = (index: number, v: any) => {
    const next = [...values];
    next[index] = v;
    onChange?.(next);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div style={{ display: 'flex', flexDirection: isListMode ? 'column' : 'row', gap: 16 }}>
        <div style={{ width: wrapperWidth }}>
          <OCRUploader 
            title="身份证正面"
            previewEnabled={previewEnabled}
            value={values[0]}
            onChange={(v: any) => handleChange(0, v)}
            displayMode={displayMode}
            triggerMode={triggerMode}
            onUpload={onUpload}
            externalLoading={externalLoading}
          />
        </div>
        <div style={{ width: wrapperWidth }}>
          <OCRUploader 
            title="身份证反面"
            previewEnabled={previewEnabled}
            value={values[1]}
            onChange={(v: any) => handleChange(1, v)}
            displayMode={displayMode}
            triggerMode={triggerMode}
            onUpload={onUpload}
            externalLoading={externalLoading}
          />
        </div>
      </div>
    </div>
  );
};

const SingleUploaderWrapper = (props: any) => {
  const { wrapperWidth, ...rest } = props;
  return (
    <div style={{ width: wrapperWidth }}>
      <OCRUploader {...rest} />
    </div>
  );
};

// ===== 组件定义 begin =====
const PluginOCR = React.memo((props: PluginOCRProps) => {
  // ===== 外部 props begin =====
  const {
    label,
    description,
    previewEnabled,
    displayMode = 'click',
    triggerMode = 'button',
    ocrConfig,
    status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    verify,
    width = WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    layout = 'vertical',
    runtime = true,
    detailMode,
    titleColor = 'inherit',
    dataField = [],
    tooltip,
    sdk,
    value,
    onChange
  } = props;
  const [recognizing, setRecognizing] = useState(false);

  const recognitionMode = ocrConfig?.recognitionMode || props.recognitionMode || 'fixed';
  const recognitionType = ocrConfig?.recognitionType || props.recognitionType || 'id_card_front';
  const bindingRules = ocrConfig?.bindingRules || props.bindingRules || [];
  const linkConfig = ocrConfig?.linkConfig || props.linkConfig || { linkField: '', rules: [] };

  // ===== 外部 props end =====

  // ===== 方法：帮助方法 begin =====
  const helpers = {
    getRules: () => [
      { required: !!verify?.required, message: `${label?.text ?? ''}是必填项` }
    ]
  };
  // ===== 方法：帮助方法 end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const { form } = Form.useFormContext();
  
  // 使用 useRef 确保兜底 ID 在组件生命周期内保持稳定
  const defaultIdRef = React.useRef<string>();
  if (!defaultIdRef.current) {
    defaultIdRef.current = genId('PluginOCR');
  }
  
  const fieldId = useMemo(() => {
    // Debug dataField input
    console.log('[PluginOCR] dataField raw:', dataField);
    
    let computedId = defaultIdRef.current!;

    if (Array.isArray(dataField) && dataField.length > 0) {
      const filtered = (dataField as any[]).filter((s) => 
        typeof s === 'string' && 
        s.trim().length > 0 && 
        s !== 'undefined' && 
        s !== 'null'
      );
      const last = filtered.length > 0 ? filtered[filtered.length - 1] : '';
      if (last) {
        computedId = last;
      }
    }

    // Extra guard for malformed IDs like "table.0.undefined"
    if (computedId.endsWith('.undefined')) {
      console.warn('[PluginOCR] Malformed fieldId detected:', computedId, 'Replacing suffix with defaultId');
      computedId = computedId.replace(/\.undefined$/, `.${defaultIdRef.current}`);
    } else if (computedId.endsWith('.null')) {
      console.warn('[PluginOCR] Malformed fieldId detected:', computedId, 'Replacing suffix with defaultId');
      computedId = computedId.replace(/\.null$/, `.${defaultIdRef.current}`);
    }
    
    console.log('[PluginOCR] final fieldId:', computedId);
    return computedId;
  }, [dataField]);
     
  const fieldValue = Form.useWatch(fieldId, form);

  // ===== 数据同步：初始化时将外部 props.value 同步到 Form Store =====
  const initializedRef = React.useRef(false);
  useEffect(() => {
    if (!initializedRef.current && value !== undefined && form.getFieldValue(fieldId) === undefined) {
      form.setFieldValue(fieldId, value);
      initializedRef.current = true;
    }
  }, [value, fieldId, form]);
  // ===== 数据同步结束 =====

  // ===== 表单上下文与字段名与值读取 end =====

  const resolveFieldName = React.useCallback((targetField: string) => {
    console.log('[PluginOCR] resolveFieldName', { targetField, fieldId });
    const segs = String(fieldId).split('.');
    const isSubRow = segs.length >= 2 ;
    return isSubRow ? `${segs[0]}.${segs[1]}.${targetField}` : targetField;
  }, [fieldId]);

  // 监听联动字段值
  const linkFieldRaw = linkConfig?.linkField;
  const linkFieldName = useMemo(() => {
    console.log('[PluginOCR] linkFieldRaw', linkFieldRaw);
     if (!linkFieldRaw) return '';
     return resolveFieldName(linkFieldRaw);
  }, [linkFieldRaw, resolveFieldName]);
  
  const linkedFieldValue = Form.useWatch(linkFieldName, form);

  // 计算当前的识别类型和绑定规则，使用 useMemo 避免引用不稳定导致的死循环
  const { type: currentType, rules: currentBindingRules } = useMemo(() => {
    if (recognitionMode === 'fixed') return { type: recognitionType, rules: bindingRules };
    
    if (!linkFieldRaw) return { type: 'id_card_front', rules: {} };
    
    const val = linkedFieldValue; // 使用 useWatch 监听的值
    console.log('[PluginOCR] linkedFieldValue', { linkFieldName, linkedFieldValue: val });

    const rules = Array.isArray(linkConfig?.rules) ? linkConfig.rules : [];
    const match = rules.find((r: any) => {
      const when = String(r?.whenValue ?? '');
      if (val == null) return false;
      
      // Log match process for debugging
      // console.log('[PluginOCR] matching rule:', { val, when });

      let targetVal = val;
      
      // Handle array value (e.g. multiple select or wrapped value)
      if (Array.isArray(val)) {
        targetVal = val[0];
      }

      if (targetVal && typeof targetVal === 'object') {
        targetVal = (targetVal as any)?.id ?? (targetVal as any)?.value ?? (targetVal as any)?.optionValue ?? targetVal;
      }
      
      // Normalize comparison to string to handle number/string mismatch (e.g. 1 vs "1")
      // This ensures compatibility whether runtime returns full object or just ID
      const isMatch = String(targetVal ?? '') === when;
      if (isMatch) {
        console.log('[PluginOCR] Rule matched:', { rule: r, val, targetVal, when });
      }
      return isMatch;
    });
    return { type: (match?.recognitionType || 'id_card_front'), rules: (match?.bindingRules || {}) };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [recognitionMode, recognitionType, JSON.stringify(bindingRules), JSON.stringify(linkConfig), linkedFieldValue, linkFieldRaw]);

  // 当联动导致识别类型变化时，规范化当前值结构以匹配 UI（单面 <-> 双面）
  const lastTypeRef = React.useRef<string>(currentType);
  useEffect(() => {
    console.log('[PluginOCR] recognition type changed', { prevType: lastTypeRef.current, nextType: currentType })
    if (lastTypeRef.current === currentType) return;
    const isDual = currentType === 'id_card_both';
    const prev = form.getFieldValue(fieldId);
    if (isDual) {
      const arr = Array.isArray(prev) ? prev : [];
      const normalized = [
        arr[0] || (prev && prev.file ? { file: prev.file, result: null } : undefined),
        arr[1] || undefined
      ];
      try {
        if ((import.meta as any)?.env?.DEV) {
          (sdk as any)?.debug?.log?.('ocr-type-change', { prevType: lastTypeRef.current, nextType: currentType, prevValue: prev, normalized });
        }
      } catch {}
      onChange?.(normalized);
    } else {
      const obj = Array.isArray(prev) ? prev[0] : prev;
      const normalized = obj && obj.file ? { file: obj.file, result: null } : undefined;
      try {
        if ((import.meta as any)?.env?.DEV) {
          (sdk as any)?.debug?.log?.('ocr-type-change', { prevType: lastTypeRef.current, nextType: currentType, prevValue: prev, normalized });
        }
      } catch {}
      onChange?.(normalized);
    }
    console.log('[PluginOCR] recognition type changed', { prevType: lastTypeRef.current, nextType: currentType})
    lastTypeRef.current = currentType;
  }, [currentType, fieldId, form, onChange]);

  // ===== 外部 API 调用 begin =====
  const callOCRIdCardAPI = React.useCallback(async (frontFile?: File, backFile?: File) => {
    const formData = new FormData();
    if (frontFile) formData.append('frontFile', frontFile);
    if (backFile) formData.append('backFile', backFile);

    try {
      const response = await fetch('http://10.0.104.33:8080/ocr/id_card', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`OCR request failed: ${response.status} ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      console.error('OCR Error:', error);
      throw error;
    }
  }, []);
  // ===== 外部 API 调用 end =====

  // ===== 内部事件 begin =====
  const handleDualIdentify = async () => {
    // console.log('[PluginOCR] handleDualIdentify triggered, current value:', fieldValue);
    
    const values = Array.isArray(fieldValue) ? fieldValue : [];
    const frontVal = values[0];
    const backVal = values[1];

    if (!frontVal?.file || !backVal?.file) {
      console.warn('[PluginOCR] In dual mode, both front and back files are required');
      sdk?.ui?.notify?.('warning', '请上传身份证正反面两张图片');
      return;
    }

    try {
      setRecognizing(true);
      const frontFile = frontVal?.file?.originFile;
      const backFile = backVal?.file?.originFile;
      
      // console.log('[PluginOCR] Preparing to upload:', { frontFile, backFile });

      const res = await callOCRIdCardAPI(frontFile, backFile);
      const normalizeIdCardResult = (apiRes: any) => {
        const data = apiRes?.data || {};
        const front = data?.front?.words_result || {};
        const back = data?.back?.words_result || {};
        const getWords = (obj: any, key: string) => (obj?.[key]?.words ?? '');
        const formatDate = (dateStr: string) => {
          if (!dateStr) return '';
          // Try to match YYYYMMDD
          const match = dateStr.match(/^(\d{4})(\d{2})(\d{2})$/);
          if (match) {
            return `${match[1]}-${match[2]}-${match[3]}`;
          }
          return dateStr;
        };
        const frontNorm = {
          name: getWords(front, '姓名'),
          gender: getWords(front, '性别'),
          ethnicity: getWords(front, '民族'),
          birthday: formatDate(getWords(front, '出生')),
          address: getWords(front, '住址'),
          id_number: getWords(front, '公民身份号码')
        };
        const validFrom = getWords(back, '签发日期');
        const validTo = getWords(back, '失效日期');
        const backNorm = {
          issue_authority: getWords(back, '签发机关'),
          valid_from: formatDate(validFrom),
          valid_to: formatDate(validTo)
        };
        return { front: frontNorm, back: backNorm };
      };
      const parsed = normalizeIdCardResult(res);
      
      // 更新结果
      const newValues = [...values];
      
      // 确保对象存在
      newValues[0] = { ...newValues[0], result: parsed.front };
      newValues[1] = { ...newValues[1], result: parsed.back };

      // console.log('[PluginOCR] Identification success, updating value:', newValues);

      onChange?.(newValues);

      const rulesObj = Array.isArray(currentBindingRules) ? { bindings: currentBindingRules } : (currentBindingRules || {});
      const buildAssignments = (resultObj: any, list?: any[]) => {
        const out: Record<string, any> = {};
        if (!resultObj || !Array.isArray(list)) return out;
        list.forEach((b: any) => {
          const v = resultObj?.[b?.ocrField];
          if (v !== undefined && b?.formField) {
            const final = resolveFieldName(b.formField);
            out[final] = v;
          }
        });
        return out;
      };
      const assignments = {
        ...buildAssignments(parsed.front, (rulesObj as any).bindingsFront),
        ...buildAssignments(parsed.back, (rulesObj as any).bindingsBack)
      };
      if (Object.keys(assignments).length > 0) {
        const setAll = (sdk as any)?.context?.entity?.setFieldsValue;
        if (typeof setAll === 'function') {
          try { setAll(assignments); } catch {}
        } else {
          try { (sdk as any)?.context?.events?.emit?.('set-fields', { values: assignments }); } catch {}
        }
      }

      
    } catch (e: any) {
      console.error('[PluginOCR] Identification failed:', e);
      // sdk?.ui?.notify?.('error', e.message || '识别失败');
    } finally {
      setRecognizing(false);
    }
  };
  // ===== 内部事件 end =====

  const handleSingleIdentify = async () => {
    const file = fieldValue?.file?.originFile;
    if (!file) {
      sdk?.ui?.notify?.('warning', '请先上传图片');
      return;
    }
    try {
      setRecognizing(true);
      const res = await callOCRIdCardAPI(file);
      const data = res?.data || {};
      const front = data?.front?.words_result || {};
      const getWords = (obj: any, key: string) => (obj?.[key]?.words ?? '');
      const formatDate = (dateStr: string) => {
        if (!dateStr) return '';
        // Try to match YYYYMMDD
        const match = dateStr.match(/^(\d{4})(\d{2})(\d{2})$/);
        if (match) {
          return `${match[1]}-${match[2]}-${match[3]}`;
        }
        return dateStr;
      };
      const frontNorm = {
        name: getWords(front, '姓名'),
        gender: getWords(front, '性别'),
        ethnicity: getWords(front, '民族'),
        birthday: formatDate(getWords(front, '出生')),
        address: getWords(front, '住址'),
        id_number: getWords(front, '公民身份号码')
      };
      onChange?.({ file: fieldValue?.file, result: frontNorm });

      const rulesObj = Array.isArray(currentBindingRules) ? { bindings: currentBindingRules } : (currentBindingRules || {});
      const buildAssignments = (resultObj: any, list?: any[]) => {
        const out: Record<string, any> = {};
        if (!resultObj || !Array.isArray(list)) return out;
        list.forEach((b: any) => {
          const v = resultObj?.[b?.ocrField];
          if (v !== undefined && b?.formField) {
            const final = resolveFieldName(b.formField);
            out[final] = v;
          }
        });
        return out;
      };
      const assignments = buildAssignments(frontNorm, (rulesObj as any).bindings);
      if (Object.keys(assignments).length > 0) {
        const setAll = (sdk as any)?.context?.entity?.setFieldsValue;
        if (typeof setAll === 'function') {
          try { setAll(assignments); } catch {}
        } else {
          try { (sdk as any)?.context?.events?.emit?.('set-fields', { values: assignments }); } catch {}
        }
      }
      // sdk?.ui?.notify?.('success', '识别成功');
    } catch (e: any) {
      // sdk?.ui?.notify?.('error', e?.message || '识别失败');
    } finally {
      setRecognizing(false);
    }
  };

  // ===== 渲染方法 begin =====
  const renderInteractiveContent = () => {
    const isListMode = displayMode === 'list';
    const wrapperWidth = isListMode ? '100%' : 160;

    if (currentType === 'id_card_both') {
      return (
        <DualUploaderWrapper 
          displayMode={displayMode}
          isListMode={isListMode}
          wrapperWidth={wrapperWidth}
          previewEnabled={previewEnabled}
          triggerMode={triggerMode}
          onUpload={undefined}
          externalLoading={recognizing}
        />
      );
    }

    const singleTitle = currentType === 'id_card_front' ? '身份证正面' : label?.text;

    return (
      <SingleUploaderWrapper
        wrapperWidth={wrapperWidth}
        key="single"
        title={singleTitle}
        // description={description}
        previewEnabled={previewEnabled}
        value={fieldValue}
        onChange={onChange}
        onUpload={undefined}
        displayMode={displayMode}
        triggerMode={triggerMode}
        externalLoading={recognizing}
      />
    );
  };

  const renderReadonlyContent = () => {
    return currentType === 'id_card_both' ? (
      <Space direction="vertical">
        <div>正面: {fieldValue?.[0]?.file?.name || '--'}</div>
        <div>反面: {fieldValue?.[1]?.file?.name || '--'}</div>
      </Space>
    ) : (
      <div>{fieldValue?.file?.name || '--'}</div>
    );
  };

  const renderRuntime = (interactive: boolean) => {
    const hidden = isHidden(status);

    const labelNode = (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-start', width: '100%' }}>
        <span>
          {label?.display && label?.text ? (
            <span
              className={tooltip ? 'tooltipLabelText' : 'labelText'}
              style={{ color: titleColor }}
            >
              {label.text}
            </span>
          ) : null}
        </span>
      </div>
    );

    return (
      <>
        <Form.Item
          label={labelNode}
          field={fieldId}
          layout={layout}
          tooltip={tooltip}
          labelCol={layout === 'horizontal' ? { span: 10 } : {}}
          rules={helpers.getRules()}
          hidden={hidden}
          style={formItemStyle(status)}
          triggerPropName="value"
        >
          {interactive ? renderInteractiveContent() : renderReadonlyContent()}
        </Form.Item>
        {interactive && triggerMode === 'button' && (
          <div style={{ marginTop: 8 }}>
            <Button type="primary" size="mini" onClick={currentType === 'id_card_both' ? handleDualIdentify : handleSingleIdentify}>识别</Button>
          </div>
        )}
      </>
    );
  };

  const renderBuilder = () => {
    return (
      <Form.Item
        label={
          label?.display && label?.text ? (
            <span
              className={tooltip ? 'tooltipLabelText' : 'labelText'}
              style={{ color: titleColor }}
            >
              {label.text}
            </span>
          ) : undefined
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={helpers.getRules()}
        hidden={false} // Builder always shows
        style={formItemStyle(status)}
        triggerPropName="value"
      >
        <div style={{ pointerEvents: 'none' }}>
           {renderInteractiveContent()}
        </div>
      </Form.Item>
    );
  };
  // ===== 渲染方法 end =====

  const isInteractive = computeInteractive(status, runtime, detailMode);

  // 自动识别：双面模式下，必须正反两面均已选择文件且触发模式为 auto 才识别
  useEffect(() => {
    if (currentType !== 'id_card_both' || triggerMode !== 'auto') return;
    const values = Array.isArray(fieldValue) ? fieldValue : [];
    const frontFile = values[0]?.file?.originFile;
    const backFile = values[1]?.file?.originFile;
    if (frontFile && backFile) {
      handleDualIdentify();
    }
  }, [fieldValue, currentType, triggerMode]);

  // 自动识别：单面模式下，选择文件且触发模式为 auto 时，调用单面识别
  useEffect(() => {
    if (currentType === 'id_card_both' || triggerMode !== 'auto') return;
    const file = fieldValue?.file?.originFile;
    const hasResult = !!fieldValue?.result;
    if (file && !hasResult) {
      handleSingleIdentify();
    }
  }, [fieldValue, currentType, triggerMode]);

  const setTargetFieldValue = React.useCallback((targetField: string, valueToSet: any) => {
    const segs = String(fieldId).split('.');
    const isSubRow = segs.length >= 3 && !isNaN(Number(segs[1]));
    const finalName = isSubRow ? `${segs[0]}.${segs[1]}.${targetField}` : targetField;

    // 优先使用宿主提供的实体赋值方法（如果存在）
    const setter = (sdk as any)?.context?.entity?.setFieldValue;
    if (typeof setter === 'function') {
      try {
        setter(finalName, valueToSet);
        return;
      } catch (e) {
        console.error('[PluginOCR] setFieldValue via sdk.context.entity failed, fallback to form:', e);
      }
    }

    // 默认回退到 Form Store 赋值
    const currentVal = form.getFieldValue(finalName);
    if (currentVal !== valueToSet) {
      form.setFieldValue(finalName, valueToSet);
    }
  }, [fieldId, form, sdk]);

  const applyBindings = React.useCallback((result: any, bindingsList?: any[]) => {
    if (!result || !Array.isArray(bindingsList)) return;
    bindingsList.forEach((b) => {
      if (b && b.formField && b.ocrField) {
        const v = result?.[b.ocrField];
        if (v !== undefined) setTargetFieldValue(b.formField, v);
      }
    });
  }, [setTargetFieldValue]);

  useEffect(() => {
    const rules = Array.isArray(currentBindingRules) ? { bindings: currentBindingRules } : (currentBindingRules || {});
    if (currentType === 'id_card_both') {
      const values = Array.isArray(fieldValue) ? fieldValue : [];
      const frontRes = values[0]?.result;
      const backRes = values[1]?.result;
      if (frontRes) applyBindings(frontRes, rules.bindingsFront);
      if (backRes) applyBindings(backRes, rules.bindingsBack);
    } else {
      const res = fieldValue?.result;
      if (res) applyBindings(res, rules.bindings);
    }
  }, [fieldValue, currentBindingRules, applyBindings, currentType]);

  return (
    <div className="formWrapper" style={wrapperStyle(width)}>
      {runtime ? renderRuntime(isInteractive) : renderBuilder()}
    </div>
  );
});
// ===== 组件定义 end =====

export default PluginOCR;
