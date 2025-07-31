import { Input, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import styles from './index.module.less';
import { type XInputTextConfig } from "./schema";

const XInputText = memo((props: XInputTextConfig) => {
    const {
        label,
        placeholder,
        tooltip,
        status,
        defaultValue,
    } = props;
    return status === "hidden" ? null : (
        <Tooltip content={tooltip}
        >
            <div className={styles.XInput}>
                <div className={styles.label}
                    hidden={!label}
                >
                    {label}
                </div>
                <Input
                    className={styles.input}
                    defaultValue={defaultValue}
                    placeholder={placeholder}
                />
            </div>
        </Tooltip>
    );
});

export default XInputText;
