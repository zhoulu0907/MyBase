import { Form, Radio, Tag } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useState, useEffect } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputRadioConfig } from './schema';
import { useAppEntityStore } from '@/signals';
import { getFieldOptionsConfig } from '@/utils';
import type { DictData } from '@onebase/platform-center';

const RadioGroup = Radio.Group;

const XRadio = memo((props: XInputRadioConfig & { runtime?: boolean; detailMode?: boolean }) => {
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
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`;
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
        field={fieldId}
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
          <div>{fieldValue?.name || options.find((op) => op.id === fieldValue?.id || op.id === fieldValue)?.label || '--'}</div>
        ) : (
          <RadioGroup
            direction={direction}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          >
            {options.map((ele, index: number) => (
              <Radio key={index} value={ele.id}>
                {ele.colorType ? <Tag
                  style={{
                    width: '8px',
                    height: '8px',
                    borderRadius: '50%',
                    background: ele.colorType,
                    display: 'inline-block',
                    marginRight: '8px'
                  }}
                ></Tag> :
                  <span>{ele.label}</span>}
              </Radio>
            ))}
          </RadioGroup>
        )}
      </Form.Item>
    </div>
  );
});

export default XRadio;
