import { Checkbox, Form } from '@arco-design/mobile-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import type { XInputSelectMutipleConfig } from './schema';
import '../index.css';
import './index.css';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    labelColSpan = 0,
    showSearch,
    defaultOptions,
    defaultOptionsConfig,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;

  const options = defaultOptionsConfig?.defaultOptions?.map(({ label, value }: { label: string; value: string | number }) => ({ label, value }));

  return (
    <Form.Item
      className="inputTextWrapper"
      label={label.display && label.text}
      field={fieldId}
      required={verify?.required}
      style={{
        pointerEvents: runtime ? 'unset' : 'none'
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || !runtime || detailMode ? (
        <div>{defaultOptions || '--'}</div>
      ) : (
        <Checkbox.Group
          className="selectCheckout"
          layout='block'
          options={options}
        >
        </Checkbox.Group>
      )}
    </Form.Item>
  );
});

export default XSelectMutiple;
