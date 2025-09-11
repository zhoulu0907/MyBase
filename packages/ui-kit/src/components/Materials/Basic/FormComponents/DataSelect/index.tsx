import { memo } from 'react'
import { Button, Form } from '@arco-design/web-react';

import { XDataSelectConfig } from './schema'
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';

const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean }) => {
    const { label, dataField, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description, runtime } = props;
    return (
        <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : ""}
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
    )
})

export default XDataSelect