import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Checkbox, Form } from '@arco-design/mobile-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import type { XInputSelectMutipleConfig } from './schema';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';
import '../index.css';
import './index.css';

const squareIcon = {
  normal: <IconSquareUnchecked />,
  active: <IconSquareChecked />,
  disabled: <IconSquareDisabled />,
  activeDisabled: <IconSquareChecked />
}

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
          icons={squareIcon}
          options={options}
        >
        </Checkbox.Group>
      )}
    </Form.Item>
  );
});

export default XSelectMutiple;
