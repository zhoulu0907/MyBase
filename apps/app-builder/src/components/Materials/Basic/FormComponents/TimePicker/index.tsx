import { TimePicker } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputTimePickerConfig } from "./schema";

const XTimePicker = memo((props: XInputTimePickerConfig) => {
    const {
        label,
        status,
        defaultValue,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <TimePicker
                defaultValue={defaultValue}
                style={{ width: "100%" }}
            />
        </div>
    );
});

export default XTimePicker;
