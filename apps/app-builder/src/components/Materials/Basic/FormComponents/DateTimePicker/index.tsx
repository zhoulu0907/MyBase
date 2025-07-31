import { DatePicker } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputDateTimePickerConfig } from "./schema";

const XDateTimePicker = memo((props: XInputDateTimePickerConfig) => {
    const {
        label,
        status,
        placeholder,
        defaultValue,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <DatePicker
                showTime
                placeholder={placeholder}
                defaultValue={defaultValue}
                style={{ width: "100%" }}
            />
        </div>
    );
});

export default XDateTimePicker;
