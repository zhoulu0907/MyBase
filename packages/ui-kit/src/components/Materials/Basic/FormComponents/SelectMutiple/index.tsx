import { Form, Select, Space, Tag } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import type { XInputSelectMutipleConfig } from './schema';
import { getPopupContainer } from '@/utils';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    defaultOptionsConfig,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_ONE}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { style: { width: 200, flex: 'unset' } } : {}}
        rules={[
          { required: verify?.required, message:`${label.text}是必填项` },
          { maxLength: verify?.maxChecked }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultOptionsConfig?.defaultOptions.filter(ele => ele.isChosen)?.map(ele => ele.value)}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Space wrap size={[4,4]}>
            {fieldValue && defaultOptionsConfig?.defaultOptions && typeof fieldValue === 'string' && fieldValue.split(',').map((ele: any, index: number) => <Tag key={index} style={{ marginBottom: '0' }}>
              {defaultOptionsConfig?.defaultOptions.find((e: any) => e.value === ele)?.label}
            </Tag>)}
          </Space>
        ) : (
          <Select
            mode="multiple"
            allowClear
            getPopupContainer={getPopupContainer}
            filterOption={(input, option) => {
              return option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
            placeholder="请选择"
            options={defaultOptionsConfig?.defaultOptions}
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
