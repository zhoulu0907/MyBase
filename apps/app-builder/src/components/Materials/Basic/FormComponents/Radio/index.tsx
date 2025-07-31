import { Radio } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputRadioConfig } from "./schema";

const XRadio = memo((props: XInputRadioConfig) => {
    const {
        label,
        status,
        defaultValue,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Radio
                checked
                disabled={false}
                defaultChecked={defaultValue === "true"}
            />
        </div>
    );
});

export default XRadio;
