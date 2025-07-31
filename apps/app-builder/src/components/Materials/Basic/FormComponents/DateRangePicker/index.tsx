import { DatePicker } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputDateRangePickerConfig } from "./schema";

const XDateRangePicker = memo((props: XInputDateRangePickerConfig) => {
    const {
        label,
        status,
    } = props;
    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <DatePicker.RangePicker
                style={{ width: "100%" }}
            />
        </div>
    );
});

export default XDateRangePicker;
