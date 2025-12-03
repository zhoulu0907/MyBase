import { memo } from 'react';
import { DatePicker, Form } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema } from '@onebase/ui-kit';
import '../index.css';

type XTimePickerConfig = typeof FormSchema.XTimePickerSchema.config;

const XTimePicker = memo((props: XTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    status,
    defaultValueConfig,
    timeRange,
    dateType,
    use24Hours,
    verify,
    layout,
    runtime = true,
    detailMode
  } = props;

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
      field=''
      rules={rules}
      label={label.display && label.text}
      required={verify?.required}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
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
          mode={"time"}
          title={label.text}
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
      )}
    </Form.Item>
  );
});

export default XTimePicker;
