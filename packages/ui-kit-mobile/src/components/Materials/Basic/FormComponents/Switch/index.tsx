import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Switch, Form, Ellipsis } from '@arco-design/mobile-react';
import { FormInternalComponentType } from '@arco-design/mobile-react/esm/form';
import { getSystem } from '@arco-design/mobile-utils';
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

  const systemInfo = getSystem();

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.SWITCH}_${nanoid()}`;

  const [switchChecked, setSwitchChecked] = useState(form?.getFieldValue(fieldId));

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Switch组件
    return (
      <div
        onClick={handleSwitchClick}
        style={{ display: 'flex', cursor: 'pointer' }}
      >
        <Switch
          platform={systemInfo === 'ios' ? 'ios' : 'android'}
          text={{ on: fillText?.display ? fillText.checkedText : '', off: fillText?.display ? fillText.uncheckedText : '' }}
          checked={switchChecked}
          onChange={(value) => {
            setSwitchChecked(value);
            form.setFieldValue(fieldId, value);
          }}
        />
      </div>
    );
  };

  const handleSwitchClick = (e: any) => {
    const isPC = !('ontouchstart' in window);
    if (isPC) {
      e.stopPropagation();
      const newValue = !switchChecked;
      setSwitchChecked(newValue);
      form.setFieldValue(fieldId, newValue);
    }
  }

  return (
    <Form.Item
      className={`inputTextWrapperOBMobile switchWrapperOBMobile ${layout === 'vertical' ? 'verticalLayout' : ''}`}
      field={fieldId}
      layout={layout}
      trigger='checked'
      displayType={FormInternalComponentType.Switch}
      label={label.display ? <Ellipsis text={label.text} maxLine={2} /> : undefined}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : false}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染文本内容
        <div className="readonlyText">{form?.getFieldValue(fieldId)
          ? (fillText?.display && fillText.checkedText) || '开启'
          : (fillText?.display && fillText.uncheckedText) || '关闭'}</div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XSwitch;
