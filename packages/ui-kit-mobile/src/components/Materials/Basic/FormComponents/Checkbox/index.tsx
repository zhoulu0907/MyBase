import { Cell, Checkbox } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputCheckboxConfig } from './schema';
import styles from './index.module.css';
const CheckboxGroup = Checkbox.Group;

const XCheckbox = memo((props: XInputCheckboxConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    direction,
    labelColSpan = 0,
    // allChecked,
    runtime = true,
    detailMode
  } = props;

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_MUTIPLE}_${nanoid()}`

  return (
    <div className="formWrapper">
      <Cell
        label={label.display && label.text}
        className={styles.checkboxCell}
      >
        <CheckboxGroup
          defaultValue={defaultValue?.filter((op) => op.chosen).map((op) => op.value)}
          options={defaultValue}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Cell>

      {/* <Form.Item
        label={label.display && label.text}
        field={fieldId}
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
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Space wrap>
            {fieldValue && defaultValue && fieldValue.map((ele: any, index: number) => <Tag key={index}>
              {defaultValue.find((e: any) => e.value === ele)?.label}
            </Tag>)}
          </Space>
        ) : (
          <CheckboxGroup
            defaultValue={defaultValue?.filter((op) => op.chosen).map((op) => op.value)}
            options={defaultValue}
            direction={direction}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XCheckbox;
