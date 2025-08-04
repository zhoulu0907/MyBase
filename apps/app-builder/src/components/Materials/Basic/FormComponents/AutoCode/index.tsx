import { memo } from 'react';
import { Input, Form } from '@arco-design/web-react';
import type { XInputAutoCodeConfig } from './schema';
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from '@/components/Materials/constants';

const XAutoCode = memo((props: XInputAutoCodeConfig) => {
    const {
        label,
        tooltip,
        status,
        required,
        align,
        layout,
        color,
        bgColor,
        labelColSpan = 0,
    } = props;

    const defaultValue = crypto.randomUUID();

    return (
        <Form.Item
            label={label}
            layout={layout}
            rules={[{ required }]}
            labelCol={{
                span: labelColSpan,
            }}
            tooltip={tooltip}
            wrapperCol={{ span: 24 - labelColSpan }}
            style={{
                opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
            }}
        >
            <Input
                readOnly={true}
                defaultValue={defaultValue}
                style={{
                    width: '100%',
                    color,
                    textAlign: align,
                    backgroundColor: bgColor,
                }}
            />
        </Form.Item>
    );
});

export default XAutoCode;
