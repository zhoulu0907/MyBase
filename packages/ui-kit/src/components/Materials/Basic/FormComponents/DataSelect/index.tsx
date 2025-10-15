import { Button, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import PreviewDataSelectModal from './previewDataSelectModal';
import { XDataSelectConfig } from './schema';

const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    labelColSpan = 0,
    description,
    runtime,
    displayFields
  } = props;

  const [previewDataSelectVisible, setPreviewDataSelectVisible] = useState(false); //预览数据选择popup

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATA_SELECT}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
          pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
          margin: '0px'
        }}
      >
        <Button
          type="secondary"
          long
          style={{
            pointerEvents: runtime ? 'unset' : 'none'
          }}
          onClick={() => setPreviewDataSelectVisible(true)}
        >
          {defaultValue}
        </Button>
        <PreviewDataSelectModal
          visible={previewDataSelectVisible}
          onCancel={() => setPreviewDataSelectVisible(false)}
          tableConfig={props.dynamicTableConfig}
        />
      </Form.Item>
      <div style={{ marginTop: '16px', background: '#f7f8fa' }}>
        {displayFields.map((field) => (
          <Form.Item
            key={field.label}
            label={field.label}
            labelCol={{ style: { width: labelColSpan, flex: 'unset' } }}
            wrapperCol={{ style: { flex: 1 } }}
          >
            <span style={{ color: '#c9cdd4' }}>暂无内容</span>
          </Form.Item>
        ))}
      </div>
    </div>
  );
});

export default XDataSelect;
