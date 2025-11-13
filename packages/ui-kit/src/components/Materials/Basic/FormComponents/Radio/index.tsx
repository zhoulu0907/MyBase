import { Form, Radio, Tag } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
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
    runtime = true
  } = props;

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultOptionsConfig?.defaultOptions.find(ele => ele.isChosen)?.value}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
          <div>{defaultOptionsConfig?.defaultOptions?.find((op) => op.isChosen)?.label || '--'}</div>
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
