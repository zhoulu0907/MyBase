import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import { type XautoCodeConfig } from './schema';

const XautoCode = memo((props: XautoCodeConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, dataField, tooltip, placeholder, status, layout, runtime = true, detailMode } = props;

  const { form } = Form.useFormContext();

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1]: `${FORM_COMPONENT_TYPES.AUTO_CODE}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        field={fieldId}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <Input
            readOnly={true}
            placeholder={placeholder}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XautoCode;
