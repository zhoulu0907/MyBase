import { Form, Picker } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputSelectOneConfig } from './schema';

const XSelectOne = memo((props: XInputSelectOneConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    labelColSpan = 0,
    showSearch,
    defaultOptionsConfig,
    runtime = true,
    detailMode
  } = props;

  const [fieldId, setFieldId] = useState('');

  useEffect(() => {
    if (dataField?.length > 0) {
      setFieldId(dataField[dataField?.length - 1]);
    }
  }, [dataField]);

  return (
    <Form.Item
      className="inputTextWrapper"
      label={label.display && label.text}
      field={fieldId}
      required={verify?.required}
      style={{
        textAlign: 'right',
        pointerEvents: runtime ? 'unset' : 'none'
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || !runtime || detailMode ? (
        <div>{defaultOptionsConfig.defaultOptions.find((item: any) => item.value == '')?.label || '--'}</div>
      ) : (
        <Picker
          cascade={false}
          data={defaultOptionsConfig.defaultOptions}
          maskClosable
        />
      )}
    </Form.Item>
  );
});

export default XSelectOne;
