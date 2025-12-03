import { Checkbox, Form, Space, Tag } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, COLOR_MODE_TYPES } from '../../../constants';
import '../index.css';
import type { XInputCheckboxConfig } from './schema';

const CheckboxGroup = Checkbox.Group;

const XCheckbox = memo((props: XInputCheckboxConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultOptionsConfig,
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`

  const fieldValue = Form.useWatch(fieldId, form);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { style: { width: 200, flex: 'unset' } } : {}}
        rules={[{ required: verify?.required, message:`${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultOptionsConfig?.defaultOptions.filter(ele => ele.isChosen)?.map(ele => ele.value)}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Space wrap>
            {fieldValue && defaultOptionsConfig?.defaultOptions && typeof fieldValue === 'string' && fieldValue.split(',').map((ele: any, index: number) => <Tag key={index}>
              {defaultOptionsConfig?.defaultOptions.find((e: any) => e.value === ele)?.label}
            </Tag>)}
          </Space>
        ) : (
          <CheckboxGroup
            direction={direction}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          >
            {defaultOptionsConfig?.defaultOptions.map((ele, index: number) => (
              <Checkbox key={index} value={ele.value}>
                {defaultOptionsConfig.colorMode ? <>
                  {defaultOptionsConfig.colorModeType === COLOR_MODE_TYPES.TAG ?
                    <Tag color={ele.colorType || "rgb(var(--primary-7))"}>{ele.label}</Tag> : <>
                      <span
                        style={{
                          width: '8px',
                          height: '8px',
                          borderRadius: '50%',
                          background: ele.colorType || "rgb(var(--primary-7))",
                          display: 'inline-block',
                          marginRight: '8px'
                        }}
                      ></span>
                      <span>{ele.label}</span>
                    </>}
                </> : <>{ele.label}</>}
              </Checkbox>
            ))}
          </CheckboxGroup>
        )}
      </Form.Item>
    </div>
  );
});

export default XCheckbox;
