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
  DATE_EXTREME_TYPE,
  DATE_DYNAMIC_VALUE
} from '../../../constants';
import '../index.css';
import type { XInputDateRangePickerConfig } from './schema';
import { getPopupContainer } from '@/utils';

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
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_RANGE_PICKER}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];

  // 禁用判断
  const handelDisabledDate = (current: any): boolean => {
    // 当前
    const currentDate = new Date(current);
    // 今日零点
    const today = dayjs(new Date()).format('YYYY-MM-DD') + ' 00:00:00';
    const todatTime = new Date(today).getTime();

    // 最早可选日期时间
    if (dateRange?.earliestLimit) {
      // 静态值
      const currentTime = currentDate.getTime();
      if (dateRange.earliestType === DATE_EXTREME_TYPE.STATIC && dateRange.earliestStaticValue) {
        const earliestTime = new Date(dateRange.earliestStaticValue).getTime();
        if (currentTime < earliestTime) {
          return true;
        }
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.earliestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.earliestDynamicValue) {
        const earliestTime =
          todatTime +
          (DATE_DYNAMIC_VALUE[dateRange.earliestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) *
            24 *
            3600 *
            1000;
        if (currentTime < earliestTime) {
          return true;
        }
      }
    }

    // 最晚可选日期时间
    if (dateRange?.latestLimit) {
      // 静态值
      const currentTime = currentDate.getTime();
      if (dateRange.latestType === DATE_EXTREME_TYPE.STATIC && dateRange.latestStaticValue) {
        const latestTime = new Date(dateRange.latestStaticValue).getTime();
        if (currentTime > latestTime) {
          return true;
        }
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.latestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.latestDynamicValue) {
        const latestTime =
          todatTime +
          (DATE_DYNAMIC_VALUE[dateRange.latestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) * 24 * 3600 * 1000;
        if (currentTime > latestTime) {
          return true;
        }
      }
    }

    return false;
  };

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
        rules={[{ required: verify?.required, message:`${label.text}是必填项` }]}
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
                  {ele}
                </span>
              ))}
          </div>
        ) : (
          <DatePicker.RangePicker
            disabledDate={handelDisabledDate}
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
