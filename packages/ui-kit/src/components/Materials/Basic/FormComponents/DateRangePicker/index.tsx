import { DatePicker, Form } from '@arco-design/web-react';
import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import dayjs from 'dayjs';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import {
  DATE_OPTIONS,
  DATE_VALUES,
  DATE_FORMAT,
  STATUS_OPTIONS,
  STATUS_VALUES,
  DEFAULT_VALUE_TYPES,
} from '../../../constants';
import '../index.css';
import type { XInputDateRangePickerConfig } from './schema';
import { getPopupContainer, securityEncodeText } from '@/utils';
import { handelDisabledDate } from '../date';

const XDateRangePicker = memo((props: XInputDateRangePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    dateRange,
    status,
    tooltip,
    verify,
    layout,
    startDefaultValueConfig,
    endDefaultValueConfig,
    dateType,
    runtime = true,
    detailMode,
    security
  } = props;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_RANGE_PICKER}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.DATE_RANGE_PICKER}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={[
          startDefaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? startDefaultValueConfig?.customValue : '',
          endDefaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? endDefaultValueConfig?.customValue : ''
        ]}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {fieldValue &&
              typeof fieldValue === 'string' &&
              fieldValue.split(',').map((ele: any, index: number) => (
                <span key={index} style={{ marginBottom: '0' }}>
                  {securityEncodeText(security, ele)}
                </span>
              ))}
          </div>
        ) : (
          <DatePicker.RangePicker
            disabledDate={(current) => {
              return handelDisabledDate(current, dateRange, form)
            }}
            format={DATE_FORMAT[dateType]}
            mode={currentDateType}
            showTime={dateType === DATE_VALUES[DATE_OPTIONS.FULL]}
            getPopupContainer={getPopupContainer}
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

export default XDateRangePicker;
