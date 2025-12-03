import { DatePicker, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES, WEEK_OPTIONS_NUMBER, DATE_EXTREME_TYPE, DATE_DYNAMIC_VALUE } from '../../../constants';
import type { XInputDatePickerConfig } from './schema';
import { getPopupContainer } from '@/utils';
import dayjs from 'dayjs';
import '../index.css';

const { YearPicker, MonthPicker } = DatePicker;
const XDatePicker = memo((props: XInputDatePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    dateType,
    dateRange,
    layout,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = dateType || DATE_VALUES[DATE_OPTIONS.DATE];

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

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    const styles = {
      width: '100%',
      pointerEvents: (runtime ? 'auto' : 'none') as React.CSSProperties['pointerEvents']
    };
    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return <YearPicker style={styles} disabledDate={handelDisabledDate} format='YYYY' getPopupContainer={getPopupContainer} />;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return <MonthPicker style={styles} disabledDate={handelDisabledDate} format='YYYY-MM' getPopupContainer={getPopupContainer} />;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return <DatePicker style={styles} disabledDate={handelDisabledDate} format='YYYY-MM-DD' getPopupContainer={getPopupContainer} />;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return <DatePicker showTime style={styles} disabledDate={handelDisabledDate} format="YYYY-MM-DD HH:mm:ss" getPopupContainer={getPopupContainer} />;
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} format='YYYY-MM-DD' />;
    }
  };

  const renderTime = ()=>{
    if(!fieldValue){
      return '--'
    }
    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return <>{dayjs(fieldValue).format('YYYY')}</>;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return <>{dayjs(fieldValue).format('YYYY-MM')}</>;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return <>{dayjs(fieldValue).format('YYYY-MM-DD')}</>;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return <>{dayjs(fieldValue).format('YYYY-MM-DD HH:mm:ss')}</>;
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} format='YYYY-MM-DD' />;
    }
  }

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { style: { width: 200, flex: 'unset' } } : {}}
        rules={[{ required: verify?.required, message:`${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig.customValue}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{renderTime() || '--'}</div>
        ) : (
          renderDatePicker()
        )}
      </Form.Item>
    </div>
  );
});

export default XDatePicker;
