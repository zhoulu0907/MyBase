import { memo } from 'react'
import { nanoid } from 'nanoid';
import { Button, Form} from '@arco-design/web-react';

import { XDataSelectConfig } from './schema'
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';

const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean }) => {
    const { label, dataField, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description, runtime, displayFields } = props;
const fields = [
  { label: "单行文本", value: "" },
  { label: "图片", value: "" },
  { label: "单选按钮组", value: "" },
  { label: "提交人", value: "" },
  { label: "提交时间", value: "" },
  { label: "更新时间", value: "" },
];
    return (
        <div className='formWrapper'>
            <Form.Item
                label={label.display && label.text}
                field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATA_SELECT}_${nanoid()}`}
                layout={layout}
                tooltip={tooltip}
                labelCol={{
                    style: { width: labelColSpan, flex: "unset" },
                }}
                wrapperCol={{ style: { flex: 1 } }}
                rules={[{ required: verify?.required }]}
                hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
                style={{
                    opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
                    pointerEvents:
                    status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? "none" : "unset",
                    margin: "0px",
            }}
            >
                <Button type="secondary" long>
                    {defaultValue}
                </Button>
            </Form.Item>
            <div style={{marginTop: "16px", background: "#f7f8fa",}}>
                {displayFields.map((field) => (
                    <Form.Item label={field.label} 
                        labelCol={{style: { width: labelColSpan, flex: "unset" }}}
                        wrapperCol={{ style: { flex: 1 } }}>
                        <span style={{ color: "#c9cdd4"}}>暂无内容</span>
                    </Form.Item>))}
            </div>
        </div>
    )
})

export default XDataSelect