import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { DatePicker, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, DATE_EXTREME_TYPE, WEEK_OPTIONS_NUMBER, DATE_DYNAMIC_VALUE } from '../../../constants';
import '../index.css';
import type { XInputDateTimePickerConfig } from './schema';
import { getPopupContainer } from '@/utils';
import dayjs from 'dayjs';

const XDateTimePicker = memo((props: XInputDateTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    dateRange,
    verify,
    layout,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_TIME_PICKER}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  // 禁用判断
  const handelDisabledDate = (current: any): boolean => {
    // 当前
    const currentDate = new Date(current);
    // 今日零点
    const today = dayjs(new Date()).format('YYYY-MM-DD') + ' 00:00:00';
    const todatTime = new Date(today).getTime();
    // 特定星期
    if (dateRange?.weekLimit && dateRange.week.length) {
      const currentDay = currentDate.getDay();
      const flag = dateRange.week.some((ele: string) => WEEK_OPTIONS_NUMBER[ele as keyof typeof WEEK_OPTIONS_NUMBER] === currentDay)
      if (!flag) {
        return true;
      }
    }

    // 最早可选日期时间
    if (dateRange?.earliestLimit) {
      // 静态值
      const currentTime = currentDate.getTime();
      if (dateRange.earliestType === DATE_EXTREME_TYPE.STATIC && dateRange.earliestStaticValue) {
        const earliestTime = new Date(dateRange.earliestStaticValue).getTime()
        if (currentTime < earliestTime) {
          return true
        }
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.earliestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.earliestDynamicValue) {
        const earliestTime = todatTime + (DATE_DYNAMIC_VALUE[dateRange.earliestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) * 24 * 3600 * 1000
        if (currentTime < earliestTime) {
          return true
        }
      }
    }

    // 最晚可选日期时间
    if (dateRange?.latestLimit) {
      // 静态值
      const currentTime = currentDate.getTime();
      if (dateRange.latestType === DATE_EXTREME_TYPE.STATIC && dateRange.latestStaticValue) {
        const latestTime = new Date(dateRange.latestStaticValue).getTime()
        if (currentTime > latestTime) {
          return true
        }
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.latestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.latestDynamicValue) {
        const latestTime = todatTime + (DATE_DYNAMIC_VALUE[dateRange.latestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) * 24 * 3600 * 1000
        if (currentTime > latestTime) {
          return true
        }
      }
    }

    return false;
  }

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
        initialValue={defaultValueConfig?.customValue}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <DatePicker
            showTime
            format="YYYY-MM-DD HH:mm:ss"
            getPopupContainer={getPopupContainer}
            disabledDate={handelDisabledDate}
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
