import { memo } from "react";
import { Checkbox, Tooltip, Form } from "@arco-design/web-react";
import type { XInputCheckboxConfig } from "./schema";
import { STATUS_VALUES, STATUS_OPTIONS } from "@/components/Materials/constants";

const XCheckbox = memo((props: XInputCheckboxConfig) => {
    const { label, tooltip, status, defaultValue, required, layout, saveWithHidden } = props;

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <Tooltip content={tooltip}>
            <Form.Item label={label} layout={layout} rules={[{ required }]}>
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
