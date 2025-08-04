import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from '@/components/Materials/constants';
import { Form, Input } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import type { XInputPhoneConfig } from './schema';

const XInputPhone = memo((props: XInputPhoneConfig) => {
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

    const [value, setValue] = useState('');
    const [InputStatus, setInputStatus] = useState<
        undefined | 'error' | 'warning'
    >();

    // 手机号校验正则
    const validateEmail = (email: string) => /^1[3-9]\d{9}$/.test(email);

    useEffect(() => {
        if (value && !validateEmail(value)) {
            setInputStatus('error');
            return;
        }
        setInputStatus(undefined);
    }, [value]);

    return (
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
                opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
                pointerEvents:
                    status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
                margin: '0px',
            }}
        >
            <Input
                status={InputStatus}
                readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
                defaultValue={defaultValue}
                style={{
                    width: '100%',
                    color,
                    textAlign: align,
                    backgroundColor: bgColor,
                }}
                placeholder={placeholder}
                onChange={setValue}
            />
        </Form.Item>
    );
});

export default XInputPhone;
