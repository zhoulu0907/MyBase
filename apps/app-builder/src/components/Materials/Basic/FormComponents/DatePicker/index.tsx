import { memo } from "react";
import { DatePicker, Tooltip, Form } from "@arco-design/web-react";
import type { XInputDatePickerConfig } from "./schema";
import { DATE_OPTIONS, DATE_VALUES, STATUS_VALUES, STATUS_OPTIONS } from "@/components/Materials/constants";

const { YearPicker, MonthPicker } = DatePicker;
const XDatePicker = memo((props: XInputDatePickerConfig) => {
    const { label, tooltip, status, defaultValue, required, dateType, layout, saveWithHidden } =
        props;

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
                {dateType === DATE_VALUES[DATE_OPTIONS.ONLY_YEAR] && (
                    <YearPicker style={{ width: "100%" }} />
                )}
                {dateType === DATE_VALUES[DATE_OPTIONS.ONLY_MONTH] && (
                    <MonthPicker style={{ width: "100%" }} />
                )}
                {dateType === DATE_VALUES[DATE_OPTIONS.ONLY_DATE] && (
                    <DatePicker style={{ width: "100%" }} />
                )}
                {dateType === DATE_VALUES[DATE_OPTIONS.FULL] && (
                    <DatePicker showTime style={{ width: "100%" }} />
                )}
            </Form.Item>
        </Tooltip>
    );
});

export default XDatePicker;
