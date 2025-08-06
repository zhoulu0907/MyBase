import { STATUS_OPTIONS, STATUS_VALUES } from "@/components/Materials/constants";
import { DatePicker, Form } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputDateRangePickerConfig } from "./schema";

const XDateRangePicker = memo((props: XInputDateRangePickerConfig) => {
    const { label, status, tooltip, required, layout, labelColSpan = 0, } = props;

    return (
        <Form.Item
            label={label}
            layout={layout}
            tooltip={tooltip}
            labelCol={{
                span: labelColSpan,
            }}
            wrapperCol={{ span: 24 - labelColSpan }}
            rules={[{ required }]}
            style={{
                opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
                pointerEvents:
                    status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                        ? "none"
                        : "unset",
                margin: '0px',
            }}
        >
            <DatePicker.RangePicker style={{ width: "100%" }} />
        </Form.Item>
    );
});

export default XDateRangePicker;
