import { Form, InputNumber } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES } from '../../../constants';
import '../index.css';
import type { XInputNumberConfig } from './schema';

const XInputNumber = memo((props: XInputNumberConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    placeholder,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    align,
    step,
    layout,
    runtime = true,
    detailMode,
    numberFormat
  } = props;

  const { showUnit, unitValue, showPrecision, precision, showPercent, useThousandsSeparator } = numberFormat;

  const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  const fieldValue = Form.useWatch(fieldId, form);

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
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_NUMBER}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
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
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{detailValue(fieldValue) || '--'}</div>
        ) : (
          <InputNumber
            placeholder={placeholder}
            step={step}
            min={verify?.numberLimit ? verify?.min : undefined}
            max={verify?.numberLimit ? verify?.max : undefined}
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
      </Form.Item>
    </div>
  );
});

export default XInputNumber;
