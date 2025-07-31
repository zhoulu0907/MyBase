import { InputNumber, Tooltip, Form } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputNumberConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const XInputNumber = memo((props: XInputNumberConfig) => {
    const {
        label,
        placeholder,
        tooltip,
        status,
        defaultValue,
        required,
        align,
        min,
        max,
        step,
        precision,
        layout,
        color,
        bgColor,
        saveWithHidden,
    } = props;

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label}
                layout={layout}
                rules={[
                    {
                        required,
                        type: "number",
                        min,
                        max,
                    },
                ]}
                style={{
                    pointerEvents:
                        status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                            ? "none"
                            : "unset",
                }}
            >
                <InputNumber
                    readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
                    defaultValue={defaultValue}
                    placeholder={placeholder}
                    step={step}
                    min={min}
                    max={max}
                    precision={precision}
                    style={{
                        width: "100%",
                        textAlignLast: align,
                    }}
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XInputNumber;
