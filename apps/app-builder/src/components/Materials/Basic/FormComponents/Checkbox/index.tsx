import { STATUS_OPTIONS, STATUS_VALUES } from "@/components/Materials/constants";
import { Checkbox, Form, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputCheckboxConfig } from "./schema";

const XCheckbox = memo((props: XInputCheckboxConfig) => {
    const { label, tooltip, status, defaultValue, required, layout } = props;

    return (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label}
                layout={layout}
                rules={[{ required }]}
                style={{
                    margin: 0,
                    opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
                }}
            >
                <Checkbox
                    defaultChecked={defaultValue === "true"}
                    style={{
                        pointerEvents:
                            status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                                ? "none"
                                : "unset",
                    }}
                >开启</Checkbox>
            </Form.Item>
        </Tooltip>
    );
});

export default XCheckbox;
