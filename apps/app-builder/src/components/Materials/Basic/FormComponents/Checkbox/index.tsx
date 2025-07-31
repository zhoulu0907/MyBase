import { Checkbox } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputCheckboxConfig } from "./schema";

const XCheckbox = memo((props: XInputCheckboxConfig) => {
    const {
        label,
        status,
        defaultValue,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Checkbox
                checked
                disabled={false}
                defaultChecked={defaultValue === "true"}
            />
        </div>
    );
});

export default XCheckbox;
