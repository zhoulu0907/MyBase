import { useAppEntityStore } from '@/signals';
import { getFieldOptionsConfig, getPopupContainer } from '@/utils';
import { Form, Select } from '@arco-design/web-react';
import type { DictData } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputSelectOneConfig } from './schema';

const XSelectOne = memo((props: XInputSelectOneConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, dataField, tooltip, status, verify, layout, runtime = true, detailMode, defaultValueConfig } = props;
  const { mainEntity, subEntities } = useAppEntityStore();

  const { form } = Form.useFormContext();
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_ONE}_${nanoid()}`;
  const fieldValue = Form.useWatch(fieldId, form);

  const [options, setOptions] = useState<DictData[]>([]);

  useEffect(() => {
    if (dataField?.length) {
      getOptions();
    }
  }, [dataField]);

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities);
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
            {fieldValue?.name || options.find((op) => op.id === fieldValue?.id || op.id === fieldValue)?.label || '--'}
          </div>
        ) : (
          <Select
            placeholder="请选择"
            filterOption={(input, option) => {
              return option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
            allowClear
            getPopupContainer={getPopupContainer}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          >
            {options.map((ele, index: number) => (
              <Select.Option key={index} value={ele.id}>
                {ele.label}
              </Select.Option>
            ))}
          </Select>
        )}
      </Form.Item>
    </div>
  );
});

export default XSelectOne;
