// ===== 导入 begin =====
import React, { useState, useEffect, useMemo } from 'react';
import { Upload, Button, Message, Spin, Image, Space, Typography, Card, Form, Input, Popover } from '@arco-design/web-react';
import { IconUpload, IconCamera, IconFileImage, IconDelete, IconEye, IconCloseCircle } from '@arco-design/web-react/icon';
import { OCR_TYPES } from './constants';
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
}
// ===== 接口定义 end =====

// ===== 内部组件 begin =====
const OCRUploader = React.forwardRef((props: OCRUploaderProps, ref: any) => {
  const { value, onChange, disabled, previewEnabled, description, title, onUpload, displayMode = 'click', triggerMode = 'button' } = props;
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

  // 调试日志
  // useEffect(() => {
  //   console.log('[OCRUploader] props.value changed:', value);
  //   console.log('[OCRUploader] derived fileList:', fileList);
  // }, [value, fileList]);

  const previewSrc = useMemo(() => {
    if (fileList.length === 0) return '';
    const file = fileList[0];
    
    // 增加详细日志
    // console.log('[OCRUploader] Generating preview for file:', file);
    // console.log('[OCRUploader] File structure:', {
    //   uid: file.uid,
    //   name: file.name,
    //   hasOriginFile: !!file.originFile,
    //   originFileType: file.originFile ? typeof file.originFile : 'undefined',
    //   url: file.url
    // });

    if (file.url) return file.url;
    if (file.originFile) {
      try {
        const url = URL.createObjectURL(file.originFile);
        // console.log('[OCRUploader] Created preview URL:', url);
        return url;
      } catch (e) {
        console.error('[OCRUploader] Failed to create object URL:', e);
        return '';
      }
    }
    return '';
  }, [fileList]);

  const handleUpload = async (files: any[]) => {
    // console.log('[OCRUploader] handleUpload triggered:', files);
    if (files.length === 0) return;
    const fileItem = files[0];
    
    // 立即更新 UI
    const newValue = {
      file: fileItem,
      result: null
    };
    
    // console.log('[OCRUploader] Updating local state and calling onChange:', newValue);
    // setFileList([fileItem]); // Controlled by value
    // setOcrResult(null);
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
      // setOcrResult(null);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = (e: React.MouseEvent) => {
    e.stopPropagation();
    // setFileList([]); // Controlled by value
    // setOcrResult(null);
    onChange?.(null);
  };

  const fileName = fileList.length > 0 ? fileList[0].name : '';
  
  // 识别结果预览内容
  const resultPreview = ocrResult ? (
    <div style={{ padding: 8, maxWidth: 300 }}>
      <div style={{ fontWeight: 600, marginBottom: 8, color: '#00B42A' }}>识别成功</div>
      {Object.entries(ocrResult).map(([key, val]: any) => (
        <div key={key} style={{ display: 'flex', fontSize: 12, marginBottom: 4 }}>
          <span style={{ color: '#86909C', width: 80, flexShrink: 0 }}>{key}:</span>
          <span style={{ flex: 1, wordBreak: 'break-all' }}>
            {typeof val === 'object' ? JSON.stringify(val) : String(val)}
          </span>
        </div>
      ))}
    </div>
  ) : null;

  return (
    <div style={{ width: '100%' }} ref={ref}>
      {fileList.length > 0 ? (
        <div style={{ position: 'relative', display: 'inline-block' }}>
          <Image
            width={100}
            height={100}
            src={previewSrc}
            alt="preview"
            preview={previewEnabled}
            error={
                <div style={{ 
                  width: 100, 
                  height: 100, 
                  background: '#F7F8FA', 
                  display: 'flex', 
                  flexDirection: 'column',
                  alignItems: 'center', 
                  justifyContent: 'center',
                  color: '#FF4D4F',
                  fontSize: 12,
                  textAlign: 'center',
                  padding: 4
                }}>
                  <div>图片加载失败</div>
                  <div style={{fontSize: 10, marginTop: 4, wordBreak: 'break-all'}}>
                    {fileList[0]?.name}
                  </div>
                </div>
              }
            />
          {loading && (
            <div style={{
              position: 'absolute',
              top: 0,
              left: 0,
              width: '100%',
              height: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              background: 'rgba(255, 255, 255, 0.7)',
              zIndex: 1
            }}>
              <Spin />
            </div>
          )}
          <div style={{ position: 'absolute', top: 0, right: 0, padding: 4, background: 'rgba(0,0,0,0.5)', borderRadius: '0 0 0 4px', zIndex: 2 }}>
            <Space>
              {ocrResult && displayMode === 'click' && (
                <Popover content={resultPreview} title="识别结果">
                  <IconEye style={{ cursor: 'pointer', color: '#fff' }} onClick={(e) => e.stopPropagation()} />
                </Popover>
              )}
              <IconCloseCircle 
                style={{ cursor: 'pointer', color: '#fff' }} 
                onClick={handleClear}
              />
            </Space>
          </div>
        </div>
      ) : (
        <Upload
          fileList={fileList}
          onChange={handleUpload}
          showUploadList={false}
          customRequest={(option) => {
            const { onSuccess } = option;
            onSuccess?.({});
          }}
          accept=".png,.jpg,.jpeg,.bmp"
          disabled={disabled}
        >
          <div 
            style={{ 
              width: 100, 
              height: 100, 
              background: '#F7F8FA', 
              border: '1px dashed #E5E6EB', 
              borderRadius: 4, 
              display: 'flex', 
              flexDirection: 'column', 
              justifyContent: 'center', 
              alignItems: 'center',
              cursor: disabled ? 'not-allowed' : 'pointer',
              color: '#86909C'
            }}
          >
             {loading ? <Spin /> : <IconCamera style={{ fontSize: 24 }} />}
             <div style={{ fontSize: 12, marginTop: 8 }}>{title || '上传图片'}</div>
          </div>
        </Upload>
      )}
      
      {description && (
        <div style={{ fontSize: 12, color: '#86909C', marginTop: 4 }}>
          {description}
        </div>
      )}

      {ocrResult && displayMode === 'list' && (
        <div style={{ marginTop: 8 }}>
          {resultPreview}
        </div>
      )}

      {ocrResult && displayMode === 'card' && (
        <div style={{ marginTop: 8 }}>
          <Card bordered title="识别结果" size="small">
            {resultPreview}
          </Card>
        </div>
      )}
    </div>
  );
});

