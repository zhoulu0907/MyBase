import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Switch, Form, Ellipsis } from '@arco-design/mobile-react';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema } from '@onebase/ui-kit';
import './index.css';
import '../index.css';

type XSwitchConfig = typeof FormSchema.XSwitchSchema.config;

const XSwitch = memo((props: XSwitchConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    status,
    defaultValueConfig,
    layout,
    fillText,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.SWITCH}_${nanoid()}`;

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Switch组件
    return (
      <Switch
        platform="android"
        text={{ on: fillText?.display ? fillText.checkedText : '', off: fillText?.display ? fillText.uncheckedText : '' }}
      />
    );
  };

  return (
    <Form.Item
      className="inputTextWrapperOBMobile switchWrapperOBMobile"
      field={fieldId}
      label={label.display ? <Ellipsis text={label.text} /> : undefined}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染文本内容
        <div className="readonlyText">{form?.getFieldValue(fieldId) ? '开启' : '关闭'}</div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XSwitch;
