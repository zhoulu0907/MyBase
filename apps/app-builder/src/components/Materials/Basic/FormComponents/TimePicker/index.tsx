import { memo } from "react";
import { TimePicker, Tooltip, Form } from "@arco-design/web-react";
import type { XInputTimePickerConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const XTimePicker = memo((props: XInputTimePickerConfig) => {
    const {
        label,
        tooltip,
        status,
        defaultValue,
        required,
        layout,
    } = props;

    return (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label}
                layout={layout}
                rules={[{ required }]}
                style={{
                    opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
                    pointerEvents:
                        status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                            ? "none"
                            : "unset",
                }}
            >
                <TimePicker
                    defaultValue={defaultValue}
                    style={{ width: "100%" }}
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XTimePicker;
