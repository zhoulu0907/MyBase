import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, Input, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import { v4 as uuidv4 } from 'uuid';
import type { XInputAutoCodeConfig } from "./schema";

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

    const defaultValue = uuidv4();

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label} layout={layout} rules={[{ required }]}
                style={{
                    margin: '0px',
                }}
            >
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
