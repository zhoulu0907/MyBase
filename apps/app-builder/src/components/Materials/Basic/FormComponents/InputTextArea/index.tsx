import { Input, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputTextAreaConfig } from "./schema";

const TextArea = Input.TextArea;

const XInputTextArea = memo((props: XInputTextAreaConfig) => {
    const {
        label,
        placeholder,
        tooltip,
        status,
        defaultValue,
    } = props;
    return status === "hidden" ? null : (
        <Tooltip content={tooltip}>
            <div>
                <div>{label}</div>
                <TextArea
                    readOnly={status === "readonly"}
                    defaultValue={defaultValue}
                    style={{ width: "100%" }}
                    placeholder={placeholder}
                />
            </div>
        </Tooltip>
    );
});

export default XInputTextArea;
