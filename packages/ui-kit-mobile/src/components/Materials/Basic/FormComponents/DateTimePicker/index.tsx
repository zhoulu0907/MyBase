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
  FormSchema
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

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      field={fieldId}
      rules={rules}
      layout={layout}
      initialValue={form?.getFieldValue(fieldId)}
      style={{
        textAlign: layout === 'vertical' ? 'left' : 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{dayjs(form?.getFieldValue(fieldId)).format('YYYY-MM-DD hh:mm:ss')}</div>
      ) : (
        <DatePicker
          title={label.text}
          maskClosable
          // typeArr={['year', 'month', 'date', 'hour', 'minute']}
          mode='datetime'
          minTs={new Date(1900, 0, 1).getTime()}
          maxTs={new Date(2099, 11, 31).getTime()}
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
