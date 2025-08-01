import { memo } from "react";
import { Select, Tooltip, Form } from "@arco-design/web-react";
import type { XInputSelectOneConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const Option = Select.Option;
const options = ["Beijing", "Shanghai", "Guangzhou", "Disabled"];

const XSelectOne = memo((props: XInputSelectOneConfig) => {
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
                <Select
                    placeholder="Select"
                    style={{ width: "100%" }}
                    allowClear
                >
                    {options.map((option, index) => (
                        <Option
                            key={option}
                            disabled={index === 3}
                            value={option}
                        >
                            {option}
                        </Option>
                    ))}
                </Select>
            </Form.Item>
        </Tooltip>
    );
});

export default XSelectOne;
