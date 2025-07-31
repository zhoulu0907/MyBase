import { Upload } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputFileUploadConfig } from "./schema";

const XFileUpload = memo((props: XInputFileUploadConfig) => {
    const {
        label,
        status,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Upload
                drag
                multiple
                action="/"
                onDrop={() => {}}
            />
        </div>
    );
});

export default XFileUpload;
