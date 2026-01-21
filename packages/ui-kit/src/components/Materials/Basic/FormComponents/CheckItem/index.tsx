// ===== 导入 begin =====
import { Form, Checkbox, Switch, Radio } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';

import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES, SHOW_MODE_TYPES } from '../../../constants';
import { type XCheckItemConfig } from './schema';
import { useFormField } from '../useFormField';
import '../index.css';
// ===== 导入 end =====

const XCheckItem = memo((props: XCheckItemConfig & { runtime?: boolean; detailMode?: boolean }) => {
  // ===== 外部 props begin =====
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    showMode,
    verify,
    align,
    layout,
    runtime = true,
    detailMode
  } = props;
  // ===== 外部 props end =====

  // ===== 内部状态 & 回显begin =====
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHECK_ITEM}_${nanoid()}`;
  // =====  内部状态 & 回显 end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const { fieldValue } = useFormField(dataField, nanoid(), FORM_COMPONENT_TYPES.RATEs);
  // ===== 表单上下文与字段名与值读取 end =====

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        triggerPropName={showMode.type === SHOW_MODE_TYPES.CHECKBOX || showMode.type === SHOW_MODE_TYPES.SWITCH ? 'checked' : undefined}
        initialValue={
          defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined
        }
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {showMode.type === SHOW_MODE_TYPES.CHECKBOX && <Checkbox disabled>{showMode.checkText}</Checkbox>}
            {showMode.type === SHOW_MODE_TYPES.SWITCH && (fieldValue ? '开启' : '关闭')}
            {showMode.type === SHOW_MODE_TYPES.WHETHER &&
              (fieldValue ? showMode.yesText || '是' : showMode.noText || '否')}
          </div>
        ) : (
          <>
            {showMode.type === SHOW_MODE_TYPES.CHECKBOX && <Checkbox>{showMode.checkText}</Checkbox>}
            {showMode.type === SHOW_MODE_TYPES.SWITCH && <Switch />}
            {showMode.type === SHOW_MODE_TYPES.WHETHER && (
              <Radio.Group>
                <Radio value={true}>{showMode.yesText || '是'}</Radio>
                <Radio value={false}>{showMode.noText || '否'}</Radio>
              </Radio.Group>
            )}
          </>
        )}
      </Form.Item>
    </div>
  );
});

export default XCheckItem;
