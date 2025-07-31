import { InputNumber, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputNumberConfig } from "./schema";

const XInputNumber = memo((props: XInputNumberConfig) => {
    const {
        label,
        placeholder,
        tooltip,
        status,
        defaultValue,
    } = props;
    return status === "hidden" ? null : (
        <Tooltip content={tooltip}>
            <div>
                <div>{label}</div>
                <InputNumber
                    readOnly={status === "readonly"}
                    defaultValue={defaultValue}
                    style={{ width: "100%" }}
                    placeholder={placeholder}
                    // TODO(mickey): 加入配置中
                    // min={0}
                    // max={15}
                />
            </div>
        </Tooltip>
    );
});

export default XInputNumber;
