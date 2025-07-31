import { Select } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputSelectMutipleConfig } from "./schema";

const Option = Select.Option;
const options = ["Beijing", "Shanghai", "Guangzhou", "Disabled"];

const XSelectMutiple = memo((props: XInputSelectMutipleConfig) => {
    const {
        label,
        status,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Select
                mode="multiple"
                placeholder="Select"
                style={{ width: "100%" }}
                defaultValue={["Beijing", "Shenzhen"]}
                allowClear
            >
                {options.map((option, index) => (
                    <Option key={option} disabled={index === 3} value={option}>
                        {option}
                    </Option>
                ))}
            </Select>
        </div>
    );
});

export default XSelectMutiple;
