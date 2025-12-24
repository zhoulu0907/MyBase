import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { DatePicker, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES, DATE_TIME_FORMAT, DEFAULT_VALUE_TYPES } from '../../../constants';
import '../index.css';
import type { XInputDateTimePickerConfig } from './schema';
import { getPopupContainer, securityEncodeText } from '@/utils';
import dayjs from 'dayjs';
import { handelDisabledDate } from '../date';

const XDateTimePicker = memo((props: XInputDateTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    dateRange,
    dateType,
    verify,
    layout,
    runtime = true,
    detailMode,
    security
  } = props;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_TIME_PICKER}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.DATE_TIME_PICKER}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue ? securityEncodeText(security, dayjs(fieldValue).format(DATE_TIME_FORMAT[dateType])):'--'}</div>
        ) : (
          <DatePicker
            showTime
            format={DATE_TIME_FORMAT[dateType]}
            getPopupContainer={getPopupContainer}
            disabledDate={(current) => {
              return handelDisabledDate(current, dateRange, form)
            }}
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

export default XDateTimePicker;
