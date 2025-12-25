import { memo } from 'react';
import { nanoid } from 'nanoid';
import dayjs from 'dayjs';
import { DatePicker, Ellipsis, Form } from '@arco-design/mobile-react';
import { ItemType } from '@arco-design/mobile-react/cjs/date-picker';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FORM_COMPONENT_TYPES, DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES, FormSchema, DATE_EXTREME_TYPE, DATE_DYNAMIC_VALUE } from '@onebase/ui-kit';
type XDatePickerConfig = typeof FormSchema.XDatePickerSchema.config;
import '../index.css';

const XDatePicker = memo((props: XDatePickerConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    label,
    dataField,
    status,
    verify,
    dateType,
    align,
    layout,
    runtime = true,
    detailMode,
    form,
    dateRange,
    defaultValueConfig
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`

  const currentDateType = dateType || DATE_VALUES[DATE_OPTIONS.DATE];

  // 时间范围判断
  const dateSelectRange = () => {
    let earliestDate, latestDate;
    // 今日零点
    const today = dayjs(new Date()).format('YYYY-MM-DD') + ' 00:00:00';
    const todatTime = new Date(today).getTime();

    // 最早可选日期时间
    if (dateRange?.earliestLimit) {
      // 静态值
      if (dateRange.earliestType === DATE_EXTREME_TYPE.STATIC && dateRange.earliestStaticValue) {
        const earliestTime = new Date(dateRange.earliestStaticValue).getTime()
        earliestDate = earliestTime
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.earliestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.earliestDynamicValue) {
        const earliestTime = todatTime + (DATE_DYNAMIC_VALUE[dateRange.earliestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) * 24 * 3600 * 1000
        earliestDate = earliestTime
      }

      // 变量
      if (dateRange.earliestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.earliestVariableValue) {
        const earliestVariableValue = form?.getFieldValue(dateRange.earliestVariableValue);
        if (earliestVariableValue) {
          const earliestTime = new Date(earliestVariableValue).getTime()
          earliestDate = earliestTime
        }
      }
    }

    // 最晚可选日期时间
    if (dateRange?.latestLimit) {
      // 静态值
      if (dateRange.latestType === DATE_EXTREME_TYPE.STATIC && dateRange.latestStaticValue) {
        const latestTime = new Date(dateRange.latestStaticValue).getTime()
        latestDate = latestTime
      }

      // 动态值  DATE_DYNAMIC_VALUE  DATE_DYNAMIC_TYPE
      if (dateRange.latestType === DATE_EXTREME_TYPE.DYNAMIC && dateRange.latestDynamicValue) {
        const latestTime = todatTime + (DATE_DYNAMIC_VALUE[dateRange.latestDynamicValue as keyof typeof DATE_DYNAMIC_VALUE] || 0) * 24 * 3600 * 1000
        latestDate = latestTime
      }

      // 变量
      if (dateRange.latestType === DATE_EXTREME_TYPE.VARIABLE && dateRange.latestVariableValue) {
        const latestVariableValue = form?.getFieldValue(dateRange.latestVariableValue)
        if (latestVariableValue) {
          const latestTime = new Date(latestVariableValue).getTime()
          latestDate = latestTime
        }
      }
    }

    return {
      earliestDate: earliestDate || new Date(1900, 0, 1).getTime(),
      latestDate: latestDate || new Date(2099, 11, 31).getTime(),
    }
  };

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    let mode: ItemType[] = [];
    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        mode.push('year');
        break;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        mode.push('year', 'month');
        break;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        mode.push('year', 'month', 'date');
        break;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        mode.push('year', 'month', 'date', 'hour', 'minute');
        break;
      default:
        mode.push('year', 'month', 'date');
    };

    return (
      <DatePicker
        title={label.text}
        typeArr={mode}
        maskClosable
        minTs={dateSelectRange().earliestDate}
        maxTs={dateSelectRange().latestDate}
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
        contentStyle={{ marginTop: layout === 'vertical' ? '0.3rem' : 0 }}
      />
    )
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      field={fieldId}
      rules={rules}
      layout={layout}
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      initialValue={form?.getFieldValue(fieldId)}
      style={{
        textAlign: layout === 'vertical' ? 'left' : 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{dayjs(form?.getFieldValue(fieldId)).format('YYYY-MM-DD')}</div>
      ) : (
        renderDatePicker()
      )}
    </Form.Item>
  );
});

export default XDatePicker;
