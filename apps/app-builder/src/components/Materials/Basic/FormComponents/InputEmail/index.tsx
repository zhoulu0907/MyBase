import { memo, useEffect, useState } from "react";
import { Input, Tooltip, Form } from "@arco-design/web-react";
import type { XInputEmailConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const XInputEmail = memo((props: XInputEmailConfig) => {
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
        saveWithHidden,
    } = props;

    const [value, setValue] = useState("");
    const [InputStatus, setInputStatus] = useState<
        undefined | "error" | "warning"
    >();

    // 邮箱校验正则
    const validateEmail = (email: string) =>
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

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
                rules={[
                    { required },
                    // { type: "email", message: "请输入合法的邮件地址" },
                    // {
                    //     validator: (value) => {
                    //         if (!value) return true;
                    //         const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    //         return regex.test(value);
                    //     },
                    // },
                ]}
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

export default XInputEmail;
