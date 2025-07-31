import { DatePicker } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputDatePickerConfig } from "./schema";

const XDatePicker = memo((props: XInputDatePickerConfig) => {
    const {
        label,
        status,
    } = props;
    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <DatePicker
                style={{ width: "100%" }}
            />
        </div>
    );
});

export default XDatePicker;
