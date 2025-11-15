import { Button, Form, Modal } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import PreviewDataSelectModal from './previewDataSelectModal';
import { XDataSelectConfig } from './schema';
import cloneDeep from 'lodash-es/cloneDeep';
import { IconClose } from '@arco-design/web-react/icon';

const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    labelColSpan = 0,
    description,
    runtime,
    displayFields,
    detailMode
  } = props;

  const {form} = Form.useFormContext();
  const fieldName = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATA_SELECT}_${props.id}`;

  const [previewDataSelectVisible, setPreviewDataSelectVisible] = useState(false); //预览数据选择popup
  const [initialSelectedId, setInitialSelectedId] = useState<string>('');
  const [displayFieldsWithValue, setdisplayFieldsWithValue] = useState<any[]>([]);

  // 字段回显
  const [selectFieldValue, setSelectFieldValue] = useState<string>('');

  const fieldValue = Form.useWatch(fieldName, form);

  useEffect(() => {
    if (runtime === true && fieldValue) {
      setInitialSelectedId(fieldValue.selectID);
      setSelectFieldValue(fieldValue.displayValue);
      // const map = new Map<string, string>();
      // for (const s of fieldValue.dataFields) {
      //     if (s.value != null && s.dataValue !== undefined) map.set(s.value, s.dataValue);
      // }
      // const displayFieldsWithValue = [...displayFields].map(t => ({
      //     ...t,
      //     dataValue: map.has(t.value) ? map.get(t.value) : t.dataValue,
      // }));
      // setdisplayFieldsWithValue(displayFieldsWithValue);
    } else {
      resetDisplayValue();
      setdisplayFieldsWithValue(cloneDeep(displayFields));
    }
  }, [fieldValue]);

  useEffect(() => {
    setdisplayFieldsWithValue(cloneDeep(displayFields));
    resetDisplayValue();
  }, [displayFields])

  const handleClear = (e: React.MouseEvent) => {
    // 阻止冒泡，避免触发按钮的 onClick（打开弹窗）
    e.stopPropagation();
    resetDisplayValue();
  };

  const resetDisplayValue = () => {
    setInitialSelectedId('');
    setSelectFieldValue('');
  }

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldName}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
          pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
          margin: '0px'
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode || !runtime ? 
        (
          <Button
          type="secondary"
          long
          style={{
              pointerEvents: runtime ? 'unset' : 'none',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}
        >
          {selectFieldValue || defaultValue}
        </Button>
        ) : 
        (
          (initialSelectedId && selectFieldValue) ? (
            <Button
              type="secondary" long
              style={{
                pointerEvents: runtime ? 'unset' : 'none',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}
              onClick={() => setPreviewDataSelectVisible(true)}
            >
            <span style={{ pointerEvents: 'none' }}>{selectFieldValue || <span style={{ color: '#999' }}>{defaultValue}</span>}</span>
            <span
              onClick={handleClear}
              style={{
                cursor:'pointer'
              }}
              title="清除"
            >
            <IconClose style={{ fontSize: 12}} />
            </span>
        </Button>
          ) : (
          <Button
            type="secondary"
            long
            style={{
              pointerEvents: runtime ? 'unset' : 'none',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}
            onClick={() => setPreviewDataSelectVisible(true)}
          >
            {defaultValue}
          </Button>
        )
      )}
      </Form.Item>
      <PreviewDataSelectModal
          visible={previewDataSelectVisible}
          onCancel={() => setPreviewDataSelectVisible(false)}
          tableConfig={props.dynamicTableConfig}
          displayFields={displayFieldsWithValue}
          form={form}
          fieldName={fieldName}
          initialSelectedId = {initialSelectedId}
        />
      <div style={{ marginTop: '16px', background: '#f7f8fa' }}>
        {/* {displayFieldsWithValue.map((field) => (
          <Form.Item
            key={field.label}
            label={field.label}
            labelCol={{ style: { width: labelColSpan, flex: 'unset' } }}
            wrapperCol={{ style: { flex: 1 } }}
          >
            <span style={{ color: '#c9cdd4' }}>{field.dataValue ?? '暂无内容'}</span>
          </Form.Item>
        ))} */}
      </div>
    </div>
  );
});

export default XDataSelect;
