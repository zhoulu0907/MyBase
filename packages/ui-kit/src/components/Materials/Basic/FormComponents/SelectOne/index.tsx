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
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.SELECT_ONE}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultOptionsConfig?.defaultOptions.find((ele) => ele.isChosen)?.value}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {(fieldValue && defaultOptionsConfig?.defaultOptions?.find((op) => op.value === fieldValue)?.label) || '--'}
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

export default XSelectOne;
