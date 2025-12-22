import { DatePicker, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import {
  DATE_OPTIONS,
  DATE_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WEEK_OPTIONS_NUMBER,
  DATE_EXTREME_TYPE,
  DATE_DYNAMIC_VALUE,
  DATE_DYNAMIC_TYPE,
  DATE_DYNAMIC_CUSTOM_TYPE,
  DATE_DYNAMIC_CUSTOM_VALUE_TYPE
} from '../../../constants';
import type { XInputDatePickerConfig } from './schema';
import { getPopupContainer, securityEncodeText } from '@/utils';
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
    detailMode,
    security
  } = props;

  const { form } = Form.useFormContext();
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`;
  const fieldValue = Form.useWatch(fieldId, form);

  // 禁用判断
  const handelDisabledDate = (current: any): boolean => {
    // 当前
    const currentDate = new Date(current);
    // 今日零点
    const today = dayjs(new Date()).format('YYYY-MM-DD') + ' 00:00:00';
    const todayTime = new Date(today).getTime();
    // 特定星期
    if (dateRange?.weekLimit && dateRange.week.length) {
      const currentDay = currentDate.getDay();
      const flag = dateRange.week.some(
        (ele: string) => WEEK_OPTIONS_NUMBER[ele as keyof typeof WEEK_OPTIONS_NUMBER] === currentDay
      );
      if (!flag) {
        return true;
      }
    }

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
        // 自定义
        if (dateRange.earliestDynamicValue === DATE_DYNAMIC_TYPE.CUSTOM) {
          if (dateRange.earliestCustomType && dateRange.earliestCustomValue && dateRange.earliestCustomValueType) {
            if (dateRange.earliestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.CURRENT) {
              // 当前  周第一天是周一
              if (dateRange.earliestCustomValueType === DATE_DYNAMIC_CUSTOM_VALUE_TYPE.WEEK) {
                const earliestDay = dayjs(todayTime)
                  .startOf(dateRange.earliestCustomValueType)
                  .add(1, 'day')
                  .format('YYYY-MM-DD');
                const earliestTime = new Date(earliestDay + ' 00:00:00').getTime();
                if (currentTime < earliestTime) {
                  return true;
                }
              } else {
                const earliestDay = dayjs(todayTime).startOf(dateRange.earliestCustomValueType).format('YYYY-MM-DD');
                const earliestTime = new Date(earliestDay + ' 00:00:00').getTime();
                if (currentTime < earliestTime) {
                  return true;
                }
              }
            } else if (dateRange.earliestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.PAST) {
              // 过去
              const earliestDay = dayjs(todayTime)
                .subtract(dateRange.earliestCustomValue, dateRange.earliestCustomValueType)
                .format('YYYY-MM-DD');
              const earliestTime = new Date(earliestDay + ' 00:00:00').getTime();
              if (currentTime < earliestTime) {
                return true;
              }
            } else if (dateRange.earliestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.FUTURE) {
              // 将来
              const earliestDay = dayjs(todayTime)
                .add(dateRange.earliestCustomValue, dateRange.earliestCustomValueType)
                .format('YYYY-MM-DD');
              const earliestTime = new Date(earliestDay + ' 00:00:00').getTime();
              if (currentTime < earliestTime) {
                return true;
              }
            }
          }
        } else {
          const earliestTime =
            todayTime +
            (DATE_DYNAMIC_VALUE[dateRange.earliestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) *
              24 *
              3600 *
              1000;
          if (currentTime < earliestTime) {
            return true;
          }
        }
      }

      // 变量
      if (dateRange.earliestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.earliestVariableValue) {
        const earliestVariableValue = form.getFieldValue(dateRange.earliestVariableValue);
        if (earliestVariableValue) {
          const earliestTime = new Date(earliestVariableValue).getTime();
          if (currentTime < earliestTime) {
            return true;
          }
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
        if (dateRange.latestDynamicValue === DATE_DYNAMIC_TYPE.CUSTOM) {
          if (dateRange.latestCustomType && dateRange.latestCustomValue && dateRange.latestCustomValueType) {
            if (dateRange.latestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.CURRENT) {
              // 当前  周第一天是周一
              if (dateRange.latestCustomValueType === DATE_DYNAMIC_CUSTOM_VALUE_TYPE.WEEK) {
                const latestDay = dayjs(todayTime)
                  .endOf(dateRange.latestCustomValueType)
                  .add(1, 'day')
                  .format('YYYY-MM-DD');
                const latestTime = new Date(latestDay + ' 00:00:00').getTime();
                if (currentTime > latestTime) {
                  return true;
                }
              } else {
                const latestDay = dayjs(todayTime).endOf(dateRange.latestCustomValueType).format('YYYY-MM-DD');
                const latestTime = new Date(latestDay + ' 00:00:00').getTime();
                if (currentTime > latestTime) {
                  return true;
                }
              }
            } else if (dateRange.latestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.PAST) {
              // 过去
              const latestDay = dayjs(todayTime)
                .subtract(dateRange.latestCustomValue, dateRange.latestCustomValueType)
                .format('YYYY-MM-DD');
              const latestTime = new Date(latestDay + ' 00:00:00').getTime();
              if (currentTime > latestTime) {
                return true;
              }
            } else if (dateRange.latestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.FUTURE) {
              // 将来
              const latestDay = dayjs(todayTime)
                .add(dateRange.latestCustomValue, dateRange.latestCustomValueType)
                .format('YYYY-MM-DD');
              const latestTime = new Date(latestDay + ' 00:00:00').getTime();
              if (currentTime > latestTime) {
                return true;
              }
            }
          }
        } else {
          const latestTime =
            todayTime +
            (DATE_DYNAMIC_VALUE[dateRange.latestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) *
              24 *
              3600 *
              1000;
          if (currentTime > latestTime) {
            return true;
          }
        }
      }

      // 变量
      if (dateRange.latestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.latestVariableValue) {
        const latestVariableValue = form.getFieldValue(dateRange.latestVariableValue);
        if (latestVariableValue) {
          const latestTime = new Date(latestVariableValue).getTime();
          if (currentTime > latestTime) {
            return true;
          }
        }
      }
    }

    return false;
  };

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    const styles = {
      width: '100%',
      pointerEvents: (runtime ? 'auto' : 'none') as React.CSSProperties['pointerEvents']
    };
    switch (dateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return (
          <YearPicker
            style={styles}
            disabledDate={handelDisabledDate}
            format="YYYY"
            getPopupContainer={getPopupContainer}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return (
          <MonthPicker
            style={styles}
            disabledDate={handelDisabledDate}
            format="YYYY-MM"
            getPopupContainer={getPopupContainer}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return (
          <DatePicker
            style={styles}
            disabledDate={handelDisabledDate}
            format="YYYY-MM-DD"
            getPopupContainer={getPopupContainer}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return (
          <DatePicker
            showTime
            style={styles}
            disabledDate={handelDisabledDate}
            format="YYYY-MM-DD HH:mm:ss"
            getPopupContainer={getPopupContainer}
          />
        );
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />;
    }
  };

  const renderTime = () => {
    switch (dateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY'))}</>;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY-MM'))}</>;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY-MM-DD'))}</>;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY-MM-DD HH:mm:ss'))}</>;
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />;
    }
  };

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
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig.customValue}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue ? renderTime() : '--'}</div>
        ) : (
          renderDatePicker()
        )}
      </Form.Item>
    </div>
  );
});

export default XDatePicker;
