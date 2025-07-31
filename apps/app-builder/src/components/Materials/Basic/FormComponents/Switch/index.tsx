import { memo } from "react";
import { Switch, Tooltip, Form } from "@arco-design/web-react";
import type { XInputSwitchConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const XSwitch = memo((props: XInputSwitchConfig) => {
    const {
        label,
        tooltip,
        status,
        defaultValue,
        required,
        layout,
        saveWithHidden,
    } = props;

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
                <Switch defaultChecked={defaultValue === "true"} />
            </Form.Item>
        </Tooltip>
    );
});

export default XSwitch;
