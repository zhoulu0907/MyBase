import { memo } from 'react';
import { Input, Form } from '@arco-design/web-react';
import { type XInputTextConfig } from './schema';
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from '@/components/Materials/constants';

const XInputText = memo((props: XInputTextConfig) => {
    const {
        label,
        placeholder,
        tooltip,
        status,
        defaultValue,
        required,
        align,
        layout,
        color,
        bgColor,
        labelColSpan = 0,
    } = props;

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <Form.Item
            label={label}
            layout={layout}
            labelCol={{
                span: labelColSpan,
            }}
            tooltip={tooltip}
            wrapperCol={{ span: 24 - labelColSpan }}
            rules={[{ required }]}
            style={{
                pointerEvents:
                    status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
            }}
        >
            <Input
                readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
                defaultValue={defaultValue}
                placeholder={placeholder}
                style={{
                    width: '100%',
                    textAlign: align,
                    color,
                    backgroundColor: bgColor,
                }}
            />
        </Form.Item>
    );
});

export default XInputText;
