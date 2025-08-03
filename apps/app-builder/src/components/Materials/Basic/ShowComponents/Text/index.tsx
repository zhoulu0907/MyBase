import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { memo } from "react";
import { type XTextConfig } from "./schema";


const XText = memo((props: XTextConfig) => {
    const {
        status,
        content,
    } = props;

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (

        <div>
            {content}
        </div>

    );
});

export default XText;
