import { Form, Select, Space, Tag } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import type { XInputSelectMutipleConfig } from './schema';
import { getPopupContainer, getFieldOptionsConfig } from '@/utils';
import { useAppEntityStore } from '@/signals';
import type { DictData } from '@onebase/platform-center';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    runtime = true,
    detailMode
  } = props;
  const { mainEntity, subEntities } = useAppEntityStore();

  const { form } = Form.useFormContext();
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_MUTIPLE}_${nanoid()}`;
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
        rules={[
          { required: verify?.required, message: `${label.text}是必填项` },
          { maxLength: verify?.maxChecked }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Space wrap size={[4, 4]}>
            {fieldValue && Array.isArray(fieldValue) && fieldValue.map((ele: any, index: number) => <Tag key={index} style={{ marginBottom: '0' }}>
              {ele?.name || options.find((e => e.id === ele || e.id === ele?.id))?.label || '--'}
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
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          >
            {options.map((ele, index: number) => (<Select.Option key={index} value={ele.id}>{ele.label}</Select.Option>))}
          </Select>
        )}
      </Form.Item>
    </div>
  );
});

export default XSelectMutiple;
