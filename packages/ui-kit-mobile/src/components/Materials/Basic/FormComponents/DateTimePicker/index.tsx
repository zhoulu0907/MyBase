import { memo } from 'react';
import { nanoid } from 'nanoid';
import dayjs from 'dayjs';
import { DatePicker, Ellipsis, Form } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';

import {
  FORM_COMPONENT_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  DEFAULT_VALUE_TYPES,
  FormSchema,
  DATE_EXTREME_TYPE,
  DATE_DYNAMIC_VALUE
} from '@onebase/ui-kit';
type XDateTimePickerConfig = typeof FormSchema.XDateTimePickerSchema.config;
import '../index.css';

const XDateTimePicker = memo((props: XDateTimePickerConfig & { runtime?: boolean; detailMode?: boolean; form?: any }) => {
  const {
    form,
    label,
    dataField,
    status,
    defaultValueConfig,
    dateRange,
    verify,
    layout,
    runtime = true,
    detailMode
  } = props;

  const textAlign = layout === 'vertical' ? 'left' : 'right';

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.DATE_TIME_PICKER}_${nanoid()}`;

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`
    }
  ];

  // 时间范围判断
  const dateSelectRange = () => {
    const initStart = new Date(1900, 0, 1).getTime();
    const initEnd = new Date(2099, 11, 31).getTime();
    let validDate = { startTs: initStart, endTs: initEnd };
    // 今日零点
    const today = dayjs(new Date()).format('YYYY-MM-DD') + ' 00:00:00';
    const todatTime = new Date(today).getTime();

    // 最早可选日期时间
    if (dateRange?.earliestLimit) {
      // 静态值
      if (dateRange.earliestType === DATE_EXTREME_TYPE.STATIC && dateRange.earliestStaticValue) {
        const earliestTime = new Date(dateRange.earliestStaticValue).getTime()
        validDate.startTs = earliestTime
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.earliestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.earliestDynamicValue) {
        const earliestTime = todatTime + (DATE_DYNAMIC_VALUE[dateRange.earliestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) * 24 * 3600 * 1000
        validDate.startTs = earliestTime
      }

      // 变量
      if (dateRange.earliestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.earliestVariableValue) {
        const earliestVariableValue = form?.getFieldValue(dateRange.earliestVariableValue);
        if (earliestVariableValue) {
          const earliestTime = new Date(earliestVariableValue).getTime()
          validDate.startTs = earliestTime
        }
      }
    }

    // 最晚可选日期时间
    if (dateRange?.latestLimit) {
      // 静态值
      if (dateRange.latestType === DATE_EXTREME_TYPE.STATIC && dateRange.latestStaticValue) {
        const latestTime = new Date(dateRange.latestStaticValue).getTime()
        validDate.endTs = latestTime
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.latestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.latestDynamicValue) {
        const latestTime = todatTime + (DATE_DYNAMIC_VALUE[dateRange.latestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) * 24 * 3600 * 1000
        validDate.endTs = latestTime
      }

      // 变量
      if (dateRange.latestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.latestVariableValue) {
        const latestVariableValue = form?.getFieldValue(dateRange.latestVariableValue)
        if (latestVariableValue) {
          const latestTime = new Date(latestVariableValue).getTime()
          validDate.endTs = latestTime
        }
      }
    }

    return {
      ...validDate
    }
  };

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      field={fieldId}
      rules={rules}
      layout={layout}
      initialValue={form?.getFieldValue(fieldId)}
      style={{
        textAlign,
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{form?.getFieldValue(fieldId) ? dayjs(form?.getFieldValue(fieldId)).format('YYYY-MM-DD hh:mm:ss') : '--'}</div>
      ) : (
        <DatePicker
          title={label.text}
          maskClosable
          // typeArr={['year', 'month', 'date', 'hour', 'minute']}
          mode='datetime'
          minTs={dateSelectRange().startTs}
          maxTs={dateSelectRange().endTs}
          formatter={(value, type) => {
            const map = {
              year: '年',
              month: '月',
              date: '日',
              hour: '时',
              minute: '分',
              second: '秒',
            };
            return `${value}${map[type] || ''}`;
          }}
        />
      )}
    </Form.Item>
  );
});

export default XDateTimePicker;
