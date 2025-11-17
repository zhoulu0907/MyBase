import { Cell, Checkbox, Dropdown } from '@arco-design/mobile-react';
import { memo, useEffect, useState } from 'react';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
// import { nanoid } from 'nanoid';
import type { XInputSelectMutipleConfig } from './schema';
import '../index.css';
import './index.css';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    labelColSpan = 0,
    showSearch,
    defaultOptions,
    runtime = true,
    detailMode
  } = props;

  const [fieldId, setFieldId] = useState('');
  const [showDropdown, setShowDropdown] = useState(false);
  const [selected, setSelected] = useState<string[]>([]);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  const options = defaultOptions?.map(({ label, value }: { label: string; value: string | number }) => ({ label, value }));

  return (
    <div className="inputTextWrapper">
      <Cell
        label={label.display && label.text}
        showArrow
        onClick={() => { setShowDropdown(true); }}
        children={selected.map((key) => options.find((o) => o.value === key)?.label).join(', ') || '请选择'}
        style={{
          width: '100%',
          pointerEvents: runtime ? 'unset' : 'none'
        }}
      />
      <Dropdown
        useColumn={3}
        multiple={true}
        showDropdown={showDropdown}
        onCancel={() => setShowDropdown(false)}
      >
        <Checkbox.Group
          className="selectCheckout"
          layout='block'
          defaultValue={selected}
          options={options}
          onChange={(value: any[]) => {
            setSelected(value);
          }}
        />
      </Dropdown>

      {/* <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_ONE}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }, { maxLength: verify?.maxChecked }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Space wrap>
            {fieldValue && defaultValue && fieldValue.map((ele: any, index: number) => <Tag key={index}>
              {defaultValue.find((e: any) => e.value === ele)?.label}
            </Tag>)}
          </Space>
        ) : (
          <Select
            mode="multiple"
            allowClear
            showSearch={showSearch}
            filterOption={(input, option) => {
              return option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
            placeholder="请选择"
            options={defaultValue}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XSelectMutiple;
