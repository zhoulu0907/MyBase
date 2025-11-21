import { Form, Radio, Tag } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useState, useEffect } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, COLOR_MODE_TYPES } from '../../../constants';
import '../index.css';
import type { XInputRadioConfig } from './schema';

const RadioGroup = Radio.Group;

const XRadio = memo((props: XInputRadioConfig & { runtime?: boolean; detailMode?: boolean }) => {
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
  const [fieldId, setFieldId] = useState('');

  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required, message:`${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultOptionsConfig?.defaultOptions.find(ele => ele.isChosen)?.value}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue && defaultOptionsConfig?.defaultOptions?.find((op) => op.value === fieldValue)?.label || '--'}</div>
        ) : (
          <RadioGroup
            direction={direction}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          >
            {defaultOptionsConfig?.defaultOptions.map((ele, index: number) => (
              <Radio key={index} value={ele.value}>
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
              </Radio>
            ))}
          </RadioGroup>
        )}
      </Form.Item>
    </div>
  );
});

export default XRadio;
