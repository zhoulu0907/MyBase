import { memo } from 'react';
import { nanoid } from 'nanoid';
import { DatePicker, Form } from '@arco-design/mobile-react';
import { ItemType } from '@arco-design/mobile-react/cjs/date-picker';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FORM_COMPONENT_TYPES, DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
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
    defaultValueConfig
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`

  const currentDateType = dateType || DATE_VALUES[DATE_OPTIONS.DATE];

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
    )
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      type: ValidatorType.Custom,
      validator: (value, callback) => {
        if (!value && verify?.required) {
          callback(`${label.text}是必填项`);
        }
      }
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      field={fieldId}
      rules={rules}
      label={label.display && label.text}
      initialValue={form?.getFieldValue(fieldId)}
      style={{
        textAlign: align,
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div>--</div>
      ) : (
        renderDatePicker()
      )}
    </Form.Item>
  );
});

export default XDatePicker;
