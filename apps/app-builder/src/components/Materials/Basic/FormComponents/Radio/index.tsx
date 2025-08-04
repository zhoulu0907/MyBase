import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, Radio, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputRadioConfig } from "./schema";

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
                    margin: '0px',
                }}
            >
                <RadioGroup options={defaultValue} />

            </Form.Item>
        </Tooltip>
    );
});

export default XRadio;
