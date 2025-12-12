import { memo } from 'react';
import { nanoid } from 'nanoid';
import { DatePicker, Form } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';

import {
  FORM_COMPONENT_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  DEFAULT_VALUE_TYPES,
  FormSchema
} from '@onebase/ui-kit';
type XDateTimePickerConfig = typeof FormSchema.XDateTimePickerSchema.config;
import '../index.css';

const XDateTimePicker = memo((props: XDateTimePickerConfig & { runtime?: boolean; detailMode?: boolean; form?: any }) => {
  const {
    label,
    dataField,
    status,
    defaultValueConfig,
    dateRange,
    verify,
    layout,
    runtime = true,
    detailMode,
    form
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.DATE_TIME_PICKER}_${nanoid()}`;

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
      label={label.display && label.text}
      field={fieldId}
      rules={rules}
      initialValue={form?.getFieldValue(fieldId)}
      style={{
        textAlign: 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div>--</div>
      ) : (
        <DatePicker
          title={label.text}
          maskClosable
          // typeArr={['year', 'month', 'date', 'hour', 'minute']}
          mode='datetime'
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
