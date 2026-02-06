import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import { type XautoCodeConfig } from './schema';

const XautoCode = memo((props: XautoCodeConfig & { runtime?: boolean; detailMode?: boolean; tooltipPosition: any; }) => {
  const { label, dataField, tooltip, placeholder, status, layout, runtime = true, detailMode, tooltipPosition } = props;

  const { form } = Form.useFormContext();

  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.AUTO_CODE}_${nanoid()}`;
  const fieldValue = Form.useWatch(fieldId, form);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        layout={layout}
        tooltip={ tooltip && {
          content: tooltip,
          position: tooltipPosition
        }}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        field={fieldId}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <Input
            readOnly={true}
            placeholder={placeholder}
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

export default XautoCode;
