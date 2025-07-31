import { memo } from "react";
import { DatePicker, Tooltip, Form } from "@arco-design/web-react";
import type { XInputDateRangePickerConfig } from "./schema";
import { STATUS_VALUES, STATUS_OPTIONS } from "@/components/Materials/constants";

const XDateRangePicker = memo((props: XInputDateRangePickerConfig) => {
    const { label, status, tooltip, required, defaultValue, layout, saveWithHidden } = props;

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
                <DatePicker.RangePicker style={{ width: "100%" }} />
            </Form.Item>
        </Tooltip>
    );
});

export default XDateRangePicker;
