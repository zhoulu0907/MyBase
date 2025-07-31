import { Switch } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputSwitchConfig } from "./schema";

const XSwitch = memo((props: XInputSwitchConfig) => {
    const {
        label,
        status,
        defaultValue,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Switch
                defaultChecked={defaultValue === "true"}
            />
        </div>
    );
});

export default XSwitch;
