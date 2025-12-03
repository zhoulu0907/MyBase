import { memo } from 'react';
import { nanoid } from 'nanoid';
import { DatePicker, Form } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';

import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema } from '@onebase/ui-kit';
type XDateRangePickerConfig = typeof FormSchema.XDateRangePickerSchema.config;

import '../index.css';

const XDateRangePicker = memo((props: XDateRangePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    align,
    dataField,
    dateRange,
    status,
    verify,
    layout,
    startDefaultValueConfig,
    endDefaultValueConfig,
    dateType,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <DatePicker
        mode="date"
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
    );
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
      className="inputTextWrapper"
      label={label.display && label.text}
      field={fieldId}
      rules={rules}
      initialValue={[
        startDefaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? startDefaultValueConfig?.customValue : '',
        endDefaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? endDefaultValueConfig?.customValue : ''
      ]}
      style={{
        textAlign: align,
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div>
          --
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XDateRangePicker;
