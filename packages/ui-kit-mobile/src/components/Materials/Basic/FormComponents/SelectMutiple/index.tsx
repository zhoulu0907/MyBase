import { Cell, Picker } from '@arco-design/mobile-react';
import { memo, useEffect, useState } from 'react';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
// import { nanoid } from 'nanoid';
import '../index.css';
import type { XInputSelectMutipleConfig } from './schema';

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
    defaultValue,
    runtime = true,
    detailMode
  } = props;

  // const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');
  const [multipleVisible, setMultipleVisible] = useState(false);

  // const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  const getPopupContainer = (node?: HTMLElement): HTMLElement => {
    return (
      (node?.closest('.arco-form-item') as HTMLElement) ||
      node?.parentNode as HTMLElement ||
      document.body
    );
  };

  return (
    <div className="formWrapper">
      <Cell
        label={label.display && label.text}
        showArrow
        // onClick={() => {setMultipleVisible(true);}}
      />
      <Picker
        data={defaultValue}
        visible={multipleVisible}
        getContainer={getPopupContainer}
        cascade={false}
        maskClosable={true}
        contentStyle={{
          width: '100%',
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        onHide={() => setMultipleVisible(false)}
        onOk={(val, data) => {
          setMultipleVisible(false);
        }}
        onPickerChange={(value, index, data) => {
            console.info('-----demo onPickerChange', value, index, data);
        }}
      />
          
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
            getPopupContainer={getPopupContainer}
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
