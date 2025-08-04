import { memo } from "react";
import { Radio, Tooltip, Form } from "@arco-design/web-react";
import type { XInputRadioConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const RadioGroup = Radio.Group;
const XRadio = memo((props: XInputRadioConfig) => {
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
                }}
            >
                <RadioGroup options={defaultValue} />
                
            </Form.Item>
        </Tooltip>
    );
});

export default XRadio;
