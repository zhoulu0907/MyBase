import { Select } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputSelectOneConfig } from "./schema";

const Option = Select.Option;
const options = ["Beijing", "Shanghai", "Guangzhou", "Disabled"];

const XSelectOne = memo((props: XInputSelectOneConfig) => {
    const {
        label,
        status,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Select placeholder="Select" style={{ width: "100%" }} allowClear>
                {options.map((option, index) => (
                    <Option key={option} disabled={index === 3} value={option}>
                        {option}
                    </Option>
                ))}
            </Select>
        </div>
    );
});

export default XSelectOne;
