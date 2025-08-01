import { memo, useEffect, useState } from "react";
import { Input, Tooltip, Form } from "@arco-design/web-react";
import type { XInputPhoneConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

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
    } = props;

    const [value, setValue] = useState("");
    const [InputStatus, setInputStatus] = useState<
        undefined | "error" | "warning"
    >();

    // 手机号校验正则
    const validateEmail = (email: string) => /^1[3-9]\d{9}$/.test(email);

    useEffect(() => {
        if (value && !validateEmail(value)) {
            setInputStatus("error");
            return;
        }
        setInputStatus(undefined);
    }, [value]);

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label}
                layout={layout}
                rules={[{ required }]}
                style={{
                    pointerEvents:
                        status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                            ? "none"
                            : "unset",
                }}
            >
                <Input
                    status={InputStatus}
                    readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
                    defaultValue={defaultValue}
                    style={{
                        width: "100%",
                        textAlign: align,
                        color,
                        backgroundColor: bgColor,
                    }}
                    placeholder={placeholder}
                    onChange={setValue}
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XInputPhone;
