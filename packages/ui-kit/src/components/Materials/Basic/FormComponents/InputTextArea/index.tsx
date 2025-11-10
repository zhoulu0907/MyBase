import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputTextAreaConfig } from './schema';

const TextArea = Input.TextArea;

const XInputTextArea = memo((props: XInputTextAreaConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValue,
    verify,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    minRows,
    maxRows,
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
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_TEXTAREA}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[
          { required: verify?.required },
          {
            validator: (value, callback) => {
              if(verify.lengthLimit){
                if (verify.minLength && value && value.length < verify.minLength) {
                  callback(`字数不能小于${verify.minLength}`);
                }
                if (verify.maxLength && value && value.length > verify.maxLength) {
                  callback(`字数不能大于${verify.maxLength}`);
                }
              }
            }
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValue}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <TextArea
            key={`${minRows}-${maxRows}`}
            placeholder={placeholder}
            maxLength={verify.lengthLimit ? verify.maxLength : undefined}
            allowClear
            rows={minRows}
            showWordLimit
            style={{
              width: '100%',
              color,
              textAlign: align,
              backgroundColor: bgColor,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XInputTextArea;
