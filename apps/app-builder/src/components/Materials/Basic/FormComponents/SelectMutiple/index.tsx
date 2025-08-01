import { memo } from "react";
import { Select, Tooltip, Form } from "@arco-design/web-react";
import type { XInputSelectMutipleConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

const Option = Select.Option;
const options = ["Beijing", "Shanghai", "Guangzhou", "Disabled"];

const XSelectMutiple = memo((props: XInputSelectMutipleConfig) => {
    const {
        label,
        tooltip,
        status,
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
                }}
            >
                <Select
                    mode="multiple"
                    placeholder="Select"
                    style={{ width: "100%" }}
                    defaultValue={["Beijing", "Shenzhen"]}
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

export default XSelectMutiple;
