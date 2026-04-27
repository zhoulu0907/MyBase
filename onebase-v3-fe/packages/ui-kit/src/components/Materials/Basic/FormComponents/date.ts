import dayjs from 'dayjs';
import { type FormInstance } from '@arco-design/web-react';
import {
  WEEK_OPTIONS_NUMBER,
  DATE_EXTREME_TYPE,
  DATE_DYNAMIC_VALUE,
  DATE_DYNAMIC_TYPE,
  DATE_DYNAMIC_CUSTOM_TYPE,
  DATE_DYNAMIC_CUSTOM_VALUE_TYPE
} from '../../constants';

interface DateRange {
  weekLimit?: boolean;
  week?: string[];
  earliestLimit: boolean;
  earliestType: string;
  earliestStaticValue: string;
  earliestDynamicValue: string;
  earliestCustomType: string;
  earliestCustomValue: number;
  earliestCustomValueType: dayjs.ManipulateType;
  earliestVariableValue: string;
  latestLimit: boolean;
  latestType: string;
  latestStaticValue: string;
  latestDynamicValue: string;
  latestCustomType: string;
  latestCustomValue: number;
  latestCustomValueType: dayjs.ManipulateType;
  latestVariableValue: string;
}

// 判断日期是否可以选择，ture：禁止
export const handelDisabledDate = (current: dayjs.Dayjs, dateRange: DateRange, form: FormInstance): boolean => {
  // 当前要选择的日期
  const currentDate = dayjs(current).startOf('day');
  // 今日日期
  const todayDate = dayjs(new Date()).startOf('day');

  // 特定星期
  if (dateRange.weekLimit && dateRange.week?.length) {
    const currentDay = currentDate.day();
    const flag = dateRange.week.some(
      (ele: string) => WEEK_OPTIONS_NUMBER[ele as keyof typeof WEEK_OPTIONS_NUMBER] === currentDay
    );
    if (!flag) {
      return true;
    }
  }

  // 最早可选日期
  if (dateRange.earliestLimit) {
    let earliestDate: dayjs.Dayjs | null = null;
    // 静态值
    if (dateRange.earliestType === DATE_EXTREME_TYPE.STATIC && dateRange.earliestStaticValue) {
      earliestDate = dayjs(dateRange.earliestStaticValue).startOf('day');
    }

    // 动态值
    if (dateRange.earliestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.earliestDynamicValue) {
      // 自定义，
      if (dateRange.earliestDynamicValue === DATE_DYNAMIC_TYPE.CUSTOM) {
        // 并且有值
        if (dateRange.earliestCustomType && dateRange.earliestCustomValue && dateRange.earliestCustomValueType) {
          // 当前
          if (dateRange.earliestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.CURRENT) {
            // 周第一天是周一，获取的周第一天是周日 需要额外添加一天
            earliestDate =
              dateRange.earliestCustomValueType === DATE_DYNAMIC_CUSTOM_VALUE_TYPE.WEEK
                ? todayDate.startOf(dateRange.earliestCustomValueType).add(1, 'day')
                : todayDate.startOf(dateRange.earliestCustomValueType);
          }

          // 过去
          if (dateRange.earliestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.PAST) {
            earliestDate = todayDate
              .subtract(dateRange.earliestCustomValue, dateRange.earliestCustomValueType)
              .startOf('day');
          }

          // 将来
          if (dateRange.earliestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.FUTURE) {
            earliestDate = todayDate
              .add(dateRange.earliestCustomValue, dateRange.earliestCustomValueType)
              .startOf('day');
          }
        }
      } else {
        // 枚举值
        const customValue = DATE_DYNAMIC_VALUE[dateRange.earliestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0;
        earliestDate =
          customValue > 0
            ? todayDate.add(customValue, 'day').startOf('day')
            : todayDate.subtract(-customValue, 'day').startOf('day');
      }
    }

    // 变量
    if (dateRange.earliestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.earliestVariableValue) {
      const earliestVariableValue = form.getFieldValue(dateRange.earliestVariableValue);
      if (earliestVariableValue) {
        earliestDate = dayjs(earliestVariableValue).startOf('day');
      }
    }

    // 是否在当前可选日期之前
    if (earliestDate && currentDate.isBefore(earliestDate)) {
      return true;
    }
  }

  // 最晚可选日期
  if (dateRange?.latestLimit) {
    let latestDate: dayjs.Dayjs | null = null;
    // 静态值
    if (dateRange.latestType === DATE_EXTREME_TYPE.STATIC && dateRange.latestStaticValue) {
      latestDate = dayjs(dateRange.latestStaticValue).startOf('day');
    }

    // 动态值
    if (dateRange.latestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.latestDynamicValue) {
      // 自定义
      if (dateRange.latestDynamicValue === DATE_DYNAMIC_TYPE.CUSTOM) {
        // 并且有值
        if (dateRange.latestCustomType && dateRange.latestCustomValue && dateRange.latestCustomValueType) {
          // 当前
          if (dateRange.latestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.CURRENT) {
            // 周第一天是周一，获取的周第一天是周日 需要额外添加一天
            latestDate =
              dateRange.latestCustomValueType === DATE_DYNAMIC_CUSTOM_VALUE_TYPE.WEEK
                ? todayDate.endOf(dateRange.latestCustomValueType).add(1, 'day')
                : todayDate.endOf(dateRange.latestCustomValueType);
          }

          // 过去
          if (dateRange.latestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.PAST) {
            latestDate = todayDate
              .subtract(dateRange.latestCustomValue, dateRange.latestCustomValueType)
              .startOf('day');
          }

          // 将来
          if (dateRange.latestCustomType === DATE_DYNAMIC_CUSTOM_TYPE.FUTURE) {
            latestDate = todayDate.add(dateRange.latestCustomValue, dateRange.latestCustomValueType).startOf('day');
          }
        }
      } else {
        // 枚举值
        const customValue = DATE_DYNAMIC_VALUE[dateRange.latestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0;
        latestDate =
          customValue > 0
            ? todayDate.add(customValue, 'day').startOf('day')
            : todayDate.subtract(-customValue, 'day').startOf('day');
      }
    }

    // 变量
    if (dateRange.latestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.latestVariableValue) {
      const latestVariableValue = form.getFieldValue(dateRange.latestVariableValue);
      if (latestVariableValue) {
        latestDate = dayjs(latestVariableValue).startOf('day');
      }
    }

    // 是否在当前可选日期之后
    if (latestDate && currentDate.isAfter(latestDate)) {
      return true;
    }
  }

  return false;
};
