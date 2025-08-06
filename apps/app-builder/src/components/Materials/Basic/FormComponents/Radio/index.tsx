import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, Radio } from "@arco-design/web-react";
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
        labelColSpan = 0,
    } = props;

    return (
        <Form.Item
            label={label}
            layout={layout}
            tooltip={tooltip}
            labelCol={{
                span: labelColSpan,
            }}
            wrapperCol={{ span: 24 - labelColSpan }}
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
            <RadioGroup options={defaultValue} />

        </Form.Item>
    );
});

export default XRadio;
