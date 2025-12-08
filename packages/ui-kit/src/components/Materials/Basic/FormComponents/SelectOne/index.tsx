import { getPopupContainer } from '@/utils';
import { Form, Select } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputSelectOneConfig } from './schema';

const XSelectOne = memo((props: XInputSelectOneConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, dataField, tooltip, status, verify, layout, defaultOptionsConfig, runtime = true, detailMode } = props;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`

  const fieldValue = Form.useWatch(fieldId, form);


  const handleSelectChange = (value: string) => {
    const name = defaultOptionsConfig?.defaultOptions.find((item) => item.value === value)?.label;

    form.setFieldValue(fieldId, {
      id:value,
      name
    });
  };

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { style: { width: 200, flex: 'unset' } } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultOptionsConfig?.defaultOptions.find((ele) => ele.isChosen)?.value}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {fieldValue?.name || '--'}
          </div>
        ) : (
          <Select
            placeholder="请选择"
            filterOption={(input, option) => {
              return option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
            allowClear
            options={defaultOptionsConfig?.defaultOptions}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleSelectChange(value)}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            renderFormat={(option) => {
              if (typeof fieldValue === 'object' && fieldValue) {
                return fieldValue?.name ?? '--';
              }
              return <span>{option?.children}</span>
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XSelectOne;
