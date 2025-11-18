import { Checkbox, Form, Tag } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputCheckboxConfig } from './schema';
import styles from './index.module.css';
const CheckboxGroup = Checkbox.Group;

const XCheckbox = memo((props: XInputCheckboxConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; }) => {
  const {
    cpName,
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
    defaultOptionsConfig,
    detailMode
  } = props;

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_MUTIPLE}_${nanoid()}`

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <CheckboxGroup
        className={styles.checkboxGroup}
        layout='block'
        defaultValue={defaultOptionsConfig?.defaultOptions?.filter((op) => op.chosen).map((op) => op.value)}
        options={defaultOptionsConfig?.defaultOptions}
        style={{
          pointerEvents: runtime ? 'unset' : 'none'
        }}
      />
    );
  };

  return (
    <Form.Item
      className="inputTextWrapper"
      field={fieldId}
      label={label.display && label.text}
      required={verify.required}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset'
      }}
    >
      {!runtime || detailMode ? (
        // 只读模式，渲染文本内容
        <div style={{
        }}>
          {defaultOptionsConfig?.defaultOptions.map((ele: any, index: number) => ele.isChosen && <Tag key={index}>
            {ele.label}
          </Tag>)}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XCheckbox;
