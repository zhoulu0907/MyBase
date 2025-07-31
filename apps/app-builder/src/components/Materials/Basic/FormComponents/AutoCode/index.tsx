import { Input, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputAutoCodeConfig } from "./schema";

const XAutoCode = memo((props: XInputAutoCodeConfig) => {
    const {
        label,
        tooltip,
        status,
    } = props;

    const defaultValue = crypto.randomUUID();

    return status === "hidden" ? null : (
        <Tooltip content={tooltip}>
            <div>
                <div>{label}</div>
                <Input
                    readOnly={true}
                    defaultValue={defaultValue}
                    style={{ width: "100%" }}
                />
            </div>
        </Tooltip>
    );
});

export default XAutoCode;
