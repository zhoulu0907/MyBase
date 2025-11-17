import { Textarea } from '@arco-design/mobile-react';
// import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import './index.css';

import type { XInputTextAreaConfig } from './schema';

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
    minLength = 0,
    maxLength = 0,
    minRows = 0,
    maxRows,
    runtime = true,
    detailMode
  } = props;

  const [fieldId, setFieldId] = useState('');

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="inputTextAreaWrapper">
      <Textarea
        label={label.display && label.text}
        key={`${minRows}-${maxRows}`}
        defaultValue={defaultValue}
        placeholder={placeholder}
        maxLength={maxLength}
        clearable
        textareaStyle={{ height: 0.25 * minRows + 'rem' }}
        autosize={false}
        rows={minRows}
      />
      {/* <Form.Item
        label={label.display && label.text}
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
              if (minLength !== 0 && value && value.length < minLength) {
                callback(`字数不能小于${minLength}`);
              }
              if (maxLength !== 0 && value && value.length > maxLength) {
                callback(`字数不能大于${maxLength}`);
              }
            }
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <TextArea
            key={`${minRows}-${maxRows}`}
            defaultValue={defaultValue}
            placeholder={placeholder}
            maxLength={maxLength}
            allowClear
            autoSize={{
              minRows,
              maxRows
            }}
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
      </Form.Item> */}
    </div>
  );
});

export default XInputTextArea;
