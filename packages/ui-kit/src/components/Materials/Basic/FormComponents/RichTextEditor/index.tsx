import '@wangeditor/editor/dist/css/style.css'; // 引入 css

import { Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useMemo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES } from '../../../constants';
import '../index.css';
import WangEditor from './editor';
import type { XRichTextConfig } from './schema';

const XRichText = memo((props: XRichTextConfig & { runtime?: boolean; detailMode?: boolean; tooltipPosition: any; }) => {
  const {
    align,
    label,
    dataField,
    tooltip,
    tooltipPosition,
    status,
    defaultValueConfig = '',
    verify,
    layout,
    placeholder,
    detailMode,
    runtime = true
  } = props;

  const { form } = Form.useFormContext();

  const fallbackFieldName = useMemo(() => `${FORM_COMPONENT_TYPES.RICH_TEXT}_${nanoid()}`, []);
  const fieldName = dataField.length > 0 ? dataField[dataField.length - 1] : fallbackFieldName;
  const fieldValue = Form.useWatch(fieldName, form);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldName}
        layout={layout}
        validateTrigger="onSubmit"
        tooltip={tooltip && {
          content: tooltip,
          position: tooltipPosition
        }}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[
          {
            required: verify?.required,
            validator: (value, callback) => {
              // 非必填时，不做校验
              if (!verify?.required) {
                callback();
                return;
              }
              const html = value || '';
              const text = html
                .replace(/<[^>]+>/g, '')
                .replace(/&nbsp;/g, '')
                .trim();
              if (!text) {
                callback(`${label.text}是必填项`);
              }
            }
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div dangerouslySetInnerHTML={{ __html: defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : '' }}></div>
        ) : (
          <WangEditor
            value={fieldValue}
            align={align}
            defaultValueConfig={defaultValueConfig}
            placeholder={placeholder}
            runtime={runtime}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XRichText;
