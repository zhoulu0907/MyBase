import { Cell, Radio } from '@arco-design/mobile-react';
// import { nanoid } from 'nanoid';
import { memo } from 'react';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputRadioConfig } from './schema';
import styles from './index.module.css';

const RadioGroup = Radio.Group;

const XRadio = memo((props: XInputRadioConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultOptions,
    verify,
    layout,
    labelColSpan = 0,
    direction,
    runtime = true
  } = props;

  return (
    <div className="formWrapper">
      <Cell
        label={label.display && label.text + 1}
        className={styles.radioCell}
      >
        <RadioGroup
          options={defaultOptions}
          defaultValue={defaultOptions?.find((op) => op.chosen)?.value}
          style={{
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Cell>


      {/* <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
          <div>{defaultValue?.find((op) => op.chosen)?.label || '--'}</div>
        ) : (
          <RadioGroup
            direction={direction}
            options={defaultValue}
            defaultValue={defaultValue?.find((op) => op.chosen)?.value}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XRadio;