const DualOCRUploader = React.forwardRef(({ value, onChange, frontProps, backProps, onIdentify, description }: any, ref: any) => {
  // console.log('[DualOCRUploader] Render value:', value);
  // 确保 value 是数组
  const safeValue = Array.isArray(value) ? value : [];

  const handleFrontChange = (v: any) => {
    const newValue = [...safeValue];
    newValue[0] = v;
    onChange?.(newValue);
  };

  const handleBackChange = (v: any) => {
    const newValue = [...safeValue];
    newValue[1] = v;
    onChange?.(newValue);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }} ref={ref}>
      <div>
        <Button type="primary" onClick={onIdentify}>开始识别</Button>
      </div>
      <div style={{ display: 'flex', gap: 16 }}>
        <div style={{ width: 100 }}>
          <OCRUploader 
            {...frontProps}
            value={safeValue[0]}
            onChange={handleFrontChange}
          />
        </div>
        <div style={{ width: 100 }}>
          <OCRUploader 
            {...backProps}
            value={safeValue[1]}
            onChange={handleBackChange}
          />
        </div>
      </div>
      {description && (
        <div style={{ fontSize: 12, color: '#86909C', marginTop: 4 }}>
          {description}
        </div>
      )}
    </div>
  );
});
// ===== 内部组件 end =====

