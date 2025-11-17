// ===== 导入 begin =====
import { Button, Form } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import PreviewDataSelectModal from './previewDataSelectModal';
import { XDataSelectConfig } from './schema';
import { IconClose } from '@arco-design/web-react/icon';

import { useFormField } from '../useFormField';
// ===== 导入 end =====


// ===== 组件定义 begin =====
const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  // ===== 外部 props begin =====
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    labelColSpan = 0,
    runtime,
    displayFields,
    detailMode
  } = props;
  // ===== 外部 props end =====

// ===== 表单上下文与字段名与值读取 begin =====
  const { form, fieldName, fieldValue: formFieldValue } = useFormField(
    dataField,
    props.id,
    FORM_COMPONENT_TYPES.DATA_SELECT
  );
  // ===== 表单上下文与字段名与值读取 end =====

  // ===== 内部状态 begin =====
  const [previewDataSelectVisible, setPreviewDataSelectVisible] = useState(false); // 预览数据选择弹窗
  const [compValue, setCompValue] = useState<any>('');
  // ===== 内部状态 end =====

  // ===== 回显 effect begin =====
  useEffect(() => {
    if (runtime === true && formFieldValue) {
      setCompValue(formFieldValue);
    } else {
      setCompValue('');
    }
  }, [formFieldValue, runtime]);
  // ===== 回显 effect end =====

  // ===== 双向同步 effect begin =====
  useEffect(() => {
    if (!runtime) return;
    const current = form.getFieldValue(fieldName);
    if (compValue && typeof compValue === 'object') {
      if (!current || current.displayValue !== compValue.displayValue || current.selectID !== compValue.selectID) {
        form.setFieldValue(fieldName, compValue);
      }
    } else if (current) {
      form.setFieldValue(fieldName, '');
    }
  }, [runtime, compValue, fieldName, form]);
  // ===== 双向同步 effect end =====

  // ===== 事件：清除 begin =====
  const handleClear = (e: React.MouseEvent) => {
    // 阻止冒泡，避免触发按钮的 onClick（打开弹窗）
    e.stopPropagation();
    resetDisplayValue();
    if (runtime) {
      form.setFieldValue(fieldName, '');
    }
  };
  // ===== 事件：清除 end =====

  // ===== 事件：重置显示值 begin =====
  const resetDisplayValue = () => {
    setCompValue('');
  }
  // ===== 事件：重置显示值 end =====

  // ===== 事件：选择数据 begin =====
  const handleSelect = (data: any) => {
    const fieldsWithValue = (displayFields || []).map((field: any) => ({
      ...field,
      dataValue: data ? data[field.value] : null
    }));
    const lastKey = (displayFields || []).length ? displayFields[displayFields.length - 1]?.value : undefined;
    const raw = lastKey ? data?.[lastKey] : '';
    const nextValue = data ? { selectID: data.id, dataFields: fieldsWithValue, displayValue: raw } : '';
    setCompValue(nextValue);
    if (runtime) {
      form.setFieldValue(fieldName, nextValue);
    }
    if ((props as any).onSelect) {
      (props as any).onSelect({ id: data?.id, displayValue: raw, fields: fieldsWithValue });
    }
  }
  // ===== 事件：选择数据 end =====

  return (
    <div className="formWrapper">
      {/* ===== 渲染：表单项 begin ===== */}
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldName}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
          margin: '0px'
        }}
      >
        {(() => {
          // ===== 渲染：主按钮 begin =====
          const interactive = runtime && status !== STATUS_VALUES[STATUS_OPTIONS.READONLY] && !detailMode;
          const showClear = interactive && !!(compValue && typeof compValue === 'object' && compValue.displayValue);
          return (
            <Button
              type="secondary"
              long
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                pointerEvents: interactive ? 'unset' : 'none'
              }}
              onClick={() => interactive && setPreviewDataSelectVisible(true)}
            >
              <span style={{ pointerEvents: 'none' }}>{(compValue && typeof compValue === 'object' ? compValue.displayValue : '') || <span style={{ color: '#999' }}>{defaultValue}</span>}</span>
              {showClear ? (
                <span onClick={handleClear} style={{ cursor: 'pointer' }} title="清除">
                  <IconClose style={{ fontSize: 12 }} />
                </span>
              ) : null}
            </Button>
          );
          // ===== 渲染：主按钮 end =====
        })()}
      </Form.Item>
      {/* ===== 渲染：表单项 end ===== */}
      {/* ===== 渲染：预览弹窗 begin ===== */}
      <PreviewDataSelectModal
          visible={previewDataSelectVisible}
          onCancel={() => setPreviewDataSelectVisible(false)}
          tableConfig={props.dynamicTableConfig}
          onSelect={handleSelect}
        />
      {/* ===== 渲染：预览弹窗 end ===== */}
      
    </div>
  );
});
// ===== 组件定义 end =====

export default XDataSelect;
