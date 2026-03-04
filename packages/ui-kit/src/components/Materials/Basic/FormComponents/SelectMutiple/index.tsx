import { menuDictSignal, useAppEntityStore } from '@/signals';
import { getFieldOptionsConfig, getPopupContainer } from '@/utils';
import { Form, Select, Space, Tag } from '@arco-design/web-react';
import type { DictData } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputSelectMutipleConfig } from './schema';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean; tooltipPosition: any; }) => {
  const { label, dataField, tooltip, tooltipPosition, status, verify, layout, runtime = true, detailMode } = props;
  const { mainEntity, subEntities } = useAppEntityStore();

  const { appDict } = menuDictSignal;

  const { form } = Form.useFormContext();
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_MUTIPLE}_${nanoid()}`;
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
        tooltip={ tooltip && {
          content: tooltip,
          position: tooltipPosition
        }}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[
          { required: verify?.required || verify?.checkedLimit && !!verify?.minChecked, message: `${label.text}是必填项` },
          {
            minLength: verify?.checkedLimit ? verify?.minChecked : undefined,
            maxLength: verify?.checkedLimit ? verify?.maxChecked : undefined
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Space wrap size={[4, 4]}>
            {fieldValue &&
              Array.isArray(fieldValue) &&
              fieldValue.map((ele: any, index: number) => (
                <Tag key={index} style={{ marginBottom: '0' }}>
                  {ele?.name || options.find((e) => e.value === ele || e.value === ele?.id || e.id === ele?.id || e.id === ele)?.label || '--'}
                </Tag>
              ))}
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

export default XSelectMutiple;
