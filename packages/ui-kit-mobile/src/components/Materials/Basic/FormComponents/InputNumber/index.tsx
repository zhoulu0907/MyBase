import { Input } from '@arco-design/mobile-react';
import { memo, useEffect, useState } from 'react';
import '../index.css';
import type { XInputNumberConfig } from './schema';

const XInputNumber = memo((props: XInputNumberConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    placeholder,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    align,
    step,
    layout,
    labelColSpan = 0,
    runtime = true,
    detailMode,
    numberFormat
  } = props;

  const { showUnit, unitValue, showPrecision, precision, showPercent, useThousandsSeparator } = numberFormat;

  // const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  // const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  const detailValue = (value: number) => {
    let result = '';
    if (showPercent) {
      value = value * 100;
    }
    if (showPrecision) {
      result = value.toFixed(precision);
    }
    if (useThousandsSeparator) {
      result = `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }
    if (showPercent) {
      result = `${result}%`;
    }
    if (showUnit) {
      result = `${result}${unitValue}`;
    }

    return result.toString();
  };

  return (
    <div className="inputTextWrapper">
      <Input
        label={label.display && label.text}
        type="number"
        defaultValue={defaultValue}
        placeholder={placeholder}
        maxLength={verify?.max || 1000000000}
        suffix={showUnit ? unitValue : ''}
        inputStyle={{ textAlign: align }}
        style={{
          width: '100%',
          textAlignLast: align,
          pointerEvents: runtime ? 'unset' : 'none'
        }}
      />

      {/* <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_NUMBER}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[
          {
            required: verify?.required
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{detailValue(fieldValue) || '--'}</div>
        ) : (
          <InputNumber
            defaultValue={defaultValue}
            placeholder={placeholder}
            step={step}
            min={verify?.min}
            max={verify?.max || 1000000000}
            precision={showPrecision ? precision : 0}
            formatter={(value) => {
              return useThousandsSeparator ? `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : value.toString();
            }}
            parser={(value) => value.replace(/,/g, '')}
            style={{
              width: '100%',
              textAlignLast: align,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            suffix={showUnit ? unitValue : ''}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XInputNumber;
