import { menuDictSignal, useAppEntityStore } from '@/signals';
import { getFieldOptionsConfig } from '@/utils';
import { Form, Radio, Tag } from '@arco-design/web-react';
import type { DictData } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputRadioConfig } from './schema';

const RadioGroup = Radio.Group;

const XRadio = memo((props: XInputRadioConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;
  const { mainEntity, subEntities } = useAppEntityStore();
  const { appDict } = menuDictSignal;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`;
  const fieldValue = Form.useWatch(fieldId, form);

  const [options, setOptions] = useState<DictData[]>([]);

  useEffect(() => {
    if (dataField?.length) {
      getOptions();
    }
  }, [dataField]);

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities, appDict.value);
    setOptions(newOptions);
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
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={
          defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined
        }
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {fieldValue?.name ||
              options.find((op) => op.value === fieldValue?.id || op.value === fieldValue)?.label ||
              '--'}
          </div>
        ) : (
          <RadioGroup
            direction={direction}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          >
            {options.map((ele, index: number) => (
              <Radio key={index} value={ele.value}>
                {ele.colorType ? <Tag color={ele.colorType}>{ele.label}</Tag> : <span>{ele.label}</span>}
              </Radio>
            ))}
          </RadioGroup>
        )}
      </Form.Item>
    </div>
  );
});

export default XRadio;
