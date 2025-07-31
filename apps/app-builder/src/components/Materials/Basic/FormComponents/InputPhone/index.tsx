import { Input, Tooltip } from "@arco-design/web-react";
import { memo, useEffect, useState } from "react";
import type { XInputPhoneConfig } from "./schema";

const XInputPhone = memo((props: XInputPhoneConfig) => {
    const {
        label,
        placeholder,
        tooltip,
        status,
        defaultValue,
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

    return status === "hidden" ? null : (
        <Tooltip content={tooltip}>
            <div>
                <div>{label}</div>
                <Input
                    status={InputStatus}
                    readOnly={status === "readonly"}
                    defaultValue={defaultValue}
                    style={{ width: "100%" }}
                    placeholder={placeholder}
                    onChange={setValue}
                />
            </div>
        </Tooltip>
    );
});

export default XInputPhone;
