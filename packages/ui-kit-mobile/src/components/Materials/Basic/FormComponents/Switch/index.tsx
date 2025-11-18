import { Switch, Form } from '@arco-design/mobile-react';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import './index.css';
import { type XInputSwitchConfig } from './schema';

const XSwitch = memo((props: XInputSwitchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    defaultValue,
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
        triggerPropName="checked"
        style={{
          pointerEvents: runtime ? 'unset' : 'none'
        }}
      />
    );
  };

  return (
    <Form.Item
      field={fieldId}
      label={label.display ? label.text : undefined}
      initialValue={defaultValue || false}
      className="inputTextWrapper switchWrapper"
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset'
      }}
    >
      {!runtime || detailMode ? (
        // 只读模式，渲染文本内容
        <div>
          {defaultValue ? '开启' : '关闭'}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XSwitch;