// ===== 组件定义 begin =====
const PluginOCR = React.memo((props: PluginOCRProps) => {
  // ===== 外部 props begin =====
  const {
    label,
    description,
    previewEnabled,
    recognitionType,
    recognitionMode,
    displayMode = 'click',
    triggerMode = 'button',
    bindingRules,
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
    return Array.isArray(dataField) && dataField.length > 0
      ? dataField[dataField.length - 1]
      : defaultIdRef.current!;
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

  const currentType = recognitionMode === 'fixed' ? recognitionType : 'general';

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

    if (!frontVal?.file && !backVal?.file) {
      console.warn('[PluginOCR] No files found in value');
      sdk?.ui?.notify?.('warning', '请至少上传一张图片');
      return;
    }

    try {
      const frontFile = frontVal?.file?.originFile;
      const backFile = backVal?.file?.originFile;
      
      // console.log('[PluginOCR] Preparing to upload:', { frontFile, backFile });

      const res = await callOCRIdCardAPI(frontFile, backFile);
      
      // 更新结果
      const newValues = [...values];
      
      // 确保对象存在
      if (frontFile) {
        newValues[0] = { ...newValues[0], result: res };
      }
      if (backFile) {
        newValues[1] = { ...newValues[1], result: res };
      }

      // console.log('[PluginOCR] Identification success, updating value:', newValues);

      onChange?.(newValues);

      sdk?.ui?.notify?.('success', '识别成功');
    } catch (e: any) {
      console.error('[PluginOCR] Identification failed:', e);
      sdk?.ui?.notify?.('error', e.message || '识别失败');
    }
  };
  // ===== 内部事件 end =====

  const handleGeneralUpload = React.useCallback((file: any) => callOCRIdCardAPI(file), [callOCRIdCardAPI]);

  // ===== 渲染方法 begin =====
  const renderInteractiveContent = () => {
    if (currentType === 'id_card_both') {
      return (
        <div style={{ display: 'flex', alignItems: 'flex-start' }}>
          <div style={{ display: 'flex', gap: 16 }}>
            <div style={{ width: 100 }}>
              <OCRUploader 
                title="上传正面"
                previewEnabled={previewEnabled}
                value={Array.isArray(fieldValue) ? fieldValue[0] : undefined}
                onChange={(v: any) => {
                  const next = Array.isArray(fieldValue) ? [...fieldValue] : [];
                  next[0] = v;
                  onChange?.(next);
                }}
                displayMode={displayMode}
                triggerMode={triggerMode}
                onUpload={(file) => callOCRIdCardAPI(file)}
              />
            </div>
            <div style={{ width: 100 }}>
              <OCRUploader 
                title="上传反面"
                previewEnabled={previewEnabled}
                value={Array.isArray(fieldValue) ? fieldValue[1] : undefined}
                onChange={(v: any) => {
                  const next = Array.isArray(fieldValue) ? [...fieldValue] : [];
                  next[1] = v;
                  onChange?.(next);
                }}
                displayMode={displayMode}
                triggerMode={triggerMode}
                onUpload={(file) => callOCRIdCardAPI(file)}
              />
            </div>
          </div>
          <div style={{ flex: 1 }} />
          <div style={{ display: 'flex', justifyContent: 'flex-end', width: '100%' }}>
            <Button type="primary" onClick={handleDualIdentify}>开始识别</Button>
          </div>
        </div>
      );
    }

    return (
      <div style={{ display: 'flex', alignItems: 'flex-start' }}>
        <OCRUploader
          key="single"
          description={description}
          previewEnabled={previewEnabled}
          value={fieldValue}
          onChange={onChange}
          onUpload={handleGeneralUpload}
          displayMode={displayMode}
          triggerMode={triggerMode}
        />
        <div style={{ flex: 1 }} />
        {triggerMode === 'button' && (
          <div style={{ display: 'flex', justifyContent: 'flex-end', width: '100%' }}>
            <Button
              type="primary"
              onClick={async () => {
                const file = fieldValue?.file?.originFile;
                if (!file) {
                  sdk?.ui?.notify?.('warning', '请先上传图片');
                  return;
                }
                try {
                  const res = await handleGeneralUpload(file);
                  onChange?.({ file: fieldValue?.file, result: res });
                  } catch (e: any) {
                  sdk?.ui?.notify?.('error', e?.message || '识别失败');
                }
              }}
            >
              开始识别
            </Button>
          </div>
        )}
      </div>
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
        hidden={hidden}
        style={formItemStyle(status)}
        triggerPropName="value"
      >
        {interactive ? renderInteractiveContent() : renderReadonlyContent()}
      </Form.Item>
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

  // 自动识别：双面模式下，当两面均选择文件且触发模式为 auto 时，自动识别
  useEffect(() => {
    if (currentType !== 'id_card_both' || triggerMode !== 'auto') return;
    const values = Array.isArray(fieldValue) ? fieldValue : [];
    const frontFile = values[0]?.file?.originFile;
    const backFile = values[1]?.file?.originFile;
    if (frontFile || backFile) {
      handleDualIdentify();
    }
  }, [fieldValue, currentType, triggerMode]);

  const setTargetFieldValue = React.useCallback((targetField: string, valueToSet: any) => {
    const segs = String(fieldId).split('.');
    const isSubRow = segs.length >= 3 && !isNaN(Number(segs[1]));
    const finalName = isSubRow ? `${segs[0]}.${segs[1]}.${targetField}` : targetField;
    form.setFieldValue(finalName, valueToSet);
  }, [fieldId, form]);

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
    const rules = Array.isArray(bindingRules) ? { bindings: bindingRules } : (bindingRules || {});
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
  }, [fieldValue, bindingRules, applyBindings, currentType]);

  return (
    <div className="formWrapper" style={wrapperStyle(width)}>
      {runtime ? renderRuntime(isInteractive) : renderBuilder()}
    </div>
  );
});
// ===== 组件定义 end =====

export default PluginOCR;
