import { memo } from "react";
import { Input, Tooltip, Form } from "@arco-design/web-react";
import type { XInputTextAreaConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const TextArea = Input.TextArea;

const XInputTextArea = memo((props: XInputTextAreaConfig) => {
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
                <TextArea
                    readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
                    defaultValue={defaultValue}
                    placeholder={placeholder}
                    style={{
                        width: "100%",
                        textAlign: align,
                        color,
                        backgroundColor: bgColor,
                    }}
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XInputTextArea;
