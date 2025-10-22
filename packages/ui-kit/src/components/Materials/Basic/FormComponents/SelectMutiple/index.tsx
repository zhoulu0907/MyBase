import { Form, Select, Space, Tag } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import type { XInputSelectMutipleConfig } from './schema';

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
    defaultValue,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_MUTIPLE}_${nanoid()}`

  const fieldValue = Form.useWatch(fieldId, form);

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }, { maxLength: verify?.maxChecked }]}
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
          <Select
            mode="multiple"
            allowClear
            showSearch={showSearch}
            filterOption={(input, option) => {
              return option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
            placeholder="请选择"
            options={defaultValue}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XSelectMutiple;
