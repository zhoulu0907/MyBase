import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, Select, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputSelectOneConfig } from "./schema";

const Option = Select.Option;
const options = ["Beijing", "Shanghai", "Guangzhou", "Disabled"];

const XSelectOne = memo((props: XInputSelectOneConfig) => {
    const {
        label,
        tooltip,
        status,
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
