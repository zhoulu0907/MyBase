import { memo } from "react";
import { Input, Tooltip, Form } from "@arco-design/web-react";
import type { XInputAutoCodeConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const XAutoCode = memo((props: XInputAutoCodeConfig) => {
    const {
        label,
        tooltip,
        status,
        required,
        align,
        layout,
        color,
        bgColor,
    } = props;

    const defaultValue = crypto.randomUUID();

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <Tooltip content={tooltip}>
            <Form.Item label={label} layout={layout} rules={[{ required }]}>
                <Input
                    readOnly={true}
                    defaultValue={defaultValue}
                    style={{
                        width: "100%",
                        textAlign: align,
                        color,
                        backgroundColor: bgColor,
                    }}
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XAutoCode;
