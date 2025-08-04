import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { DatePicker, Form, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputDateTimePickerConfig } from "./schema";

const XDateTimePicker = memo((props: XInputDateTimePickerConfig) => {
    const {
        label,
        tooltip,
        status,
        defaultValue,
        required,
        layout,
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
                    margin: '0px',
                }}
            >
                <DatePicker
                    showTime
                    defaultValue={defaultValue}
                    style={{ width: "100%" }}
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XDateTimePicker;
