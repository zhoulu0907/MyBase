import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Calendar } from "@arco-design/web-react";
import { memo } from "react";
import { type XCalendarConfig } from "./schema";

const XCalendar = memo((props: XCalendarConfig) => {
    const {
        status,
    } = props;

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (

        <Calendar />

    );
});

export default XCalendar;
