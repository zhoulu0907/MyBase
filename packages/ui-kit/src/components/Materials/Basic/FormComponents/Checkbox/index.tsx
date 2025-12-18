import { Checkbox, Form, Space, Tag } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputCheckboxConfig } from './schema';
import { getFieldOptionsConfig } from '@/utils';
import { useAppEntityStore } from '@/signals';
import type { DictData } from '@onebase/platform-center';

const CheckboxGroup = Checkbox.Group;

const XCheckbox = memo((props: XInputCheckboxConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;
  const { mainEntity, subEntities } = useAppEntityStore();

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  const [options, setOptions] = useState<DictData[]>([]);

  useEffect(() => {
    if (dataField?.length) {
      getOptions()
    }
  }, [dataField])

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities);
    setOptions(newOptions)
  }

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Space wrap size={[4, 4]}>
            {fieldValue && fieldValue.map((ele: any, index: number) => <Tag key={index} style={{ marginBottom: '0' }}>
              {options.find((e) => e.id === ele.id)?.label}
            </Tag>)}
          </Space>
        ) : (
          <CheckboxGroup
            direction={direction}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          >
            {options.map((ele, index: number) => (
              <Checkbox key={index} value={ele.value}>
                {ele.colorType && <span
                  style={{
                    width: '8px',
                    height: '8px',
                    borderRadius: '50%',
                    background: ele.colorType,
                    display: 'inline-block',
                    marginRight: '8px'
                  }}
                ></span>}
                <span>{ele.label}</span>
              </Checkbox>
            ))}
          </CheckboxGroup>
        )}
      </Form.Item>
    </div>
  );
});

export default XCheckbox;
