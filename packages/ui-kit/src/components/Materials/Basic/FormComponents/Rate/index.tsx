// ===== 导入 begin =====
import { Form, Rate } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XRateConfig } from './schema';
import { useFormFieldWatch } from '../useFormField';
import '../index.css';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XRate = memo((props:XRateConfig & { runtime?: boolean; detailMode?: boolean })=>{
    // ===== 外部 props begin =====
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    align,
    layout,
    runtime = true,
    detailMode,
  } = props;
  // ===== 外部 props end =====

  // ===== 内部状态 & 回显begin =====
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RATE}_${nanoid()}`;
  // =====  内部状态 & 回显 end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const { fieldValue } = useFormFieldWatch(dataField);
  // ===== 表单上下文与字段名与值读取 end =====

  return (<div className="formWrapper">
    <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[
          { required: verify?.required, message: `${label.text}是必填项` },
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : 0}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{String(fieldValue)}</div>
        ) : (
          <Rate
          />
        )}
      </Form.Item>
  </div>)
})

export default XRate;