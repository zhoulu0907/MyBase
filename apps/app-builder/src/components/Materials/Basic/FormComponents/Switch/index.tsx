import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, Switch, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputSwitchConfig } from "./schema";

const XSwitch = memo((props: XInputSwitchConfig) => {
    const {
        label,
        tooltip,
        status,
        defaultValue,
        required,
        layout,
    } = props;

    return (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label}
                layout={layout}
                rules={[{ required }]}
                style={{
                    opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
                    pointerEvents:
                        status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                            ? "none"
                            : "unset",
                    margin: '0px',
                }}
            >
                <Switch defaultChecked={defaultValue === "true"} />
            </Form.Item>
        </Tooltip>
    );
});

export default XSwitch;
